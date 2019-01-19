package com.mmall;

import com.mmall.util.BigDecimalUtil;
import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {
    @Test
    public void bigDecimalTest(){
        Double a = 2.02;
        Double b = 3.01;
        System.out.println(a+b);
        System.out.println(new BigDecimal(a.toString()));
        System.out.println(new BigDecimal(b.toString()));
        System.out.println(new BigDecimal(a.toString()).add(new BigDecimal(b.toString())));
    }

    @Test
    public void bigDecimalUtil(){
        System.out.println(2.02+3.01);
        System.out.println(BigDecimalUtil.add(2.02,3.01));
    }
}
