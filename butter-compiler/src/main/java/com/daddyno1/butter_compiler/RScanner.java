package com.daddyno1.butter_compiler;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.util.HashMap;
import java.util.Map;

public class RScanner extends TreeScanner {

    private Map<Integer, String> r2Cache = new HashMap<>();

    /**
     * 变量的定义
     */
    @Override
    public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
        super.visitVarDef(jcVariableDecl);
//        System.out.println("1 " + jcVariableDecl.name);     //abc_action_bar_title_item     //变量名
//        System.out.println("2 " + jcVariableDecl.init);     //1                             //变量初始值
//        System.out.println("3 " + jcVariableDecl.vartype);  //int                           //变量类型

        if(jcVariableDecl.sym != null){
            //父节点
            Symbol enclosingSym = jcVariableDecl.sym.getEnclosingElement();     // layout
            if(enclosingSym != null){
                r2Cache.put(Integer.valueOf(jcVariableDecl.init.toString()), "R." + enclosingSym.name.toString() + "." + jcVariableDecl.name.toString());
            }
        }
    }

    public Map<Integer, String> getR2Cache() {
        return r2Cache;
    }
}
