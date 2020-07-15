package com.daddyno1.butter_compiler;

import com.daddyno1.butter_annotation.BindString;
import com.daddyno1.butter_annotation.BindView;
import com.daddyno1.butter_annotation.OnClick;
import com.daddyno1.butter_compiler.model.Bean;
import com.daddyno1.butter_compiler.model.BindStringBean;
import com.daddyno1.butter_compiler.model.BindViewBean;
import com.daddyno1.butter_compiler.model.OnClickBean;
import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ButterProcessor extends BaseProcessor {

    private static final String R2 = "R2";

    Map<String, List<Bean>> maps = new HashMap<>();
    TypeElement t_Activity;
    TypeElement t_VIEW;

    //AST
    Trees trees;

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

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(BindView.class);
        annotations.add(OnClick.class);
        annotations.add(BindString.class);
        return annotations;
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        //初始化 AST
        trees = Trees.instance(processingEnvironment);
        Utils.init(elementUtils);

        //android.app.Activity
        t_Activity = elementUtils.getTypeElement(Consts.ACTIVITY);
        t_VIEW = elementUtils.getTypeElement(Consts.VIEW);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "ButterProcessor - process - start");

        if (!CollectionUtils.isEmpty(set)) {
            handleElement(roundEnvironment.getElementsAnnotatedWith(BindView.class), BindView.class);
            handleElement(roundEnvironment.getElementsAnnotatedWith(OnClick.class), OnClick.class);
            handleElement(roundEnvironment.getElementsAnnotatedWith(BindString.class), BindString.class);

            new Generator(maps).generate(handleR2(roundEnvironment));

            return true;
        }

        return false;
    }

    private void handleElement(Set<? extends Element> elements, Class<? extends Annotation> cls) {
        for (Element element : elements) {

            Checker.checkElement(element);

            Element parent = element.getEnclosingElement();
            if (!typeUtils.isSubtype(parent.asType(), t_Activity.asType())) {
                throw new RuntimeException("@BindView 只能用于Activity");
            }
            // 所属类的名字
            String clsSimpleName = parent.getSimpleName().toString();
            PackageElement packageEle = elementUtils.getPackageOf(parent);
            // 所属类的的包名
            String packageName = packageEle.getQualifiedName().toString();

            logger(packageEle);
            logger(clsSimpleName);

            Bean bean;
            if (cls == BindView.class) {
                bean = handleBindView(element, packageName, clsSimpleName);
            } else if (cls == OnClick.class) {
                bean = handleOnClick(element, packageName, clsSimpleName);
            } else if (cls == BindString.class) {
                bean = handleBindString(element, packageName, clsSimpleName);
            } else {
                continue;
            }

            String mapKey = packageEle + Consts.DOT + clsSimpleName;
            List<Bean> beans = maps.get(mapKey);
            if (beans == null) {
                beans = new ArrayList<>();
                maps.put(mapKey, beans);
            }
            beans.add(bean);
        }
    }

    private BindViewBean handleBindView(Element element, String pkg, String clsName) {

        if(!typeUtils.isSubtype(element.asType(), t_VIEW.asType())){
            throw new RuntimeException("@BindView 只能用于View");
        }

        // 属性的名称
        String attr = element.getSimpleName().toString();
        logger(attr);

        // BindView 注解
        BindView bindView = element.getAnnotation(BindView.class);
        // 控件的id
        int id = bindView.value();

        BindViewBean bindViewBean = new BindViewBean(pkg, clsName, attr, id);
        return bindViewBean;
    }

    private OnClickBean handleOnClick(Element element, String pkg, String clsName) {
        //被注解的方法名称
        String methodName = element.getSimpleName().toString();
        logger(methodName);

        OnClick onClick = element.getAnnotation(OnClick.class);
        int[] ids = onClick.value();

        OnClickBean onClickBean = new OnClickBean(pkg, clsName, methodName, ids);
        return onClickBean;
    }

    private BindStringBean handleBindString(Element element, String pkg, String clsName) {
        String attr = element.getSimpleName().toString();
        logger(attr);

        BindString bindString = element.getAnnotation(BindString.class);
        int id = bindString.value();

        BindStringBean bindStringBean = new BindStringBean(pkg, clsName, attr, id);
        return bindStringBean;
    }

    /**
     * 处理 遍历 R2 文件。
     */
    private Map<Integer, String> handleR2(RoundEnvironment roundEnvironment) {
        // Visitor
        RScanner rScanner = new RScanner();

        if (!roundEnvironment.processingOver() && trees != null) {
            Set<Element> elements = (Set<Element>) roundEnvironment.getRootElements();
            for (Element element : elements) {
                // 如果是 R2 文件
                if(R2.equals(element.getSimpleName().toString())){
                    messager.printMessage(Diagnostic.Kind.NOTE, " >>>> element " + element.getSimpleName());

                    JCTree tree = (JCTree) trees.getTree(element);
                    tree.accept(rScanner);
                }
            }
        }

        // 通过遍历 R2 文件，生成一份缓存文件，key-> R2的常量值。  value:R.attr.actionBarPopupTheme
        return rScanner.getR2Cache();
    }

}
