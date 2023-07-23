package asia.lhweb.spring.aop;

import org.springframework.stereotype.Component;

@Component
public class SmartCat implements SmartAnimal {
    @Override
    public float getSum(float x, float y) {
        System.out.println("SmartCat-getSum() res="+x+y);
        return x+y;
    }

    @Override
    public float getSub(float x, float y) {
        System.out.println("SmartCat-getSum() res="+x*y);

        return x*y;
    }
}
