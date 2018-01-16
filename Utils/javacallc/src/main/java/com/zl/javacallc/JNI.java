package com.zl.javacallc;

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
 * Created by Steve on 17/11/13.
 * <p>
 * ━━━━━━━━━━━━━━━━
 */

public class JNI {

    {
        System.loadLibrary("JavaCallC");
    }

    /**
     * 让C代码做加法运算，把结果返回给java
     * @param x
     * @param y
     * @return
     */
    public native int add(int x,int y);

    /**
     * 从java传入一个字符串，C代码进行一个拼接
     *
     * @param s I am from java
     * @return I am from java and I am from c
     */
    public native String sayHello(String s);

    /**
     * 让C代码给每个元素都加上10
     * @param intArray
     * @return
     */
    public native int[] increaseArrayEles(int[] intArray);

    /**
     * 较验密码是否正确，如果正确返回200，否则返回400
     * @param pwd
     * @return
     */
    public native int checkPwd(String pwd);
}
