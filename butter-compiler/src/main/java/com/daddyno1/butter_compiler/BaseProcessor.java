package com.daddyno1.butter_compiler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public abstract class BaseProcessor extends AbstractProcessor {

    static public Filer filer;
    static public Elements elementUtils;
    static public Messager messager;
    static public Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();

        /**
         * Types
         */
//        boolean isSubtype(TypeMirror var1, TypeMirror var2);    true if and only the first type is a subtype of the second.
//        boolean isSameType(TypeMirror var1, TypeMirror var2);  true if and only if the two types are the same
//        boolean isAssignable(TypeMirror var1, TypeMirror var2);  true if and only if the first type is assignable to the second.

        /**
         * Elements
         */
//        PackageElement getPackageElement(CharSequence name)   return a package given tis fully qualified name
//        PackageElement getPackageOf(Element type)  returns the package of an element. the package of a package is itself.

    }

    protected void logger(Object msg){
        messager.printMessage(Diagnostic.Kind.NOTE, msg.toString());
    }
}
