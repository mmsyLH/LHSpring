package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.*;
import asia.lhweb.spring.processor.BeanPostProcessor;
import asia.lhweb.spring.processor.InitializingBean;
import org.springframework.util.StringUtils;


import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

    //定义一个属性 用来存放后置处理器
    private  List<BeanPostProcessor> beanPostProcessorList=new ArrayList<BeanPostProcessor>();

    public LHSpringApplicationContext(Class configClass) {
        // 完成扫描
        beanDefinitonByScan(configClass);

        // 通过beanDefinitonMap，初始化单例池 singletonObjects
        System.out.println("beanDefinitonMap=" + beanDefinitonMap);
        Enumeration<String> keys = beanDefinitonMap.keys();// kes指的是全部bean的名字
        while (keys.hasMoreElements()) {
            // 得到beanName
            String beanName = keys.nextElement();
            // System.out.println(beanName);

            // 通过BeanName 得到对应的beanDefinition对象
            BeanDefinition beanDefinition = beanDefinitonMap.get(beanName);
            // System.out.println(beanDefinition);
            // 判断该bean是singleton还是prototype
            if ("singleton".equalsIgnoreCase(beanDefinition.getScope())) {// 是单例
                // 将该bean实例放入到singletonObjects
                Object bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName, bean);
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
                        if (clazz.isAnnotationPresent(Service.class) || (clazz.isAnnotationPresent(Component.class)) || (clazz.isAnnotationPresent(Controller.class)) || (clazz.isAnnotationPresent(Repository.class))) {

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

                                //为了方便 将后置处理器放到list集合中
                                //1 如果发现是一个后置处理器
                                //2 在原生spring容器中，对后置处理器还是走的getBean，createBean
                                //但是需要再单例池中加对应的逻辑，这里只是为了体验 所以直接放入到list集合中

                                //判断是否实现是后置处理器
                                //这里不能使用 instanceof 来判断 原因：clazz不是一个实例对象，而是一个类对象
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)){
                                    BeanPostProcessor instance = (BeanPostProcessor) clazz.newInstance();
                                    //放入到集合中
                                    beanPostProcessorList.add(instance);
                                    continue;
                                }

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
    private Object createBean(String beanName,BeanDefinition beanDefinition) {
        // 得到Bean的clazz对象
        Class clazz = beanDefinition.getClazz();
        try {
            // 使用反射得到实例
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 加入依赖注入的业务逻辑
            // 1 遍历当前要创建的对象的所有字段
            for (Field declaredField : clazz.getDeclaredFields()) {
                // 2 判断这个字段是否有Autowired注解修饰
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    // 3得到字段的名字
                    String name = declaredField.getName();
                    // 4 通过getBean方法来获取要组装的对象
                    Object bean = getBean(name);
                    // 5 进行组装
                    // 因为属性是私有的不能反射 所以需要爆破
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);// 第一个是需要组装的对象  第二个参数是你要组装的东西
                }
            }
            System.out.println();

            System.out.println("=========创建好bean====="+instance);

            //在bean的初始化方法前调用后置处理器方法
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                //在后置处理器的before方法前可以对Bean进行处理 ， 然后再返回处理后的bean
                //相当于做了一个前置处理
                Object current=
                        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
                if (current!=null){
                    instance=current;
                }
            }

            //这里判断是执行bean的初始化方法
            // 1 判断当前创建的bean对象是否实现了InitializingBean接口
            if (instance instanceof InitializingBean){
                // 2 将instance转成InitializingBean类型
                try {
                    ((InitializingBean)instance).afterPropertiesSet();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            //在bean的初始化方法后调用后置处理器方法
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                //在后置处理器的after方法前可以对Bean进行处理 ， 然后再返回处理后的bean
                //相当于做了后置处理
                Object current=
                        beanPostProcessor.postProcessAfterInitialization(instance, beanName);
                if (current!=null){
                    instance=current;
                }
            }

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
        // 判断 传入的beanName是否在beanDefinitonMap中存在
        if (beanDefinitonMap.containsKey(beanName)) {// 存在
            BeanDefinition beanDefinition = beanDefinitonMap.get(beanName);
            // 得到beanDefinition的scope，分别进行处理
            if ("singleton".equalsIgnoreCase(beanDefinition.getScope())) {
                // 说明是单例的，就直接从单例池获取
                return singletonObjects.get(beanName);
            } else {// 不是单例就调用creatBean，反射一个对象
                return createBean(beanName,beanDefinition);
            }
        } else {// 不存在
            // 抛出个空指针异常
            throw new NullPointerException("没有该bean");

        }

    }
}
