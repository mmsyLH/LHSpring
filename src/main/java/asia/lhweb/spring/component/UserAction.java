package asia.lhweb.spring.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 就是一个Controller
 *
 * @author 罗汉
 * @date 2023/07/22
 */
@Component//默认情况下都是单例
@Scope(value = "prototype")// Scope:作用范围 prototype：多实例
public class UserAction {
    @PostConstruct//指定初始化方法的注解
    public void  init(){
        System.out.println("UserAction init()");
    }
}
