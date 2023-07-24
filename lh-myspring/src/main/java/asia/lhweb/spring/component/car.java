package asia.lhweb.spring.component;

import asia.lhweb.spring.annotation.Component;
import asia.lhweb.spring.processor.InitializingBean;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

/**
 * @author :罗汉
 * @date : 2023/7/24
 */
@Component
public class car implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("car的初始化方法");
    }
}
