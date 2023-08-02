package org.ycframework.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycframework.annotation.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class YcAnnotationConfigApplicationContext implements YcApplicationContext{
    private Logger logger = LoggerFactory.getLogger(YcAnnotationConfigApplicationContext.class);
    //存每个待托管的Bean的定义信息
    private Map<String, YcBeanDafinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //存每个实例忙后的bean
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    //存系统属性，db .properties
    private Properties pros;

    public YcAnnotationConfigApplicationContext(Class... configClasses){
        try {
            //读取系统的属性
            pros = System.getProperties();
            List<String> toScanPackagePath = new ArrayList<>();
            for (Class cls : configClasses) {
                if (cls.isAnnotationPresent(YcComponentScan.class) == false) {
                    continue;
                }
                //扫描配置类上的 @YcComponentScan注解﹐读取要扫描的包
                String[] basePackages = null;
                if (cls.isAnnotationPresent(YcComponentScan.class)) {
                    YcComponentScan ycComponentScan = (YcComponentScan) cls.getAnnotation(YcComponentScan.class);
                    basePackages = ycComponentScan.basePackages();
                    if (basePackages == null || basePackages.length <= 0) {
                        basePackages = new String[1];
                        basePackages[0] = cls.getPackage().getName();
                    }
                    logger.info(cls.getName() + "类上有@YcComponentScan注解﹐它要扫描的路径:" + basePackages[0]);
                }
                //开始扫描这些basePackages包下的bean，并加载包装成 BeanDefinition 对象 beanDefinitionMap
                recursiveLoadBeanDefinition(basePackages);
            }
            //循环 beanDefinitionMap, 创建bean(是否为懒加载， 是否为单例)，存到 beanMap
            createBean();
            //循环所有托管的 beanMap中的bean，看属性和方法上是否有@Autowired @Resource @Value
            doDi();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }
    }

    /**
     * 循环 beanDefinitionMap, 创建bean(是否为懒加载， 是否为单例)，存到 beanMap
     */
    private void doDi() throws IllegalAccessException,InstantiationException, ClassNotFoundException{
        //循环的是 beanMap, 这是托管Bean
        for (Map.Entry<String, Object>entry: beanMap.entrySet()){
            String beanId = entry.getKey();
            Object beanObj = entry.getValue();
            //情况一：属性上有 @YcResource注解的情况
            Field[] fields = beanObj.getClass().getDeclaredFields();
            for (Field field: fields){
                if (field.isAnnotationPresent(YcResource.class)){
                    YcResource ycResource = field.getAnnotation(YcResource.class);
                    String toDiBeanId = ycResource.name();
                    //从 beanMap中找，是否singleton，是否lazy
                    Object obj = getToDiBean(toDiBeanId);
                    //注入
                    field.setAccessible(true);//因为属性是private的﹐所以要将它accessible设为true
                    field.set(beanObj, obj);//userBizimpl.userDao=userDaompl
                }
            }
        }
    }

    //从 beanMap中找，是香singleton,是否lazy
    private Object getToDiBean(String toDiBeanId) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (beanMap.containsKey(toDiBeanId)){
            return beanMap.get(toDiBeanId);
        }
        //判断 beanMap中没有此bean是否因为lazy
        if (!beanDefinitionMap.containsKey(toDiBeanId)){
            throw new RuntimeException("spring容器中没有加载此class:" + toDiBeanId);
        }
        YcBeanDafinition bd = beanDefinitionMap.get(toDiBeanId);
        if (bd.isLazy()){
            //是因为懒﹐所以没有托管
            String classpath = bd.getClassInfo();
            Object beanObj = Class.forName(classpath).newInstance();
            beanMap.put(toDiBeanId, beanObj);
            return beanObj;
        }
        //是因prototype
        if (bd.getScope().equalsIgnoreCase("prototype")){
            //是因为懒﹐所以没有托管
            String classpath = bd.getClassInfo();
            Object beanObj = Class.forName(classpath).newInstance();
            //beanMap .put(toDiBeanId,bean0bj );  原型模式下﹐每getBean创建一或bean ，所以 beanMap不存
            return beanObj;
        }
        return null;
    }

    private void createBean() throws ClassNotFoundException, IllegalAccessException, InstantiationException{
        for (Map.Entry<String, YcBeanDafinition> entry: beanDefinitionMap.entrySet()){
            String beanId = entry.getKey();
            YcBeanDafinition ybd = entry.getValue();
            if (!ybd.isLazy() && !ybd.getScope().equalsIgnoreCase("prototype")){
                String classInfo = ybd.getClassInfo();
                Object obj = Class.forName(classInfo).newInstance();
                beanMap.put(beanId, obj);
                logger.trace("spring容器托管了:"+beanId+"=>"+classInfo);
            }
        }
    }

    /**
     * 开始扫描这些basePackages包下的bean，并加载包装成 BeanDefinition 对象 beanDefinitionMap
     * @param basePackages
     */
    private void recursiveLoadBeanDefinition(String[] basePackages) {
        for (String basePackage : basePackages){
            String packagePath = basePackage.replaceAll("\\.","/");
            Enumeration<URL> files = null;
            try {
                files = Thread.currentThread().getContextClassLoader().getResources(packagePath);
                //循环这个files,看是否是我要加载的资源
                while (files.hasMoreElements()){
                    URL url = files.nextElement();
                    logger.trace("当前正在递归加载:"+url.getFile());
                    //查找此包下的类      com/yc全路怪    com/yc包名
                    findPackageClasses(url.getFile(), basePackage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findPackageClasses(String packagePath, String basePackage) {
        //路怪异常的处理，前面有/,则去掉它
        if (packagePath.startsWith("/")){
            packagePath = packagePath.substring(1);
        }
        //取这个路径下所有的字节码文作(因为目录下有可能有其它的资源)
        File file = new File(packagePath);
        //只读取后缀名为.class的字节码
        //方法一接口的匿名内部类写法
//        File [] classFile = file.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                if (pathname.getName().endsWith(".class") || pathname.isDirectory()){
//                    return true;
//                }
//                return false;
//            }
//        });
        //方法二: lambda写法
        File[] classFiles = file.listFiles((pathname)->{
            if (pathname.getName().endsWith(".class") || pathname.isDirectory()){
                return true;
            }
            return false;
        });
        //循环此classFiles
        if (classFiles == null || classFiles.length<=0){
            return;
        }
        for (File cf: classFiles){
            if (cf.isDirectory()){
                //继续递归
                logger.trace("递归:"+cf.getAbsolutePath()+",它对应的包名为:"+(basePackage +"."+cf.getName()));
                findPackageClasses(cf.getAbsolutePath(), basePackage + "." + cf.getName());
            }else {
                //是class文件﹐则取出文件﹐判断此文件对应的class中是否有  acomponent注解
                URLClassLoader uc = new URLClassLoader(new URL[]{});
                //UserDaoImpl.class
                Class cls = null;
                try {
                    cls = uc.loadClass(basePackage+ "." +cf.getName().replaceAll(".class",""));
                    if (cls.isAnnotationPresent(YcComponent.class)
                    ||cls.isAnnotationPresent(YcController.class)
                    ||cls.isAnnotationPresent(YcConfiguration.class)
                    ||cls.isAnnotationPresent(YcRepository.class)
                    ||cls.isAnnotationPresent(YcService.class)){
                        logger.info("加载到一个待托管的类:" + cls.getName());
                        //TODO:包装成BeanDefinition
                        YcBeanDafinition bd = new YcBeanDafinition();
                        if (cls.isAnnotationPresent(YcLazy.class)){
                            bd.setLazy(true);
                        }
                        if (cls.isAnnotationPresent(YcScope.class)){
                            YcScope ycScope = (YcScope) cls.getAnnotation(YcScope.class);
                            String scope = ycScope.value();
                            bd.setScope(scope);
                        }
                        bd.setClassInfo(basePackage+"."+cf.getName().replaceAll(".class", ""));
                        //存到 beanDefinitionMap 中 "beanid" -> "BeanDefinition对象"
                        String beanid = genBeanId(cls);
                        this.beanDefinitionMap.put(beanid, bd);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String genBeanId(Class cls){
        YcComponent ycComponent= (YcComponent) cls.getAnnotation(YcComponent.class);
        YcController ycController= (YcController) cls.getAnnotation(YcController.class);
        YcService ycService= (YcService) cls.getAnnotation(YcService.class);
        YcRepository ycRepository= (YcRepository) cls.getAnnotation(YcRepository.class);
        YcConfiguration ycConfiguration= (YcConfiguration) cls.getAnnotation(YcConfiguration.class);

        if (ycConfiguration!=null){
            return cls.getName();
        }
        String benId=null;
        if (ycComponent!=null){
            benId=ycComponent.value();
        }else if(ycController!=null){
            benId= ycController.value();
        }else if(ycService!=null){
            benId= ycService.value();
        }else if(ycRepository!=null){
            benId= ycRepository.value();
        }
        if (benId==null || "".equalsIgnoreCase(benId)){
            String typename=cls.getSimpleName();
            benId=typename.substring(0,1).toLowerCase()+typename.substring(1);
        }
        return benId;
    }

    @Override
    public Object getBean(String beanid) {
        YcBeanDafinition bd = this.beanDefinitionMap.get(beanid);
        if (bd == null){
            throw new RuntimeException("容器中没有加载此bean");
        }
        String scope = bd.getScope();
        if ("prototype".equalsIgnoreCase(scope)){
            //原型模式，每次getBean 创建
            Object obj = null;
            try {
                obj = Class.forName(bd.getClassInfo()).newInstance();
                return obj;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        if (this.beanMap.containsKey(beanid)){
            return this.beanMap.get(beanid);
        }
        if (bd.isLazy()){
            Object obj = null;
            try {
                obj = Class.forName(bd.getClassInfo()).newInstance();
                //懒加载的bean是要保存的
                this.beanMap.put(beanid, obj);
                return obj;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return obj;
        }
        return null;
    }
}
















