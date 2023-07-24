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
        System.out.println("前置通知");
    }
    public static void showSuccessLog(){
        System.out.println("返回通知");
    }

}
