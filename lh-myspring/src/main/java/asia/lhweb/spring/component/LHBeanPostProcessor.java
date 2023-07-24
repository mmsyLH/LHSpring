package asia.lhweb.spring.component;

import asia.lhweb.spring.annotation.Component;
import asia.lhweb.spring.processor.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * lhbean后置处理程序
 * 可以写里面的方法
 * <p>
 * 1 在spring容器中仍然把后置处理器当成一个bean对待
 * 2 还要考虑多个后置处理器对象注入到容器中的问题
 *
 * @author 罗汉
 * @date 2023/07/24
 */
@Component
public class LHBeanPostProcessor implements BeanPostProcessor {
    /**
     * 在初始化方法前调用
     *
     * @param bean     豆
     * @param beanName bean名字
     * @return {@link Object}
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        // 后置处理器是针对容器创建的bean生效 相当于是对多个对象编程 切面编程
        if (bean instanceof car) {
            System.out.println("这是一个car对象，我可以处理");

        }
        System.out.println("后置处理器LHBeanPostProcessor 的before调用 bean的类型=" + bean.getClass() + "bean的名字=" + beanName);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    /**
     * 发布过程初始化后调用
     *
     * @param bean     豆
     * @param beanName bean名字
     * @return {@link Object}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("后置处理器LHBeanPostProcessor 的after调用   bean的类型=" + bean.getClass() + "bean的名字=" + beanName);

        // 实现AOP 返回代理对象
        // 先死后活 之后再通过注解变灵活
        if ("smartDog".equals(beanName)) {
            // 返回代理对象 使用jdk的动态代理
            Object proxyInstance = Proxy.newProxyInstance(LHBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("method=" + method.getName());
                    Object res = null;
                    // 假如我们进行前置+返回通知处理的方法是getSum
                    if ("getSum".equals(method.getName())) {
                        SmartAnimalAspectj.showBeginLog();
                        res = method.invoke(bean, args);
                        // 进行返回通知的处理
                        SmartAnimalAspectj.showSuccessLog();
                    } else {
                        res = method.invoke(bean, args);
                    }
                    return res;
                }
            });

            // 如果bean是需要返回代理对象的
            return proxyInstance;
        }
            //如果不需要AOP处理 返回原生对象
        return bean;
    }
}
