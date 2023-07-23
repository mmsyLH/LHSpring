package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.*;
import org.springframework.util.StringUtils;


import java.io.File;
import java.net.URL;
import java.util.Enumeration;
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
    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitonMap = new ConcurrentHashMap<>();// 存放BeanDefiniton对象
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();// 存放单例对象

    public LHSpringApplicationContext(Class configClass) {
        //完成扫描
        beanDefinitonByScan(configClass);

        //通过beanDefinitonMap，初始化单例池 singletonObjects
        System.out.println("beanDefinitonMap=" + beanDefinitonMap);
        Enumeration<String> keys = beanDefinitonMap.keys();//kes指的是全部bean的名字
        while (keys.hasMoreElements()){
            //得到beanName
            String beanName = keys.nextElement();
            // System.out.println(beanName);

            //通过BeanName 得到对应的beanDefinition对象
            BeanDefinition beanDefinition = beanDefinitonMap.get(beanName);
            // System.out.println(beanDefinition);
            //判断该bean是singleton还是prototype
            if ("singleton".equalsIgnoreCase(beanDefinition.getScope())){//是单例
                //将该bean实例放入到singletonObjects
                Object bean = createBean(beanDefinition);
                singletonObjects.put(beanName,bean);
            }

        }
        System.out.println(singletonObjects);
    }

    /**
     * 完成对指定包的扫描，并且封装到BeanDefinition对象，再放入到Map中
     *
     * @param configClass 配置类
     */
    public void beanDefinitonByScan(Class configClass) {
        this.configClass = configClass;
        // System.out.println("配置的路径：this.configClass =" + configClass);
        // 1得到注解
        ComponentScan componentScan = (ComponentScan) this.configClass.getAnnotation(ComponentScan.class);
        // 2通过componentScan得到要扫描的包
        String path = componentScan.value();
        // System.out.println("要扫码的包为:path=" + path);

        // 1 得到类加载器->APP 类加载器
        ClassLoader classLoader = LHSpringApplicationContext.class.getClassLoader();
        // 2 获取扫描包的url
        path = path.replace(".", "/");
        URL resource = classLoader.getResource(path);
        // System.out.println(resource);
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

                            if (clazz.isAnnotationPresent(Service.class)) {
                                System.out.println("这是一个LHSpring bean=" + clazz + "    类名=" + className);
                                // 将Bean的信息封装到BeanDefiniton中放入map
                                // 1 得到Service注解
                                Service declaredAnnotation = clazz.getDeclaredAnnotation(Service.class);
                                // 2 得到value值
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                            if (clazz.isAnnotationPresent(Component.class)) {
                                System.out.println("这是一个LHSpring bean=" + clazz + "    类名=" + className);
                                Component declaredAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                            if (clazz.isAnnotationPresent(Controller.class)) {
                                System.out.println("这是一个LHSpring bean=" + clazz + "    类名=" + className);
                                Controller declaredAnnotation = clazz.getDeclaredAnnotation(Controller.class);
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                            if (clazz.isAnnotationPresent(Repository.class)) {
                                System.out.println("这是一个LHSpring bean=" + clazz + "    类名=" + className);
                                Repository declaredAnnotation = clazz.getDeclaredAnnotation(Repository.class);
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                        } else {
                            System.out.println("这不是一个LHSpring bean=" + clazz + "  类名=" + className);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }

    /**
     * 共同方法提取出来方法
     *
     * @param className 类名
     * @param clazz     clazz
     * @param beanName  bean名字
     */
    public void method(String className, Class<?> clazz, String beanName) {
        if ("".equals(beanName)) {// 如果为空
            // 首字母小写作为beanName
            beanName = StringUtils.uncapitalize(className);// 替换
        }
        // 3 放入map
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setClazz(clazz);
        // 4 获取Scope值
        if (clazz.isAnnotationPresent(Scope.class)) {
            // 如果配置了Scope
            Scope scopedeclaredAnnotation = clazz.getDeclaredAnnotation(Scope.class);
            beanDefinition.setScope(scopedeclaredAnnotation.value());
        } else {
            // 如果没有配置
            beanDefinition.setScope("singleton");
        }
        // 放入到map中
        beanDefinitonMap.put(beanName, beanDefinition);
    }

    /**
     * 创建bean
     *
     * @param beanDefinition bean定义
     */
    private Object createBean(BeanDefinition beanDefinition) {
        // 得到Bean的clazz对象
        Class clazz = beanDefinition.getClazz();

        try {
            // 使用反射得到实例
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果反射创建对象失败
        return null;
    }

    /**
     * 得到bean
     *
     * @param beanName bean名字
     * @return {@link Object}
     */
    public Object getBean(String beanName) {
        //判断 传入的beanName是否在beanDefinitonMap中存在
        if (beanDefinitonMap.containsKey(beanName)){//存在
            BeanDefinition beanDefinition = beanDefinitonMap.get(beanName);
            //得到beanDefinition的scope，分别进行处理
            if ("singleton".equalsIgnoreCase(beanDefinition.getScope())){
                //说明是单例的，就直接从单例池获取
                return singletonObjects.get(beanName);
            }else {//不是单例就调用creatBean，反射一个对象
                return createBean(beanDefinition);
            }
        }else {//不存在
            //抛出个空指针异常
            throw  new NullPointerException("没有该bean");

        }

    }
}
