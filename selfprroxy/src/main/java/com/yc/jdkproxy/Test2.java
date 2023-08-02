package com.yc.jdkproxy;

import com.yc.staticproxy.OrderBiz;

public class Test2 {
    public static void main(String[] args) {
        System.getProperties().put("sum.misc.ProxyGenerator.saveGeneratedFiles", "true");
        JdkProxyTool jpt = new JdkProxyTool(new OrderBizImpl());
        OrderBiz ob = (OrderBiz) jpt.createProxy();

        ob.findOrder();
        ob.addOrder(1, 99);
    }
}
