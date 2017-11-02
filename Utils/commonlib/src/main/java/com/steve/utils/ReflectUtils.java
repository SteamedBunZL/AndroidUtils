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


    public static final int BOOLEAN_TYPE = 1;
    public static final int BYTE_TYPE = 2;
    public static final int SHORT_TYPE = 3;
    public static final int INT_TYPE = 4;
    public static final int LONG_TYPE = 5;
    public static final int FLOAT_TYPE = 6;
    public static final int DOUBLE_TYPE = 7;
    public static final int CHARACTER_TYPE = 8;
    public static final int STRING_TYPE = 9;
    public static final int CHASEQUENCE_TYPE = 10;
    public static final int OBJECT_TYPE = 11;


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
     * 自动装箱 int -> Integer 会导致getMethod失败
     * @param objs
     * @return
     */
    private static Class<?>[] obj2class(Object... objs){

        int objLen = objs == null? 0 : objs.length;

        Class<?>[] clazzTypes = new Class[objLen];
        Class<?> clazz;
        int type;
        for(int i = 0;i<objLen;i++){
            clazz = objs[i].getClass();
            type = getParameterType(clazz);
            if (type != OBJECT_TYPE)
                clazzTypes[i] = getParameterType(type);
            else
                clazzTypes[i] = clazz;
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

    /**
     * Chasequence.class的处理还是有点问题
     * @param clazz
     * @return
     */
    private static int getParameterType(Class<?> clazz) {
        if (clazz == Boolean.class){
            return BOOLEAN_TYPE;
        }else if (clazz == Byte.class){
            return BYTE_TYPE;
        }else if (clazz == Short.class){
            return SHORT_TYPE;
        }else if(clazz == Integer.class){
            return INT_TYPE;
        }else if (clazz == Long.class){
            return LONG_TYPE;
        }else if(clazz == Float.class){
            return FLOAT_TYPE;
        }else if(clazz == Double.class){
            return DOUBLE_TYPE;
        }else if(clazz == Character.class){
            return CHARACTER_TYPE;
        }else if(clazz == String.class){
            return STRING_TYPE;
        }else if(clazz == CharSequence.class){
            return CHASEQUENCE_TYPE;
        }else{
            return OBJECT_TYPE;
        }
    }

    private static Class<?> getParameterType(int type) {
        switch (type){
            case BOOLEAN_TYPE:
                return boolean.class;
            case BYTE_TYPE:
                return byte.class;
            case SHORT_TYPE:
                return short.class;
            case INT_TYPE:
                return int.class;
            case LONG_TYPE:
                return long.class;
            case FLOAT_TYPE:
                return float.class;
            case DOUBLE_TYPE:
                return double.class;
            case CHARACTER_TYPE:
                return char.class;
            case STRING_TYPE:
                return String.class;
            case CHASEQUENCE_TYPE:
                return CharSequence.class;
        }
        return null;
    }
}
