package com.daddyno1.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.TaskAction

class GenerateR2Task extends DefaultTask{

    File outputDir
    String pkg
    ConfigurableFileCollection rFile


    GenerateR2Task(){
        description = "这是一个生成R2 的task"
    }

    def setInputAndOutput(){
        this.inputs.files(rFile)
        /**
         * 现在又有一个问题：为什么clean完项目后，只有第一次可以build通过，下一次执行build 的时候回报错？
         *
         *    报错信息：
         *    A problem was found with the configuration of task ':test:GenerateDebugR2'.
         *      > Cannot write to file '/Users/jxf/workspace/Android/githubProject/Butter/test/build/generated/source/r2/debug' specified for property '$1' as it is a directory.
         *
         */
        this.outputs.file(outputDir)
    }

    /**
     * 有@TaskAction 修饰的会执行于 Task 执行阶段。
     */
    @TaskAction
    def doAction(){
        println 'GenerateR2Task - doAction'

        File file = rFile.getSingleFile()
        if(file.exists()){
            println "========file ${file.path} exists========"

            def utils = new GenerateJavaUtils()
            def lines = file.readLines()
            lines.each { // 遍历每一行内容
                utils.addItem(it)
            }
            utils.generate(pkg, outputDir)
        }else{
            println "========file ${file.path} don't exists========"
        }
    }
}