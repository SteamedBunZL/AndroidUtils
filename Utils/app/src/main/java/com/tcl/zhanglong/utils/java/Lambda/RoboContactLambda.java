package com.tcl.zhanglong.utils.java.Lambda;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Steve on 16/11/26.
 */

public class RoboContactLambda {

    /**
     – Predicate: 判断是否符合某个条件
     – Consumer: 使用参数对象来执行一些操作
     – Function: 把对象 T 变成 U
     – Supplier:提供一个对象 T (和工厂方法类似)
     – UnaryOperator: A unary operator from T -> T
     – BinaryOperator: A binary operator from (T, T) -> T
     * @param pl
     * @param pred
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void predicate(List<ComparatorTest.Person> pl, Predicate<ComparatorTest.Person> pred){
        for(ComparatorTest.Person p:pl){
            if (pred.test(p)){
                //
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.N)
    public void consumer(List<ComparatorTest.Person> pl, Consumer<ComparatorTest.Person> consum){
        for(ComparatorTest.Person p:pl){
            consum.accept(p);
        }
    }

    /**
     * Lambda 表达式替代匿名内部类的调法
     */
    private void testPhoneContacts(){
        List<ComparatorTest.Person> list = new ArrayList<>();
        predicate(list,p -> p.surName.equals("ssss"));

        consumer(list,p -> p.surName = "zl");
    }
}
