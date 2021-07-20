package com.lizy.myspring.spring;

/**
 * @author lizy
 * @date 2021/7/20 16:21
 */
public class BeanDefinition {

    private Class clazz;

    private String scope;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}