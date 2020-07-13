package com.daddyno1.butter_compiler.model;

public class BindViewBean extends Bean {
    public String attrName;
    public int id;

    public BindViewBean(String pkgName, String clsName, String attrName, int id) {
        super(pkgName, clsName);
        this.attrName = attrName;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.pkgName + ":" + clsName + " -> " + attrName + ":" + id;
    }
}
