package asia.lhweb.spring.processor;

/**
 * 1 根据原生spring定义一个接口 InitializingBean
 * 2 该InitializingBean接口有一个 void afterPropertiesSet() throws Exception;
 * 3 afterPropertiesSet() 在bean的setter后执行，即就是我们原来的初始化方法
 * 4 当一个bean实现这个接口后，就实现afterPropertiesSet() 这个方法就是初始化方法
 * @author 罗汉
 * @date 2023/07/24
 */
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
