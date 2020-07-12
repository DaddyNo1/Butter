package com.daddyno1.butter_compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class Utils {

    private static Elements elementUtils;

    public static void init(Elements elements){
        elementUtils = elements;
    }

    public static String detailElement(Element element){
        Element parent = element.getEnclosingElement();
        PackageElement packageElement = elementUtils.getPackageOf(parent);
        String eleName = element.getSimpleName().toString();
        TypeMirror typeMirror = element.asType();

        return packageElement.getQualifiedName()
                + "@"
                + parent.getSimpleName()
                + "-->"
                + element
                + "  ";
    }
}
