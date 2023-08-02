package spring02;

import com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutorService;

public class App {
    public static void main(String[] args) {
        ApplicationContext container = new AnnotationConfigApplicationContext(Config.class);
        ExecutorService es = (ExecutorService) container.getBean("threadPoolExecutor");
//        for (int i=0; i<5; i++){
//            es.submit(()->{
//                while (true){
//
//                }
//            });
//        }
    }
}
