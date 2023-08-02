package spring03;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring03.system.Container;
import spring03.user.Student;


public class App {
    public static void main(String[] args) {
        ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
        Container c = (Container) ac.getBean("container");

        c.add(new Student("张三",1.7,90));
        c.add(new Student("李四",1.8,110));
        c.add(new Student("王五",1.5,60));

        c.add(new Student("异常",0.5,799));

        System.out.println(c.getMax());
        System.out.println(c.getMin());
        System.out.println(c.getAvg());
        System.out.println(c.size());
    }
}
