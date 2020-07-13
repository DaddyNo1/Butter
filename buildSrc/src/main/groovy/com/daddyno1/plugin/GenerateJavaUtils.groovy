package com.daddyno1.plugin

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec

import javax.lang.model.element.Modifier
import java.lang.reflect.Array

class GenerateJavaUtils {

    static def R2 = "R2"
    int initValue = 1
    static def SYMBOLS = ["anim", "array", "attr", "bool", "color", "dimen",
                          "drawable", "id", "integer", "layout", "menu", "plurals",
                          "string", "style", "styleable"]
    // symbolType -> names
    Map<String, List<String>> maps = new HashMap<String, List<String>>()

    /**
     * int anim abc_slide_in_bottom 0x7f010006
     */
    def addItem(String item) {
        def values = item.split(" ")
        if (values.length < 4) {
            return
        }
        def type = values[0]            //int
        if (type != 'int') {
            return
        }
        def symbolType = values[1]      //anim
        if (!SYMBOLS.contains(symbolType)) {
            return
        }
        def name = values[2]        //abc_slide_in_bottom

        def stList = maps.get(symbolType)
        if (stList == null) {
            stList = new ArrayList<String>()
            maps.put(symbolType, stList)
        }
        stList.add(name)
    }

    def generate(String pkg, File output) {

        List<TypeSpec> types = new ArrayList<>()

        maps.entrySet().each { Map.Entry<String, List<String>> entry ->
            List<FieldSpec> fields = new ArrayList<>()

            entry.value.each { String symbolType ->
                FieldSpec fieldSpec = FieldSpec.builder(int.class, symbolType, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("\$L", initValue++)
                        .build()
                fields.add(fieldSpec)
            }

            TypeSpec type = TypeSpec.classBuilder(entry.key)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addFields(fields)
                    .build()

            types.add(type)
        }

        TypeSpec R2 = TypeSpec.classBuilder(R2)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addTypes(types)
                .build()

        println "${pkg} - ${output.path}"
        JavaFile r2File = JavaFile.builder(pkg, R2).build()
        r2File.writeTo(output)
    }
}