package com.daddyno1.butter_compiler;

import com.daddyno1.butter_annotation.BindString;
import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butter_compiler.model.Bean;
import com.daddyno1.butter_compiler.model.BindStringBean;
import com.daddyno1.butter_compiler.model.BindViewBean;
import com.daddyno1.butter_compiler.model.OnClickBean;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class Generator {
    Map<String, List<Bean>> maps;
    public Generator(Map<String, List<Bean>> maps){
        this.maps = maps;
    }

    //按规则生成java类
    public void generate(){
        if(maps == null){
            return;
        }

        TypeElement t_Activity = ButterProcessor.elementUtils.getTypeElement(Consts.ACTIVITY);

        for (Map.Entry<String, List<Bean>> entry : maps.entrySet()) {
            String classFullName = entry.getKey();
            TypeElement clsType = ButterProcessor.elementUtils.getTypeElement(classFullName);

            // Activity obj
            ParameterSpec bindParameterSpec = ParameterSpec.builder(ClassName.get(t_Activity), Consts.BIND_METHOD_PARAMETER_NAME).build();

            /**
             * @Override
             * public void bind(Activity obj)
             */
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Consts.BIND_METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(bindParameterSpec)
                    .addStatement("{")
                    .addStatement("if(obj != null){")
                    .addStatement("$T activity = ($T) obj;", clsType, clsType);

            List<Bean> list= entry.getValue();
            for (Bean bean : list) {
                if(bean instanceof BindViewBean){
                    // id 和 R2 文件中的数据如何 映射？
//                    methodBuilder.addStatement("activity.tvTxt = activity.findViewById($L);", );
                }else if(bean instanceof OnClickBean){

                }else if(bean instanceof BindStringBean){

                }
            }
            methodBuilder.addStatement("}");
            methodBuilder.addStatement("}");
        }
    }
}
