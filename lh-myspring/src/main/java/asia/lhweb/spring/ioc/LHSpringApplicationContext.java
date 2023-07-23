package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.Component;
import asia.lhweb.spring.annotation.ComponentScan;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lhspring应用程序上下文
 *
 * @author 罗汉
 * @date 2023/07/23
 */
public class LHSpringApplicationContext {
    private Class configClass;
    // 基于注解
    private final ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();

    public LHSpringApplicationContext(Class configClass) {
        this.configClass = configClass;
        System.out.println("配置的路径：this.configClass =" + configClass);
        // 1得到注解
        ComponentScan componentScan = (ComponentScan) this.configClass.getAnnotation(ComponentScan.class);
        // 2通过componentScan得到要扫描的包
        String path = componentScan.value();
        System.out.println("要扫码的包为:path=" + path);

        // 1 得到类加载器->APP 类加载器
        ClassLoader classLoader = LHSpringApplicationContext.class.getClassLoader();
        // 2 获取扫描包的url
        path = path.replace(".", "/");
        URL resource = classLoader.getResource(path);
        System.out.println(resource);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                // System.out.println(file1);
                String absolutePath = file1.getAbsolutePath();
                // 只处理class文件
                if (absolutePath.endsWith(".class")) {
                    // 绝对路径：---F:\JavaWorksparce\Spring\out\production\springTest\xyz\lhweb\spring\component\MyComponent.class
                    // xyz.lhweb.spring.component.MyComponent

                    // 1 获取类名
                    String className = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.indexOf(".class"));
                    // System.out.println(className);

                    // 2 获取类的完整路径
                    String classFullName = path.replace("/", ".") + "." + className;
                    System.out.println(classFullName);

                    // 3 判断该类是不是需要注入到容器中
                    try {
                        // 反射一个类对象
                        // 1 Class.forName 调用该类的静态方法
                        // 2 classLoader.loadClass 不会调用该类的静态方法
                        // 3 isAnnotationPresent判断该类是否有这个注解
                        Class<?> clazz = classLoader.loadClass(classFullName);
                        if (clazz.isAnnotationPresent(Service.class)
                                || (clazz.isAnnotationPresent(Component.class))
                                || (clazz.isAnnotationPresent(Controller.class))
                                || (clazz.isAnnotationPresent(Repository.class))) {

                            if (clazz.isAnnotationPresent(Service.class)){
                                Service declaredAnnotation = clazz.getDeclaredAnnotation(Service.class);
                                String id = declaredAnnotation.value();
                                if (!StringUtils.isEmpty(id)){
                                    className=id;//替换
                                }
                            }
                            if (clazz.isAnnotationPresent(Component.class)){
                                System.out.println("这是一个Spring bean="+clazz);
                                Component declaredAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String id = declaredAnnotation.value();
                                if (!StringUtils.isEmpty(id)){
                                    className=id;//替换
                                }
                            }

                        }else {
                            System.out.println("这不是一个Spring bean="+clazz);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }
    //返回容器对象
    public Object getBean(String name) {
        return ioc.get(name);
    }
}
