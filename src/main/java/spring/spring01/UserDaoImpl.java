package spring.spring01;

import org.springframework.stereotype.Repository;
import spring.dao.UserDao;


@Repository
public class UserDaoImpl implements UserDao {
    public UserDaoImpl(){
        System.out.println("UserDaoImpl类的构造...");
    }
    @Override
    public void add(String uname){
        System.out.println("添加了:"+uname);
    }
}
