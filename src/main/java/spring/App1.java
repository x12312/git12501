package spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.biz.UserBiz;
import spring.dao.UserDao;
import spring.spring01.Config;

public class App1 {
    public static void main(String[] args) {
        ApplicationContext container = new AnnotationConfigApplicationContext(Config.class);
        UserDao ud = (UserDao) container.getBean("userDaoImpl");
        ud.add("张三");

        UserBiz ub = (UserBiz) container.getBean("userBizImpl");
        ub.add("王五");
    }
}
