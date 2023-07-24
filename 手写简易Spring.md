:::info
💡   初始化IOC容器+依赖注入+BeanPostProcessor机制+AOP
:::
GitHub地址：[https://github.com/1072344372/LHSpring](https://github.com/1072344372/LHSpring)
## 一图胜千言
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690105376424-62279913-fa51-45e3-8c6d-6d952bf96374.png#averageHue=%23e0f0d5&clientId=ufefc7b87-0e21-4&from=paste&height=689&id=bzSeW&originHeight=861&originWidth=1511&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=181561&status=done&style=none&taskId=u72590fb8-9a44-4682-8259-9329ba388b5&title=&width=1208.8)
## 1.实现阶段1
编写Spring容器，实现扫描包，得到bean的class对象
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690094983290-f6ec6246-32f9-4f61-8f6c-a2814b2fc8be.png#averageHue=%23f6dabe&clientId=ufefc7b87-0e21-4&from=paste&height=291&id=u085fb012&originHeight=364&originWidth=1093&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=20378&status=done&style=none&taskId=u711a7532-3f96-4468-917e-1f6eee23634&title=&width=874.4)
导包
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690097527510-0353f75c-4188-40a0-84f0-1ab17005560e.png#averageHue=%23fcfbfa&clientId=ufefc7b87-0e21-4&from=paste&height=387&id=hMI6J&originHeight=484&originWidth=575&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=60935&status=done&style=none&taskId=u47d4595c-86eb-4707-b5f1-daa789d0bff&title=&width=460)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690097585748-ec1cb612-e9ad-40c6-81d4-8257776f8ac4.png#averageHue=%23f8f8f6&clientId=ufefc7b87-0e21-4&from=paste&height=838&id=u685fbf46&originHeight=1048&originWidth=1920&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=240273&status=done&style=none&taskId=u5468f823-6d04-4990-a636-00756a4fe5c&title=&width=1536)
创建对应文件，注意导入的是自定义的注解，
```java
package asia.lhweb.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件扫描
 * 1 @Target(ElementType.TYPE)：
 * 指定ComponentScan注解可以修饰type类型
 * 2 @Retention(RetentionPolicy.RUNTIME)
 *  指定ComponentScan存活范围
 * 3 表示ComponentScan可以传入value属性
 * @author 罗汉
 * @date 2023/07/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {
    String value() default "";
}

```
Component、Controller、Repository、Service注解类似这里只展示Component注解
```java
package asia.lhweb.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件
 *
 * @author 罗汉
 * @date 2023/07/23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    //通过value可以给注入的bean指定id 名字
    String value() default "";
}

```
```java
package asia.lhweb.spring.component;

import asia.lhweb.spring.annotation.Service;

/**
 * MonsterService是一个service
 *  1 如果指定了那么在注入容器时以指定的为准
 *  2 如果没有指定则使用类名首字母小写的名字
 * @author 罗汉
 * @date 2023/07/23
 */
@Service(value = "monsterService")//把MonsterService注入到我们自己的容器中
public class MonsterService {

}

```
```java
package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.*;

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
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Service declaredAnnotation = clazz.getDeclaredAnnotation(Service.class);
                                String id = declaredAnnotation.value();
                                if (!StringUtils.isEmpty(id)){
                                    className=id;//替换
                                }
                            }
                            if (clazz.isAnnotationPresent(Component.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Component declaredAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String id = declaredAnnotation.value();
                                if (!StringUtils.isEmpty(id)){
                                    className=id;//替换
                                }
                            }
                            if (clazz.isAnnotationPresent(Controller.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Controller declaredAnnotation = clazz.getDeclaredAnnotation(Controller.class);
                                String id = declaredAnnotation.value();
                                if (!StringUtils.isEmpty(id)){
                                    className=id;//替换
                                }
                            }
                            if (clazz.isAnnotationPresent(Repository.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Repository declaredAnnotation = clazz.getDeclaredAnnotation(Repository.class);
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

```
```java
package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.ComponentScan;

/**
 * lhspring配置
 *  类似beans.xml  容器配置文件
 * @author 罗汉
 * @date 2023/07/17
 */
@ComponentScan(value = "asia.lhweb.spring.component")
public class LHSpringConfig {

}

```
测试方法
```java
import asia.lhweb.spring.ioc.LHSpringApplicationContext;
import asia.lhweb.spring.ioc.LHSpringConfig;

public class Text {
    public static void main(String[] args) {
        LHSpringApplicationContext ioc = new LHSpringApplicationContext(LHSpringConfig.class);
        
    }
}

```
## 2.实现阶段2
扫描将bean信息封装到BeanDefinition对象，并放入到Map
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690098177844-808edb32-0c8d-4c1e-814c-f3136b8d256f.png#averageHue=%23f8e6d1&clientId=ufefc7b87-0e21-4&from=paste&height=410&id=ubfa1aa76&originHeight=512&originWidth=1446&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=49124&status=done&style=none&taskId=uee6be3fa-6382-404b-9b08-ccdd195403e&title=&width=1156.8)


新增一个Scope注解
```java
package asia.lhweb.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以指定bean的作用范围【singleton,prototype】
 *
 * @author 罗汉
 * @date 2023/07/23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    //通过value可以指定是singleton还是prototype
    String value() default "";
}

```
默认为单例，在MonsterService添加这个注解
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690098630424-420f0f09-17eb-4146-af3f-34f961e7636c.png#averageHue=%23fdfcfa&clientId=ufefc7b87-0e21-4&from=paste&height=328&id=uc4a429c2&originHeight=410&originWidth=711&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=54324&status=done&style=none&taskId=u47fbe45e-3331-4526-9bcf-2d5a45f9de2&title=&width=568.8)

新建BeanDefiniton.java 用于封装/记录Bean的信息
```java
package asia.lhweb.spring.ioc;

/**
 * 用于封装/记录Bean的信息[1 scope 2 bean对应的class对象]
 * @author :罗汉
 * @date : 2023/7/23
 */
public class BeanDefiniton {
    private String scope;
    private Class clazz;

    public BeanDefiniton() {
    }

    public BeanDefiniton(String scope, Class clazz) {
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

```
修改LHSpringApplicationContext
```java
package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.*;
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
    private final ConcurrentHashMap<String, BeanDefiniton> beanDefinitonMap = new ConcurrentHashMap<>();//存放BeanDefiniton对象
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();//存放单例对象

    public LHSpringApplicationContext(Class configClass) {
        beanDefiniton(configClass);
        System.out.println("beanDefinitonMap="+beanDefinitonMap);
    }

    /**
     * 完成对指定包的扫描，并且封装到BeanDefinition对象，再放入到Map中
     *
     * @param configClass 配置类
     */
    private void beanDefiniton(Class configClass) {
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

                            if (clazz.isAnnotationPresent(Service.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                //将Bean的信息封装到BeanDefiniton中放入map
                                // 1 得到Service注解
                                Service declaredAnnotation = clazz.getDeclaredAnnotation(Service.class);
                                // 2 得到value值
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                            if (clazz.isAnnotationPresent(Component.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Component declaredAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                            if (clazz.isAnnotationPresent(Controller.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Controller declaredAnnotation = clazz.getDeclaredAnnotation(Controller.class);
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                            if (clazz.isAnnotationPresent(Repository.class)){
                                System.out.println("这是一个LHSpring bean="+clazz+"    类名="+className);
                                Repository declaredAnnotation = clazz.getDeclaredAnnotation(Repository.class);
                                String beanName = declaredAnnotation.value();
                                method(className, clazz, beanName);
                            }

                        }else {
                            System.out.println("这不是一个LHSpring bean="+clazz+"  类名="+className);
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
    private void method(String className, Class<?> clazz, String beanName) {
        if ("".equals(beanName)){//如果为空
            //首字母小写作为beanName
            beanName = StringUtils.uncapitalize(className);//替换
        }
        // 3 放入map
        BeanDefiniton beanDefiniton = new BeanDefiniton();
        beanDefiniton.setClazz(clazz);
        // 4 获取Scope值
        if ( clazz.isAnnotationPresent(Scope.class)){
            //如果配置了Scope
            Scope scopedeclaredAnnotation = clazz.getDeclaredAnnotation(Scope.class);
            beanDefiniton.setScope(scopedeclaredAnnotation.value());
        }else {
            //如果没有配置
            beanDefiniton.setScope("singleton");
        }
        //放入到map中
        beanDefinitonMap.put(beanName,beanDefiniton);
    }


}

```
测试结果
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690105068863-c6783297-e9d9-43d1-b150-dc4fd2f6d3a2.png#averageHue=%23fbfaf8&clientId=ufefc7b87-0e21-4&from=paste&height=165&id=u7e24e95c&originHeight=206&originWidth=1809&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=34837&status=done&style=none&taskId=ua6046f6c-02b9-4042-8b1e-30fb6a7c4a3&title=&width=1447.2)
## 3.实现阶段3
初始化bean单例池，并完成getBean方法，createBean方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690105376424-62279913-fa51-45e3-8c6d-6d952bf96374.png#averageHue=%23e0f0d5&clientId=ufefc7b87-0e21-4&from=paste&height=689&id=u94584252&originHeight=861&originWidth=1511&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=181561&status=done&style=none&taskId=u72590fb8-9a44-4682-8259-9329ba388b5&title=&width=1208.8)
在LHSpringApplicationContext中创建一个得到bean的方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690106886876-d3abf2fb-6893-41e5-abac-6c1129e3fcea.png#averageHue=%23fcfbf9&clientId=ufefc7b87-0e21-4&from=paste&height=358&id=u9fd27353&originHeight=447&originWidth=613&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=46693&status=done&style=none&taskId=u924b23d4-36b9-4c8b-9712-e52c966331b&title=&width=490.4)
修改方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690107876459-b41d4645-3699-4451-bc30-0203e56c1fa0.png#averageHue=%23fcfbf9&clientId=ufefc7b87-0e21-4&from=paste&height=445&id=u1db1b663&originHeight=556&originWidth=715&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=96842&status=done&style=none&taskId=ucdb0b5d3-ba2a-4e08-908b-285a072e1bb&title=&width=572)
到此已经完成![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690107579332-32756d32-8e09-45fc-9542-4d213a56da8a.png#averageHue=%23dfb185&clientId=ufefc7b87-0e21-4&from=paste&height=116&id=uc8a90173&originHeight=145&originWidth=186&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=7127&status=done&style=none&taskId=u127be032-31a5-49a5-b456-f1f26002e37&title=&width=148.8)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690107603418-526f50ee-c36f-42b4-8d13-2685e9252c37.png#averageHue=%23fcfaf9&clientId=ufefc7b87-0e21-4&from=paste&height=200&id=u6e613b02&originHeight=250&originWidth=1800&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=39733&status=done&style=none&taskId=u726fdddf-fd93-4c1c-a951-478ea3edad3&title=&width=1440)
写一个得到bean的方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690108037712-88b4c602-a7e8-451e-9055-6fb41c4689cf.png#averageHue=%23fcfbfa&clientId=ufefc7b87-0e21-4&from=paste&height=336&id=uc5414a04&originHeight=420&originWidth=572&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=66190&status=done&style=none&taskId=ub6282b60-f44a-4b21-bac9-37eaab6f5a4&title=&width=457.6)

```java
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

```
结果
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690108469679-92c75b32-1b57-4f72-8a71-65af64a12b0f.png#averageHue=%23f7f6f5&clientId=ufefc7b87-0e21-4&from=paste&height=838&id=X0gTN&originHeight=1047&originWidth=1918&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=196610&status=done&style=none&taskId=u915ea2b7-40b3-41d1-9a8b-006b7c91a85&title=&width=1534.4)
可以自行测试多次获取是否存在的bean名以及是否单例。
## 4.实现阶段4-完成依赖注入
新添加一个注解类
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690121134881-1f7be6a7-95af-4655-8ec6-895d9f95374d.png#averageHue=%23f8f7f6&clientId=ufefc7b87-0e21-4&from=paste&height=572&id=u1af0ecf0&originHeight=715&originWidth=873&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=101027&status=done&style=none&taskId=u77341816-bf36-4900-bf25-1e0909ac4ea&title=&width=698.4)
```java
package asia.lhweb.spring.annotation;

import java.lang.annotation.*;

/**
 * 自动装配注解
 *
 * @author 罗汉
 * @date 2023/07/23
 */

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    //一定要找到一个匹配的找不到就会报错
    // boolean required() default true;
}

```
 ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690121262298-bb27b3ad-8af7-48d6-ae43-162124054198.png#averageHue=%23f9f8f7&clientId=ufefc7b87-0e21-4&from=paste&height=566&id=u3d1a7336&originHeight=708&originWidth=1864&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=308056&status=done&style=none&taskId=u75de8b5e-7844-4cd0-b0c6-601095fe33a&title=&width=1491.2)
修改createBean方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690121940339-6870f263-1c18-4fae-bb5b-a26cab432cf6.png#averageHue=%23fcfaf8&clientId=ufefc7b87-0e21-4&from=paste&height=492&id=u0e4f3c5c&originHeight=615&originWidth=974&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=117296&status=done&style=none&taskId=u5a64cc09-7c38-48de-a7f1-d1567524b2d&title=&width=779.2)
```java
package asia.lhweb.spring.ioc;

import asia.lhweb.spring.annotation.*;
import org.springframework.util.StringUtils;


import java.io.File;
import java.lang.reflect.Field;
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
                Object bean = createBean(beanDefinition);
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
                return createBean(beanDefinition);
            }
        } else {// 不存在
            // 抛出个空指针异常
            throw new NullPointerException("没有该bean");

        }

    }
}

```
结果展示
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690121995493-38af1857-0162-4ad4-891a-35290c4f3d82.png#averageHue=%23f9f8f7&clientId=ufefc7b87-0e21-4&from=paste&height=299&id=ufe774387&originHeight=374&originWidth=1882&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=64601&status=done&style=none&taskId=u5877cca9-e488-4d85-b064-37b87b5afc2&title=&width=1505.6)
## 5.实现阶段5-bean后置处理器实现（相对复杂）
根据原生spring定义一个接口
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690182867261-615992f9-2ce3-4d20-aec9-5ce0503d4db1.png#averageHue=%23fdfdfb&clientId=ud937a302-abd9-4&from=paste&height=283&id=u8fb3bec2&originHeight=354&originWidth=801&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=48365&status=done&style=none&taskId=u480b2818-1949-4200-b057-cfc79d986b3&title=&width=640.8)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690182955165-5c2efb78-c4ca-4bc7-887b-989b8ad7d274.png#averageHue=%23f9f8f8&clientId=ud937a302-abd9-4&from=paste&height=834&id=u2bc91606&originHeight=1042&originWidth=1919&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=222119&status=done&style=none&taskId=u6597c463-ce6a-4c57-820f-63ff00ea500&title=&width=1535.2)
搞定初始化方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690189341558-178d59af-6255-45b2-929d-86f429e873cc.png#averageHue=%23f6f5f3&clientId=ud937a302-abd9-4&from=paste&height=832&id=ua29108aa&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=299681&status=done&style=none&taskId=ud912d878-5bb9-412c-85f0-cae075e5a09&title=&width=1536)
模仿spring新建一个接口BeanPostProcessor.java
```java
package asia.lhweb.spring.processor;

import org.springframework.beans.BeansException;
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
    @Nullable
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

```
写一个自己的后置处理器（目前还是普通的bean）
```java
package asia.lhweb.spring.component;

import asia.lhweb.spring.annotation.Component;
import asia.lhweb.spring.processor.BeanPostProcessor;

/**
 * lhbean后置处理程序
 * 可以写里面的方法
 *
 * 在spring容器中仍然把后置处理器当成一个bean对待
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
        System.out.println("后置处理器LHBeanPostProcessor 的before调用 bean的类型="+
                bean.getClass()+"bean的名字="+beanName);
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
        System.out.println("后置处理器LHBeanPostProcessor 的after调用   bean的类型="+
                bean.getClass()+"bean的名字="+beanName);
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

```
修改beanDefinitonByScan方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690190409242-b8191222-9762-40b0-9f91-e101be809ddb.png#averageHue=%23fcfbf6&clientId=ud937a302-abd9-4&from=paste&height=84&id=u048810f3&originHeight=105&originWidth=892&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=15888&status=done&style=none&taskId=u60298881-1f1e-4076-98a7-61201517d6c&title=&width=713.6)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690190397528-74cdeb69-616d-40f7-855f-6b5ad06f169e.png#averageHue=%23f8f7f6&clientId=ud937a302-abd9-4&from=paste&height=832&id=u443bcd02&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=309612&status=done&style=none&taskId=u5c373081-8e8c-4b32-b60b-cb78caf9a9b&title=&width=1536)
修改createBean方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690191114738-6c2b7872-fe87-40a7-a908-6bf87fd75444.png#averageHue=%23f8f7f5&clientId=ud937a302-abd9-4&from=paste&height=832&id=ue1f15f18&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=288115&status=done&style=none&taskId=ub770d8b0-1141-4a1f-a058-d42d50d128a&title=&width=1536)
新建一个car测试
```java
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

```
```java
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

```
演示效果
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690195192276-47c92af2-4161-4ee6-be4e-cbb67affdcc9.png#averageHue=%23f7f6f4&clientId=u358616f1-d2c8-4&from=paste&height=310&id=ubd9ea081&originHeight=388&originWidth=1126&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=58956&status=done&style=none&taskId=uebd7304e-61ec-41a9-bfd5-5b3d77bb816&title=&width=900.8)
## 6.实现阶段6-AOP机制实现
先死后活，先实现一个简单的切面类
新建一个接口和实现类
```java
package asia.lhweb.spring.component;

/**
 * @author :罗汉
 * @date : 2023/7/24
 */
public interface SmartAnimalable {
    float getSum(float x,float y);
    float getSub(float x,float y);
}
```
```java
package asia.lhweb.spring.component;

import asia.lhweb.spring.annotation.Component;

/**
 * @author :罗汉
 * @date : 2023/7/24
 */
@Component(value = "smartDog")
public class SmartDog implements SmartAnimalable{
    @Override
    public float getSum(float x, float y) {
        System.out.println("SmartDog getSum");
        return x+y;
    }

    @Override
    public float getSub(float x, float y) {
        System.out.println("SmartDog getSub");
        return x*y;
    }
}
```
```java
package asia.lhweb.spring.component;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 切面类
 *
 *
 * @author 罗汉
 * @date 2023/07/24
 */

public class SmartAnimalAspectj {

    public SmartAnimalAspectj() {
    }

    public static void showBeginLog(){
        System.out.println("前置类");
    }
    public static void showSuccessLog(){
        System.out.println("返回通知");
    }

}
```
修改方法
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690206529271-2b310374-9dc9-4e7b-b4ca-8cbbd155a755.png#averageHue=%23fdfcfb&clientId=u358616f1-d2c8-4&from=paste&height=598&id=u35aaebca&originHeight=748&originWidth=939&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=114959&status=done&style=none&taskId=u43874fd4-de83-456f-8fc3-aa83ed928d8&title=&width=751.2)
测试
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1690206903750-187b9ba2-8b80-4b20-a3c8-04d13611673d.png#averageHue=%23f8f8f7&clientId=u358616f1-d2c8-4&from=paste&height=832&id=uc83e8b6a&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=215273&status=done&style=none&taskId=u58a17728-33ee-4f8f-a711-7c6b2a63c17&title=&width=1536)
## 总结
> AOP可以使用注解会更灵活，暂时先做到这里 有机会我再更新



