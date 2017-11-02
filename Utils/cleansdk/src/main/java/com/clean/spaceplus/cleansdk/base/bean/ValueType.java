package com.clean.spaceplus.cleansdk.base.bean;

/**
 * @author dongdong.huang
 * @Description: 常用数值类型类
 * @date 2016/6/29 14:24
 * @copyright TCL-MIG
 */
public class ValueType {
    private static final int TYPE_INT = 0;
    private static final int TYPE_LONG = 1;
    private Object lock = new Object();
    private int type;
    private int intValue;
    private long longValue;

    public ValueType(int value){
        intValue = value;
        type = TYPE_INT;
    }

    public ValueType(long value){
        longValue = value;
        type = TYPE_LONG;
    }

    public void increment(){
       synchronized (lock){
           switch (type){
               case TYPE_INT:
                   ++intValue;
                   break;
               case TYPE_LONG:
                   ++longValue;
                   break;
               default:
                   break;
           }
       }
    }

    public void decrement(){
        synchronized (lock){
            switch (type){
                case TYPE_INT:
                    --intValue;
                    break;
                case TYPE_LONG:
                    --longValue;
                    break;
                default:
                    break;
            }
        }
    }

    public int intValue(){
        synchronized (lock){
            return intValue;
        }
    }

    public long longValue(){
        synchronized (lock){
            return longValue;
        }
    }
}
