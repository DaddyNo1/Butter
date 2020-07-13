package com.daddyno1.butter_compiler.model;

public class OnClickBean extends Bean {
    public String funName;
    public int[] id;

    public OnClickBean(String pkgName, String clsName, String funName, int[] id) {
        super(pkgName, clsName);
        this.funName = funName;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.pkgName + ":" + clsName + " -> " + funName + ":" + id.toString();
    }
}
