package com.yc.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component
//@Aspect
public class MyAspects {
    @Pointcut("execution(* com.yc.biz.*.make*(..))")
    private void a(){}

    @Before("a()")
    public void recordTime(){
        Date d = new Date();
        System.out.println("=========下单时间:"+ d);
    }

    @AfterReturning("a()")
    public void sendEmail(){
        System.out.println("调用数据库查询此下单用户的Email, 包装信息, 发送到信息中间件 kafka/mq");
    }

    @AfterReturning("a()")
    public void recordParams(JoinPoint jp){
        System.out.println("增强的方法为:"+ jp.getSignature());
        System.out.println("增强的目标类为:"+ jp.getTarget());
        System.out.println("参数:");
        Object[] params = jp.getArgs();
        for(Object o: params){
            System.out.println(o);
        }
    }

    @Pointcut("execution(* com.yc.biz.*.findOrderId(String))")
    private void b(){}

    private Map<String, Long> map = new ConcurrentHashMap<>();
    @AfterReturning("b()")
    public void recordPnameCount(JoinPoint jp){
        Object[] objs = jp.getArgs();
        String pname = (String)objs[0];
        Long num = 1L;
        if (map.containsKey(pname)){
            num = map.get(pname);
            num++;
        }
        map.put(pname, num);
        System.out.println("统计结果:"+ map);
    }

    @Pointcut("execution(int com.yc.biz.*.findPid(String))")
    private void c(){}
    private Map<String, Long> map2 = new ConcurrentHashMap<>();
    @AfterReturning(pointcut = "c()", returning = "retValue")
    public void recordPnameCount2(JoinPoint jp, int retValue){
        Object[] objs = jp.getArgs();
        String pname = (String)objs[0];
        Long num = 1L;
        if (map2.containsKey(pname)){
            num = map2.get(pname);
            num++;
        }
        map2.put(pname+":"+retValue, num);
        System.out.println("统计结果:" + map2);
    }

    @AfterThrowing(pointcut = "a()", throwing = "ex")
    public void recordException (JoinPoint jp,  RuntimeException ex){
        System.out.println("***********异常了**********");
        System.out.println(ex.getMessage());
        System.out.println(jp.getArgs()[0]+"\t"+jp.getArgs()[1]);
        System.out.println("**************************");
    }

    @Pointcut("execution(* com.yc.biz.*.find*(..))")
    private void d(){}

    @Around("d()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.currentTimeMillis();
        Object retVal = pjp.proceed();
        long end = System.currentTimeMillis();
        System.out.println("方法执行时长为:" + (end-start));
        return retVal;
    }











}
