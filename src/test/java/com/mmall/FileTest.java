package com.mmall;

import org.junit.Test;

import java.io.File;

public class FileTest {

    @Test
    public void fileTest(){
        File file1 = new File("/usr/local/a.jpg");
        File file2 = new File("/usr/local","a.jpg");
        System.out.println(file1.getName());
        System.out.println(file2.getName());
    }
}
