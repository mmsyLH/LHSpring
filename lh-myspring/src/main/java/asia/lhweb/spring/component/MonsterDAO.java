package asia.lhweb.spring.component;


import asia.lhweb.spring.annotation.Repository;
import asia.lhweb.spring.processor.InitializingBean;

/**
 *
 * @author 罗汉
 * @date 2023/07/23
 */
@Repository(value = "monsterDAO")
public class MonsterDAO{
    public void hi(){
        System.out.println("MonsterDAO的 hi()");
    }

    // @Override
    // public void afterPropertiesSet() throws Exception {
    //     System.out.println("MonsterDAO 初始化方法被调用");
    // }
}
