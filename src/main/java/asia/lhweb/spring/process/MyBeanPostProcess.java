package asia.lhweb.spring.process;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 一个后置处理器
 *
 * @author 罗汉
 * @date 2023/07/22
 */
@Component
public class MyBeanPostProcess implements BeanPostProcessor {
    /**
     * 在bean的初始化方法前调用
     *
     * @param bean     豆
     * @param beanName bean名字
     * @return {@link Object}
     * @throws BeansException 豆子例外
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization 被 调 用 " + beanName + " bean= " + bean.getClass());
        return bean;
    }

    /**
     * bean初始化后调用
     *
     * @param bean     豆
     * @param beanName bean名字
     * @return {@link Object}
     * @throws BeansException 豆子例外
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("AfterInitialization 被 调 用 " + beanName + " bean= " + bean.getClass());
        return bean;
    }
}
