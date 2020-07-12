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