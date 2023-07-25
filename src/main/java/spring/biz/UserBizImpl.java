package spring.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import spring.dao.UserDao;

@Service
public class UserBizImpl implements UserBiz {
    @Autowired
    @Qualifier("userDaoImpl")
    private UserDao userDao;
    public UserBizImpl(){
        System.out.println("userBizImpl的构造");
    }
    @Override
    public void add(String uname){
        userDao.add(uname);
    }
}
