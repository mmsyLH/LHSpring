package asia.lhweb.spring.annotation;

import java.lang.annotation.*;

/**
 * 自动装配注解
 *
 * @author 罗汉
 * @date 2023/07/23
 */

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    //一定要找到一个匹配的找不到就会报错
    // boolean required() default true;
}
