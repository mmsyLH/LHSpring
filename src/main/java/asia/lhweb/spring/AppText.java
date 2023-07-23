package asia.lhweb.spring;

import asia.lhweb.spring.aop.SmartAnimal;
import asia.lhweb.spring.component.UserAction;
import asia.lhweb.spring.component.UserDAO;
import asia.lhweb.spring.component.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppText {
    public static void main(String[] args) {
        ApplicationContext
          ioc      = new ClassPathXmlApplicationContext("beans.xml");
        UserAction userAction = (UserAction) ioc.getBean("userAction");
        UserAction userAction2 = (UserAction) ioc.getBean("userAction");
        System.out.println("userAction="+userAction);
        System.out.println("userAction2="+userAction2);

        UserDAO userDAO = (UserDAO) ioc.getBean("userDAO");
        System.out.println("userDAO="+userDAO);

        UserService userService = (UserService) ioc.getBean("userService");
        System.out.println("userService="+userService);

        //测试aop
        System.out.println();
        SmartAnimal bean = ioc.getBean(SmartAnimal.class);
        bean.getSum(10,15);


    }
}
