package asia.lhweb.spring.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 用户服务
 *
 * @author 罗汉
 * @date 2023/07/22
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    public void hi(){
        userDAO.hi();
    }
    //需要指定init方法是初始化方法
    @PostConstruct//指定初始化方法的注解
    public void  init(){
        System.out.println("UserService init()");
    }
}
