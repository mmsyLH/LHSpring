package asia.lhweb.spring.processor;


import org.springframework.lang.Nullable;

/**
 * 后置处理程序
 * 1 参考原生spring容器定义的一个接口
 * 2 该接口有2个方法
 * 3 这2个方法会对spring容器的全部bean生效（切面编程的概念）
 * @author 罗汉
 * @date 2023/07/24
 */
public interface BeanPostProcessor {
    /**
     * 在初始化方法前调用
     *
     * @param bean     豆
     * @param beanName bean名字
     * @return {@link Object}
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName){
        return bean;
    }


    /**
     * 发布过程初始化后调用
     *
     * @param bean     豆
     * @param beanName bean名字
     * @return {@link Object}
     */
    default Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
