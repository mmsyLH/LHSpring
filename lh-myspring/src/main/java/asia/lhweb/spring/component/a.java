package asia.lhweb.spring.component;

import asia.lhweb.spring.processor.BeanPostProcessor;
import asia.lhweb.spring.processor.InitializingBean;

public class a  implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("a的初始化方法");//没有被扫描加入容器就会无效
    }
}
