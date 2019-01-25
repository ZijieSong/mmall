package com.mmall;

import org.junit.Test;

public class ClassTest {
    private static int i=0;
    private int j =0;
    @Test
    public void testClass(){
        new ClassTest().i++;
        new ClassTest().j++;
        System.out.println(new ClassTest().i);
        System.out.println(new ClassTest().j);
    }
}
