package spring02;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExample {
    public static void main(String[] args) {
        // 创建线程池，最大线程数为5
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // 提交5个任务给线程池
        for (int i = 0; i < 5; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String currentTime = dateFormat.format(new Date());
                    System.out.println("当前时间: " + currentTime);
                }
            });
        }

        // 关闭线程池
        executor.shutdown();
    }
}
