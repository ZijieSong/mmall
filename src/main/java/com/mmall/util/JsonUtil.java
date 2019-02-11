package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //在序列化时会把所有字段都包含进去
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认转换timestamps（时间戳）形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.DEFAULT_PATTERN));
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String objToString(Object object) {
        if (object == null)
            return null;
        try {
            return object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            log.warn("Object to String error!", e);
            return null;
        }
    }

    public static String objToStringPretty(Object object) {
        if (object == null)
            return null;
        try {
            return object instanceof String ? (String) object : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            log.warn("Object to String error!", e);
            return null;
        }
    }

    public static <T> T stringToObject(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null)
            return null;
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.warn("String to Object error!", e);
            return null;
        }
    }

    public static <T> T stringToObject(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null)
            return null;
        try {
            return typeReference.getType().equals(String.class) ? (T) str : objectMapper.readValue(str, typeReference);
        } catch (IOException e) {
            log.warn("String to Object error!", e);
            return null;
        }
    }

    public static <T> T stringToObject(String str, Class<?> collectionClazz, Class<?>... elementsClazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementsClazz);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("String to Object error!", e);
            return null;
        }
    }


    public static void main(String[] args) {
        User user = new User();
        user.setUsername("szj");
        user.setId(1);
        user.setUpdateTime(new Date());

        String userString = JsonUtil.objToString(user);
        String userStringPretty = JsonUtil.objToStringPretty(user);

        log.info("userString: {}", userString);
        log.info("userStringPretty: {}", userStringPretty);

        User user1 = JsonUtil.stringToObject(userString, User.class);
        User user2 = JsonUtil.stringToObject(userString, new TypeReference<User>() {
        });

        user1.setId(2);
        user2.setId(3);

        List<User> userList = Lists.newArrayList();
        userList.add(user);
        userList.add(user1);
        userList.add(user2);

        String userListStr = JsonUtil.objToStringPretty(userList);
        log.info("userListString: {}", userListStr);
        List<User> list1 = JsonUtil.stringToObject(userListStr, new TypeReference<List<User>>() {
        });
        List<User> list2 = JsonUtil.stringToObject(userListStr, List.class, User.class);


        String str = "abc";
        log.info("String: {}", JsonUtil.objToString(str));
        log.info("Object: {}", JsonUtil.stringToObject(str, String.class));

        log.info("end========");


    }
}
