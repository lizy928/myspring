package com.lizy.myspring.test;

import com.lizy.myspring.spring.annotation.Component;

/**
 * @author lizy
 * @date 2021/7/20 16:47
 */
@Component("orderService")
public class OrderService implements IOrderService{


    @Override
    public void test() {
        System.out.println("下单业务逻辑。。");
    }
}
