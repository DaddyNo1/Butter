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
            //定义R2输出路径
            def outputDir = project.getBuildDir().path + "/generated/source/R2/${variant.dirName}"
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
                     * 第一种方式： 把 复制 R文件的逻辑插入构建流程 processResources 之后会生成，插入这个任务之后，
                     * 这种方式不用定义任务。
                     */
                    def rFile = project.files(textSymbolOutputFile)
                    //创建生成
                    def task = project.tasks.create("Generate${variant.name.capitalize()}R2", GenerateR2Task.class)
                    task.outputDir = new File(outputDir)
                    task.pkg = pkg
                    task.rFile = rFile
                    processResourcesTask.doLast{
                        task.doAction()
                    }

                    /**
                     *  第二种方式：把我们定义的任务挂载到 构建任务中去。
                     *  ????????????? 但是这里有个问题，GenerateR2Task的执行不会跟  processResourcesTask 建立联系，
                     *  所以获取到的 rFile 是不存在的。待研究。。。。。。。。。。。
                     */
                    //获取根据  processResourcesTask 生成的 textSymbolOutputFile
//                    def rFile = project.files(textSymbolOutputFile).builtBy(processResourcesTask)
//
//                    //创建生成
//                    def task = project.tasks.create("Generate${variant.name.capitalize()}R2", GenerateR2Task.class)
//                    task.outputDir = new File(outputDir)
//                    task.pkg = pkg
//                    task.rFile = rFile
//
//                    /**
//                     * 把新创建的任务挂载到 Gradle 构建任务中：
//                     * 给variant 添加一个生成 Java 代码的任务。This will make the generate[Variant]Sources task depend on this task and add the
//                     *  new source folders as compilation inputs.
//                     */
//                    variant.registerJavaGeneratingTask(task, task.outputDir)
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