package com.yc.staticproxy;


import com.yc.jdkproxy.OrderBizImpl;

public class Test {
    public static void main(String[] args) {
        OrderBiz ob = (OrderBiz) new OrderBizImpl();
        ob.addOrder(1,100);
    }
}
