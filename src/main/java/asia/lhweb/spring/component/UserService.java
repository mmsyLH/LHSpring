package asia.lhweb.spring.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
