package asia.lhweb.spring.component;

import asia.lhweb.spring.annotation.Autowired;
import asia.lhweb.spring.annotation.Scope;
import asia.lhweb.spring.annotation.Service;
import asia.lhweb.spring.processor.InitializingBean;

/**
 * MonsterService是一个service
 *  1 如果指定了那么在注入容器时以指定的为准
 *  2 如果没有指定则使用类名首字母小写的名字
 * @author 罗汉
 * @date 2023/07/23
 */
// @Service(value = "monsterService")//阶段1 把MonsterService注入到我们自己的容器中
@Scope(value = "prototype")
@Service//阶段2
public class MonsterService implements InitializingBean {
    @Autowired
    private MonsterDAO monsterDAO;
    public void hello(){
        monsterDAO.hi();
    }

    /**
     * 这个方法就是bean的初始化方法
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化方法被调用");
    }
}
