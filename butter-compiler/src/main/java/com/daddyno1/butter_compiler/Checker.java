package com.daddyno1.butter_compiler;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public class Checker {
    public static void checkBindViewElement(Element element){
        if(checkIsPrivate(element)){
            throw new RuntimeException(Utils.detailElement(element) + " 不能是私有的");
        }
    }

    public static void checkOnClickElement(Element element){
        if(checkIsPrivate(element)){
            throw new RuntimeException(Utils.detailElement(element) + " 不能是私有的");
        }
    }

    public static void checkBindStringElement(Element element){
        if(checkIsPrivate(element)){
            throw new RuntimeException(Utils.detailElement(element) + " 不能是私有的");
        }
    }

    private static boolean checkIsPrivate(Element element){
        Set<Modifier> modifiers = element.getModifiers();
        for (Modifier modifier : modifiers) {
            if(modifier == Modifier.PRIVATE){
                return true;
            }
        }
        return false;
    }
}
