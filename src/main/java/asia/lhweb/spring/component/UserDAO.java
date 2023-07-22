package asia.lhweb.spring.component;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * 用户DAO
 *
 * @author 罗汉
 * @date 2023/07/22
 */
@Repository
public class UserDAO {
    public void hi(){
        System.out.println("UserDAO hi()");
    }
}
