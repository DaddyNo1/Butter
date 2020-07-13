package com.daddyno1.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.android.builder.model.SourceProvider
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.concurrent.atomic.AtomicBoolean

class ButterPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println "${project.name}: ButterPlugin - start"

        project.afterEvaluate {
            /**
             *  处理 app 和 lib 的插件
             */
            def android = project.extensions.findByType(AppExtension.class)
            def lib = project.extensions.findByType(LibraryExtension.class)

            project.plugins.all {Plugin plugin ->
                if(plugin instanceof AppPlugin){        //com.android.application
                    //本项目包含 app 插件
                    generateR2(project, android.applicationVariants)
                }else if(plugin instanceof LibraryPlugin){      //com.android.library
                    //本项目包含 lib 插件
                    generateR2(project, lib.libraryVariants)
                }
            }

        }
    }

    /**
     * 处理生成R2 文件
     * @param project  项目
     * @param variants 项目对应的变种
     */
    def generateR2(Project project, DomainObjectSet<BaseVariant> variants){
        variants.all {BaseVariant variant ->
            //定义R2输出路径。注意生成的R2文件如何可以在build过程中被引入，这是个问题。
            def outputDir = project.getBuildDir().path + "/generated/source/r2/${variant.dirName}"

            def pkg = getPackage(variant);
            def once = new AtomicBoolean()

            variant.outputs.all { BaseVariantOutput output ->

                if(once.compareAndSet(false, true)){
                    ProcessAndroidResources processResourcesTask = output.processResourcesProvider.get()        // processResources 任务

                    def textSymbolOutputFile
                    if(processResourcesTask instanceof GenerateLibraryRFileTask){
                        textSymbolOutputFile = processResourcesTask.textSymbolOutputFile
                    }else if(processResourcesTask instanceof LinkApplicationAndroidResourcesTask){
                        textSymbolOutputFile = processResourcesTask.textSymbolOutputFile
                    }


                    /**
                     * 第一种方式： 把 复制 R 文件的逻辑插入构建流程 processResources 的最后，这样就可以处理生成R 文件了。这种方式有点小问题：
                     *
                     *   build时报错： 程序包R2不存在 @BindS tring (R2.string.app_ame) String appName;
                     *                                           ^
                     *
                     *                问题1：这个输出路径不知道为什么不能被src下的原码访问到，或者编译不通过，而 butterKnife中生成的R2可以编译通过你？
                     *                AndroidStudio 自己生成的 BuildConfig 为什么可以参与编译打包呢？
                     *
                     *                原因是：variant.registerJavaGeneratingTask(@NonNull Task task, @NonNull File... sourceFolders);
                     *                        This will make the generate[Variant]Sources task depend on this task and add the
                     *                        new source folders as compilation inputs.
                     *                生成的Java文件会作为 inputs 给到下一个任务。所以这些 生成的 .java 文件也会参与任务。
                     *
                     *
                     */
//                    def rFile = project.files(textSymbolOutputFile)
//                    //创建生成
//                    def task = project.tasks.create("Generate${variant.name.capitalize()}R2", GenerateR2Task.class)
//                    task.outputDir = new File(outputDir)
//                    task.pkg = pkg
//                    task.rFile = rFile
//                    processResourcesTask.doLast{
//                        task.doAction()
//                    }

                    /**
                     * 主要得理解 build 构建过程中各个任务是干嘛用的。
                     * generate[Variant]Sources  dependOn GenerateR2Task; GenerateR2Task dependOn  processResources.
                     *
                     *  第二种方式：把我们定义的任务挂载到 构建任务中去。 但是这里有个问题，GenerateR2Task的执行不会跟 processResourcesTask 建立联系，
                     *  所以第一次 获取到的 rFile 是不存在的，怎么处理呢？ 注意 project.files的方法有一段注释： The returned file collection maintains
                     *  the details of the tasks that produce the files, so that these tasks are executed if this file collection is used as an
                     *  input to some task.
                     *  大概意思是：只要让这些文件作为 某些Task的input，这样某些Task就会在 生成这些文件的任务之后执行了。本例来说，找到了processResourcesTask 和
                     *  GenerateR2Task 关联的方法：input
                     *
                     */
                    //获取根据  processResourcesTask 生成的 textSymbolOutputFile
                    def rFile = project.files(textSymbolOutputFile).builtBy(processResourcesTask)

                    //创建生成
                    def task = project.tasks.create("Generate${variant.name.capitalize()}R2", GenerateR2Task.class){GenerateR2Task task ->
                        task.outputDir = new File(outputDir)
                        task.pkg = pkg
                        task.rFile = rFile
                        task.setInputAndOutput()        //让 GenerateR2Task 和 processResources 建立联系
                    }

                    /**
                     * 把新创建的任务挂载到 Gradle 构建任务中：
                     * 给variant 添加一个生成 Java 代码的任务。This will make the generate[Variant]Sources task depend on this task and add the
                     *  new source folders as compilation inputs.
                     *
                     *  本质是让 我们的任务 GenerateR2Task 的输出，能够作为  generate[Variant]Sources 的输入。这样通过输入输出关系，就可以很好地把 GenerateR2Task
                     *  放到 build 构建过程中去
                     */
                    variant.registerJavaGeneratingTask(task, task.outputDir)
                }
            }
        }

    }

    /**
     * 返回此 变体下 packageName 的名字
     * @param variant
     */
    def getPackage(BaseVariant variant){
        def xmlSlurper = new XmlSlurper()
        def list = variant.sourceSets.collect {
            it.manifestFile
        }// 所有 manifestFile 列表
        def res = xmlSlurper.parse(list[0])
        res.@package
    }
}