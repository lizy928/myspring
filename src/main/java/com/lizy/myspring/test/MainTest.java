package com.lizy.myspring.test;

import com.lizy.myspring.spring.context.ApplicationContext;
import com.lizy.myspring.test.service.UserService;

/**
 * @author lizy
 * @date 2021/7/20 15:57
 */
public class MainTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(Config.class);

        final UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();
    }

}