package asia.lhweb.spring.ioc;

/**
 * 用于封装/记录Bean的信息[1 scope 2 bean对应的class对象]
 * @author :罗汉
 * @date : 2023/7/23
 */
public class BeanDefinition {
    private String scope;
    private Class clazz;

    public BeanDefinition() {
    }

    public BeanDefinition(String scope, Class clazz) {
        this.scope = scope;
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "BeanDefiniton{" +
                "scope='" + scope + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}
