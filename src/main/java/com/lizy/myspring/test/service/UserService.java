package com.lizy.myspring.test.service;

import com.lizy.myspring.spring.InititalizingBean;
import com.lizy.myspring.spring.annotation.Autowried;
import com.lizy.myspring.spring.annotation.Component;
import com.lizy.myspring.spring.aware.BeanNameAware;

/**
 * @author lizy
 * @date 2021/7/20 15:52
 */
@Component("userService")
public class UserService implements BeanNameAware, InititalizingBean {

    private String beanName;

    @Autowried
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }

    @Override
    public String setBeanName(String beanName) {
        this.beanName = beanName;
        return beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("userService 初始化。。。");
    }
}