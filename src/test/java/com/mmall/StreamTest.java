package com.mmall;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class StreamTest {
    @Test
    public void test(){
        List<Integer> list1 = Lists.newArrayList(1,2,3,4,5);
        List<String> list2 = list1.stream().map(item->"abc"+item+",").collect(Collectors.toList());
        list1.forEach(System.out::print);
        System.out.println();
        list2.forEach(System.out::print);
    }
}
