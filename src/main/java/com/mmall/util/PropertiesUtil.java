package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {

    private static Properties prop;

    static {
        String filename = "mmall.properties";
        prop = new Properties();
        try {
            prop.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(filename)));
        } catch (IOException e) {
            log.error("配置文件读取异常", e);
        }
    }

    public static String get(String key) {
        String value = prop.getProperty(key.trim());
        if (StringUtils.isBlank(value))
            return null;
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        return get(key) == null ? defaultValue : get(key);
    }
}
