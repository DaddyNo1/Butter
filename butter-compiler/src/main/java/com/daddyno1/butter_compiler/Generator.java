package com.daddyno1.butter_compiler;

import com.daddyno1.butter_annotation.BindString;
import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butter_compiler.model.Bean;
import com.daddyno1.butter_compiler.model.BindStringBean;
import com.daddyno1.butter_compiler.model.BindViewBean;
import com.daddyno1.butter_compiler.model.OnClickBean;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class Generator {
    Map<String, List<Bean>> maps;
    public Generator(Map<String, List<Bean>> maps){
        this.maps = maps;
    }


    /**
     * 按规则生成java类
     * @param ids   id 和 对应变量名的 映射
     */
    public void generate(Map<Integer, String> ids){
        if(maps == null || ids.isEmpty()){
            System.out.println("------  异常退出 ------");
            return;
        }

        TypeElement t_Activity = ButterProcessor.elementUtils.getTypeElement(Consts.ACTIVITY);
        TypeElement t_ViewBinding = ButterProcessor.elementUtils.getTypeElement(Consts.VIEW_BINDING);

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
                    .beginControlFlow("if(obj != null)")
//                    .addStatement("if(obj != null){")
                    .addStatement("$T activity = ($T) obj", clsType, clsType);

            List<Bean> list= entry.getValue();
            String pkg = null;
            String clsName = null;
            for (Bean bean : list) {
                pkg = bean.pkgName;
                clsName = bean.clsName;

                if(bean instanceof BindViewBean){
                    // id 和 R2 文件中的数据如何 映射？
                    methodBuilder.addStatement("activity.$L = activity.findViewById($L)", ((BindViewBean) bean).attrName, ids.get(((BindViewBean) bean).id));
                }else if(bean instanceof OnClickBean){

                }else if(bean instanceof BindStringBean){

                }
            }
//            methodBuilder.addStatement("}");
            methodBuilder.endControlFlow();

            TypeSpec typeSpec = TypeSpec.classBuilder(clsName + Consts.CLASS_SUFFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(t_ViewBinding.asType())
                    .addMethod(methodBuilder.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(pkg, typeSpec)
                    .build();

            try {
                javaFile.writeTo(ButterProcessor.filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
