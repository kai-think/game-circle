package com.kai.common.utils.converter;


import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CusConverter {

    @SneakyThrows
    public static Object convert(Object from, Class<?> toCls) {
        Object to = toCls.getConstructor().newInstance();
        assign(from, to);

        return to;
    }

    @SneakyThrows
    public static Object convert(List<Object> fromList, Class<?> toCls) {
        Object to = toCls.getConstructor().newInstance();
        for (Object from : fromList)
            assign(from, to);

        return to;
    }

    @SneakyThrows
    public static void convert(Object from, Object ...tos) {
        for (Object to : tos)
            assign(from, to);
    }

    @SneakyThrows
    public static void assign(Object from, Object to) {
        Class<?> fromCls = from.getClass();
        Class<?> toCls = to.getClass();

        Field[] fromFields = fromCls.getDeclaredFields();
        Map<String, Field> toFieldMap = Arrays.stream(toCls.getDeclaredFields())
                .collect(HashMap::new,
                        (map, f) -> map.put(f.getName(), f),
                        Map::putAll);

        //根据from的属性，填充到to
        //如果from属性找不到对应的to属性，则从 from 的别名中查找
        for (Field fromField : fromFields) {
            String fName = fromField.getName();
            boolean ignore = fromField.getAnnotation(ConvertIgnore.class) != null;
            //跳过被忽略的属性
            if (ignore)
                continue;

            //查找对应fromField 的 toField 属性
            Field toField = toFieldMap.get(fName);
            if (toField == null)
            {
                ConvertAlias alias = fromField.getAnnotation(ConvertAlias.class);
                if (alias == null)
                    continue;

                String[] fNames = alias.value();
                for (String fName2 : fNames) {
                    toField =  toFieldMap.get(fName2);
                    if (toField != null)
                        break;
                }
            }

            if (toField == null)    //实在找不到属性，就跳过
                continue;

            //到这里，可以保证 fromField 和 toField 有值
            //移除，避免重复赋值
            toFieldMap.remove(toField.getName());

            //两个类型不同，则抛出异常
            if (!fromField.getType().equals(toField.getType()))
                throw new ClassCastException(String.format("%s 类型不同于 %s ", fromField.toString(), toField.toString()));

            //得到to的get方法
            String name = toField.getName();
            String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            Method method;
            try {
                method = toCls.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                continue;
            }
            Object obj = method.invoke(to);
            if (obj != null)    //当前toField有值，则跳过
                continue;

            //得到to的set方法
            name = toField.getName();
            methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

            try {
                method = toCls.getMethod(methodName, toField.getType());
            } catch (NoSuchMethodException e) {
                continue;
            }

            //得到from的get方法
            name = fromField.getName();
            methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            Method getMethod;
            try {
                getMethod = fromCls.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                continue;
            }

            //设置值
            method.invoke(to, getMethod.invoke(from));
        }
    }
}
