import asia.lhweb.spring.component.MonsterDAO;
import asia.lhweb.spring.component.MonsterService;
import asia.lhweb.spring.ioc.LHSpringApplicationContext;
import asia.lhweb.spring.ioc.LHSpringConfig;

public class Text {
    public static void main(String[] args) {
        LHSpringApplicationContext ioc = new LHSpringApplicationContext(LHSpringConfig.class);
        // System.out.println("ok");
        // System.out.println();
        // MonsterService bean = (MonsterService) ioc.getBean("monsterService");
        // System.out.println(bean);

        //测试依赖注入
        MonsterService monsterService = (MonsterService) ioc.getBean("monsterService");
        monsterService.hello();

    }
}
