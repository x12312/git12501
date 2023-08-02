package com.yc.biz;

public interface OrderBiz {
    public void makeOrder(int pid, int num);

    public int findOrderId(String pname);

    int findPid(String pname);
}
