package com.daddyno1.butter_compiler;

import com.daddyno1.butter_annotation.BindString;
import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butter_annotation.OnClick;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ButterProcessor extends BaseProcessor {

    TypeElement t_Activity;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        Set<Class<? extends Annotation>> annotations = getSupportedAnnotations();
        for (Class<? extends Annotation> annotation : annotations) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations(){
        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(BindView.class);
        annotations.add(OnClick.class);
        annotations.add(BindString.class);
        return annotations;
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        Utils.init(elementUtils);

        //android.app.Activity
        t_Activity = elementUtils.getTypeElement(Consts.ACTIVITY);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "ButterProcessor - process - start");

        if (!CollectionUtils.isEmpty(set)){
            handleBindView(roundEnvironment.getElementsAnnotatedWith(BindView.class));
            handleOnClick(roundEnvironment.getElementsAnnotatedWith(OnClick.class));
            handleBindString(roundEnvironment.getElementsAnnotatedWith(BindString.class));
        }

        return false;
    }

    private void handleBindView(Set<? extends Element> elements){
        for (Element element : elements) {

            Checker.checkBindViewElement(element);

            Element parent = element.getEnclosingElement();
            if(!typeUtils.isSubtype(parent.asType(), t_Activity.asType())){
                throw new RuntimeException("@BindView 只能用于Activity");
            }
            // 所属类的名字
            String clsSimpleName = parent.getSimpleName().toString();
            PackageElement packageEle = elementUtils.getPackageOf(parent);
            // 所属类的的包名
            String packageName = packageEle.getQualifiedName().toString();

            logger(packageEle);
            logger(clsSimpleName);

            // 属性的名称
            String attr = element.getSimpleName().toString();
            // 属性类型
            String attrType = element.asType().toString();

            logger(attrType + "   " + attr);
            // BindView 注解
            BindView bindView = element.getAnnotation(BindView.class);
            // 控件的id
            int id = bindView.value();
            logger(id);
        }
    }

    private void handleOnClick(Set<? extends Element> elements){
        for (Element element : elements) {

            Checker.checkOnClickElement(element);

            Element parent = element.getEnclosingElement();
            if(!typeUtils.isSubtype(parent.asType(),t_Activity.asType())){
                throw new RuntimeException("@OnClick 只能用于Activity");
            }

            // 所属类的名字
            String clsSimpleName = parent.getSimpleName().toString();
            PackageElement packageEle = elementUtils.getPackageOf(parent);
            // 所属类的的包名
            String packageName = packageEle.getQualifiedName().toString();

            logger(packageEle);
            logger(clsSimpleName);

            // 方法需要符合一定的规范，例如最多只能有一个参数 View v

            //被注解的方法名称
            String methodName = element.getSimpleName().toString();
            logger(methodName);

            OnClick onClick = element.getAnnotation(OnClick.class);
            int[] ids = onClick.value();
            logger(ids);
        }
    }

    private void handleBindString(Set<? extends Element> elements){
        for (Element element : elements) {

            Checker.checkBindStringElement(element);

            Element parent = element.getEnclosingElement();
            if(!typeUtils.isSubtype(parent.asType(),t_Activity.asType())){
                throw new RuntimeException("@BindString 只能用于Activity");
            }

            // 所属类的名字
            String clsSimpleName = parent.getSimpleName().toString();
            PackageElement packageEle = elementUtils.getPackageOf(parent);
            // 所属类的的包名
            String packageName = packageEle.getQualifiedName().toString();

            logger(packageEle);
            logger(clsSimpleName);

            String attr = element.getSimpleName().toString();
            logger(attr);

        }
    }


}
