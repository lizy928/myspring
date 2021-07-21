package com.lizy.myspring.test;

import com.lizy.myspring.spring.annotation.BeanPostProcessor;
import com.lizy.myspring.spring.annotation.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lzy
 * @date 2021/7/20
 */
@Component("testBeanPostProcessor")
public class TestBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(beanName.equals("userService")){
            System.out.println("beanPostProcessor for " + beanName);
        }
        System.out.println(beanName + "初始化前。。" );
        return null;
    }

    // spring aop就是在这里实现的
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        System.out.println(beanName + "初始化后。。");
        if("orderService".equals(beanName)){
            final Object instance = Proxy.newProxyInstance(TestBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理逻辑。。");
                    return method.invoke(bean, args);
                }
            });
            return instance;
        }

        return null;
    }
}
