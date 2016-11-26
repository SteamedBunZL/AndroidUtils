package com.tcl.zhanglong.utils.java.Lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Steve on 16/11/26.
 */

public class ComparatorTest {

    List<Person> personList = new ArrayList<>();

    /**
     * 使用内部类实现排序
     */
    private void sortDefault(){
        Collections.sort(personList, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return p1.surName.compareTo(p2.surName);
            }
        });
    }

    /**
     * 使用Lambda表达式实现 ASC 升序排列
     */
    private void sortLambdaAsc(){
        Collections.sort(personList,(Person p1,Person p2) -> p1.surName.compareTo(p2.surName));
    }

    /**
     * 使用Lambda表达式实现 DESC 降序排列
     */
    private void sortLambdaDesc(){
        Collections.sort(personList,(p1,p2) -> p2.surName.compareTo(p1.surName));
    }




    class Person{
        public String givenName;
        public String surName;
        public int age;
        public String eMail;
    }
}
