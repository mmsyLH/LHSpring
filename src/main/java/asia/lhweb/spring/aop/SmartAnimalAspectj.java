package asia.lhweb.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 切面类
 *
 * @author 罗汉
 * @date 2023/07/21
 */
@Aspect//表示是一个切面类
@Component
@Order(value = 3)//越大优先级越低
public class SmartAnimalAspectj {
    //定义一个切入点，在后面使用时可以直接引用，提高了复用性
    @Pointcut(value = "execution(public float asia.lhweb.spring.aop.SmartCat.getSum(float , float ))")
    public void myPointCut(){

    }

    // @Before(value ="execution(public float xyz.lhweb.spring.aop.aspectj.SmartDog.getSum(float , float ))")
    //使用切入点表达式
    @Before(value = "myPointCut()")
    public static void f1(JoinPoint joinPoint){
        //通过连接点对象 joinPoint获取方法签名
        Signature signature = joinPoint.getSignature();
        System.out.println("切面1-调用方法前-日志-方法名"+signature.getName()+"-参数"+ Arrays.asList(joinPoint.getArgs()));
    }
    // @AfterReturning(value ="execution(public float xyz.lhweb.spring.aop.aspectj.SmartDog.getSum(float , float ))",returning = "res")
    @AfterReturning(value = "myPointCut()",returning = "res")
    public static void f2( JoinPoint joinPoint,Object res){
        //通过连接点对象 joinPoint获取方法签名
        Signature signature = joinPoint.getSignature();
        // System.out.println("调用方法前-日志-方法名"+signature.getName()+"-参数"+ Arrays.asList(joinPoint.getArgs()));
        System.out.println("切面1-调用方法后-日志-方法名"+signature.getName()+"-结果res:"+res);
    }
    @AfterThrowing(value ="execution(public float asia.lhweb.spring.aop.SmartCat..getSum(float , float ))",throwing="throwable")
    public static void f3( JoinPoint joinPoint,Throwable throwable){
        Signature signature = joinPoint.getSignature();
        System.out.println("切面1-调用方法后-日志-异常执行方法"+signature.getName()+"-异常信息:"+throwable);
    }
    public SmartAnimalAspectj() {
    }
}
