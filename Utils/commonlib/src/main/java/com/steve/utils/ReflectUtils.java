package com.steve.utils;

import com.steve.commonlib.DebugLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　>      <　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　⌒　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  护码神兽
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * <p>
 * Created by Steve on 17/8/14.
 * <p>
 * ━━━━━━感觉萌萌哒━━━━━━
 *
 * 注意，普通内部类和静态内部类，反射时的区别
 * Class<?> clazz = ReflectUtils.getClazz("com.yuanhh.model.Outer$StaticInner");
 * Class<?> clazz = ReflectUtils.getClazz("com.yuanhh.model.Outer$Inner");
 */

public class ReflectUtils {


    /**
     * 实例化获取类名对应的类
     *
     * Object obj = ReflectUtils.newInstance("com.yuanhh.model.Outer");  //实例化对象
     *
     * @param clazz           类
     * @param constructorArgs 构造函数的各个参数
     * @return 实例化对象
     */
    public static Object newInstance(Class clazz, Object... constructorArgs) {
        if (clazz == null) {
            return null;
        }

        Object object = null;

        try {
            Constructor constructor = clazz.getDeclaredConstructor(obj2class(constructorArgs));
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            object = constructor.newInstance(constructorArgs);

        } catch (Exception e) {
            DebugLog.printException(e);
        }

        return object;
    }


    /**
     * 实例化获取类名对应的类
     *
     * Object obj = ReflectUtils.newInstance("com.yuanhh.model.Outer");  //实例化对象
     *
     * @param clazzName
     * @param constructorArgs
     * @return
     */
    public static Object newInstance(String clazzName,Object... constructorArgs){
        Class<?> clazz = getClazz(clazzName);

        if (clazz == null)
            return null;

        Object object = null;

        try {
            Constructor constructor = clazz.getDeclaredConstructor(obj2class(constructorArgs));
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            object = constructor.newInstance(constructorArgs);

        } catch (Exception e) {
            DebugLog.printException(e);
        }

        return object;
    }




    /**
     * 反射调用方法
     *
     * 普通类
     * Object obj = ReflectUtils.newInstance("com.yuanhh.model.Outer");  //实例化对
     * ReflectUtils.invokeMethod(obj, "outerMethod");  //无参方法
     * ReflectUtils.invokeMethod(obj, "outerMethod", "yuanhh"); //有参方法
     *
     * @param object     反射调用的对象实例
     * @param methodName 反射调用的对象方法名
     * @param methodArgs 反射调用的对象方法的参数列表
     * @return 反射调用执行的结果
     */
    public static Object invokeMethod(Object object, String methodName,
                                      Object... methodArgs) {
        if (object == null) {
            return null;
        }

        Object result = null;
        Class<?> clazz = object.getClass();
        try {
            Method method = clazz.getDeclaredMethod(methodName, obj2class(methodArgs));
            if (method != null) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);  //当私有方法时，设置可访问
                }
                result = method.invoke(object, methodArgs);
            }
        } catch (Exception e) {
            DebugLog.printException(e);
        }

        return result;

    }

    /**
     * 反射调用静态方法
     * @param clazz
     * @param methodName
     * @param methodArgs
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String methodName, Object... methodArgs){
        if (clazz == null)
            return null;

        Object result = null;

        try {
            Method method = clazz.getDeclaredMethod(methodName, obj2class(methodArgs));
            if (method != null) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);  //当私有方法时，设置可访问
                }
                result = method.invoke(null, methodArgs);
            }
        } catch (Exception e) {
            DebugLog.printException(e);
        }
        return result;
    }

    /**
     * 反射调用，获取属性值
     *
     * ReflectUtils.getField(obj, "outerField");  //get操作
     *
     * @param object    操作对象
     * @param fieldName 对象属性
     * @return 属性值
     */
    public static Object getField(Object object, String fieldName) {
        if (object == null) {
            return null;
        }

        Object result = null;
        Class<?> clazz = object.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                result = field.get(object);
            }
        } catch (Exception e) {
            DebugLog.printException(e);
        }
        return result;
    }


    /**
     * 反射调用，修改属性值
     *
     * ReflectUtils.setField(obj, "outerField", "new value"); //set操作
     *
     * @param object
     * @param fieldName
     * @param fieldValue
     */
    public static boolean setField(Object object, String fieldName, Object fieldValue){
        if (object == null)
            return false;

        Class<?> clazz = object.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null){
                if (!field.isAccessible()){
                    field.setAccessible(true);
                }
                field.set(object,fieldValue);
                return true;
            }
        } catch (Exception e) {
            DebugLog.printException(e);
        }
        return false;
    }


    /**
     * 反射调用，获取静态属性值
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Object getStaticField(Class clazz, String fieldName){
        if (clazz == null)
            return null;

        Object result = null;

        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null){
                if (!field.isAccessible()){
                    field.setAccessible(true);
                }
                result = field.get(clazz); //是静态，所以不需要object
            }
        } catch (Exception e) {
            DebugLog.printException(e);
        }

        return result;
    }


    /**
     * 反射调用，设置静态属性值
     *
     * @param clazz    操作类
     * @param fieldName 属性名
     * @param value     属性的新值
     * @return 设置是否成功
     */
    public static boolean setStaticField(Class clazz, String fieldName, Object value) {
        if (clazz == null) {
            return false;
        }

        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(null, value);
                return true;
            }
        } catch (Exception e) {
            DebugLog.printException(e);
        }
        return false;
    }

    /**
     * 通过类名获取Class
     * @param clazzName
     * @return
     */
    public static Class<?> getClazz(String clazzName){
        try {
            return Class.forName(clazzName);
        } catch (Exception e) {
            DebugLog.printException(e);
        }
        return null;
    }


    /**
     * 将对象转化为class
     * @param objs
     * @return
     */
    private static Class<?>[] obj2class(Object... objs){

        int objLen = objs == null? 0 : objs.length;

        Class<?>[] clazzTypes = new Class[objLen];
        for(int i = 0;i<objLen;i++){
            clazzTypes[i] = objs[i].getClass();

        }
        return clazzTypes;
    }


    /**
     * 获取类的所有 构造函数，属性，方法
     *
     * @param className 类名
     * @return
     */
    public static String dumpClass(String className) {
        StringBuffer sb = new StringBuffer();
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            DebugLog.printException(e);
            return "";
        }

        Constructor<?>[] cs = clazz.getDeclaredConstructors();
        sb.append("------  Constructor  ------>  ").append("\n");
        for (Constructor<?> c : cs) {
            sb.append(c.toString()).append("\n");
        }

        sb.append("------  Field  ------>").append("\n");
        Field[] fs = clazz.getDeclaredFields();
        for (Field f : fs) {
            sb.append(f.toString()).append("\n");
            ;
        }
        sb.append("------  Method  ------>").append("\n");
        Method[] ms = clazz.getDeclaredMethods();
        for (Method m : ms) {
            sb.append(m.toString()).append("\n");
        }
        return sb.toString();
    }
}
