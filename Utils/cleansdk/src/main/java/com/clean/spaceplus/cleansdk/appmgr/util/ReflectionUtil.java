//package com.clean.spaceplus.cleansdk.appmgr.util;
//
//import java.lang.reflect.Field;
//
///**
// * @author wangtianbao
// * @Description: 反射工具
// * @date 2016/4/23 11:17
// * @copyright TCL-MIG
// */
//public class ReflectionUtil {
//
//    /**
//     * 获取变量里的隐形变量
//     * @param variable
//     * @param fieldName
//     * @return
//     */
//    public static Object getField(Object variable, String fieldName){
//        Field fieldX = null;
//        Object object = null;
//        try {
//            fieldX = variable.getClass().getDeclaredField(fieldName);
//            fieldX.setAccessible(true);
//            object = fieldX.get(variable);
//        } catch (NoSuchFieldException e1) {
//            e1.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return object;
//    }
//}
