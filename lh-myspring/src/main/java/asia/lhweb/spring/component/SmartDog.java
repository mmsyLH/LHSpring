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
