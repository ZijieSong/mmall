package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    public static BigDecimal add(Double a, Double b){
        return new BigDecimal(a.toString()).add(new BigDecimal(b.toString()));
    }

    public static BigDecimal subtract(Double a, Double b){
        return new BigDecimal(a.toString()).subtract(new BigDecimal(b.toString()));
    }

    public static BigDecimal multiply(Double a, Double b){
        return new BigDecimal(a.toString()).multiply(new BigDecimal(b.toString()));
    }

    public static BigDecimal divide (Double a, Double b){
        return new BigDecimal(a.toString()).divide(new BigDecimal(b.toString()));
    }
}
