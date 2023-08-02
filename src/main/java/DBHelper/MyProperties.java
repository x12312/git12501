package DBHelper;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取配置文件且只需读取一次即可   --》单例设计模式  --》饿汉式
 *
 * @author Administrator
 */
public class MyProperties extends Properties {

	private static DBHelper.MyProperties instance = new DBHelper.MyProperties();

    private MyProperties() {
        //加载文件
        try {
            this.load(DBHelper.MyProperties.class.getClassLoader().getResourceAsStream("db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //DCL 双重检查锁
    public static DBHelper.MyProperties getInstance(){
        if (instance == null){
            synchronized (DBHelper.MyProperties.class){
                if (instance == null){
                    instance = new DBHelper.MyProperties();
                }
            }
        }
        return instance;
    }

}
