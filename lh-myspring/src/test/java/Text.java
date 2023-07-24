import asia.lhweb.spring.component.MonsterDAO;
import asia.lhweb.spring.component.MonsterService;
import asia.lhweb.spring.component.SmartAnimalable;
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
        // MonsterService monsterService = (MonsterService) ioc.getBean("monsterService");
        // monsterService.hello();

        //测试AOP是否生效
        SmartAnimalable smartDog = (SmartAnimalable) ioc.getBean("smartDog");
        smartDog.getSum(15,2);
        System.out.println("smartDog的类型="+smartDog.getClass());

    }
}
