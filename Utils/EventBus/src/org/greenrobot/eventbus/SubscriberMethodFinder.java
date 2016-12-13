/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.eventbus;

import org.greenrobot.eventbus.meta.SubscriberInfo;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅者响应函数信息存储和查找类，由 HashMap 缓存，以 ${subscriberClassName} 为 key
 */
class SubscriberMethodFinder {
    /*
     * In newer class files, compilers may add methods. Those are called bridge or synthetic methods.
     * EventBus must ignore both. There modifiers are not public but defined in the Java class file format:
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1
     */
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();

    private List<SubscriberInfoIndex> subscriberInfoIndexes;
    private final boolean strictMethodVerification;
    private final boolean ignoreGeneratedIndex;

    private static final int POOL_SIZE = 4;
    private static final FindState[] FIND_STATE_POOL = new FindState[POOL_SIZE];

    SubscriberMethodFinder(List<SubscriberInfoIndex> subscriberInfoIndexes, boolean strictMethodVerification,
                           boolean ignoreGeneratedIndex) {
        this.subscriberInfoIndexes = subscriberInfoIndexes;
        this.strictMethodVerification = strictMethodVerification;
        this.ignoreGeneratedIndex = ignoreGeneratedIndex;
    }

    /***
     * 寻找订阅者的方法
     * @param subscriberClass
     * @return
     */
    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        //从Cache中取出
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        //不为空直接返回
        if (subscriberMethods != null) {
            return subscriberMethods;
        }

        //生成SubscriberMethod
        if (ignoreGeneratedIndex) {
            subscriberMethods = findUsingReflection(subscriberClass);
        } else {
            subscriberMethods = findUsingInfo(subscriberClass);
        }

        //如果List为空,抛异常
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass
                    + " and its super classes have no public methods with the @Subscribe annotation");
        } else {
            //存入Cache中
            METHOD_CACHE.put(subscriberClass, subscriberMethods);
            return subscriberMethods;
        }
    }

    /**
     *
     * @param subscriberClass
     * @return
     */
    private List<SubscriberMethod> findUsingInfo(Class<?> subscriberClass) {
        //从对象池中取出一个FindState获重新生成一个
        FindState findState = prepareFindState();
        //初始化订阅者
        findState.initForSubscriber(subscriberClass);
        while (findState.clazz != null) {
            //得到订阅者信息实体
            findState.subscriberInfo = getSubscriberInfo(findState);
            if (findState.subscriberInfo != null) {
                SubscriberMethod[] array = findState.subscriberInfo.getSubscriberMethods();
                for (SubscriberMethod subscriberMethod : array) {
                    if (findState.checkAdd(subscriberMethod.method, subscriberMethod.eventType)) {
                        findState.subscriberMethods.add(subscriberMethod);
                    }
                }
            } else {
                //第一次会走这里
                findUsingReflectionInSingleClass(findState);
            }
            findState.moveToSuperclass();
        }
        return getMethodsAndRelease(findState);
    }

    /**
     * 返回findState中的订阅者方法,并发布(回退给findstate对象池)
     * @param findState
     * @return
     */
    private List<SubscriberMethod> getMethodsAndRelease(FindState findState) {
        //生成临时变量的List,持有findState.subsrciberMethods
        List<SubscriberMethod> subscriberMethods = new ArrayList<>(findState.subscriberMethods);
        //findState中的资源就可以回收掉了
        findState.recycle();
        //把findState还给对象池中去,注意锁
        synchronized (FIND_STATE_POOL) {
            for (int i = 0; i < POOL_SIZE; i++) {
                if (FIND_STATE_POOL[i] == null) {
                    FIND_STATE_POOL[i] = findState;
                    break;
                }
            }
        }
        return subscriberMethods;
    }

    /**
     * 从对象池中取出一个FindState
     * @return
     */
    private FindState prepareFindState() {
        synchronized (FIND_STATE_POOL) {
            for (int i = 0; i < POOL_SIZE; i++) {
                FindState state = FIND_STATE_POOL[i];
                if (state != null) {
                    FIND_STATE_POOL[i] = null;
                    return state;
                }
            }
        }
        return new FindState();
    }

    /**
     * 获取订阅者信息
     * @param findState
     * @return
     */
    private SubscriberInfo getSubscriberInfo(FindState findState) {
        if (findState.subscriberInfo != null && findState.subscriberInfo.getSuperSubscriberInfo() != null) {
            SubscriberInfo superclassInfo = findState.subscriberInfo.getSuperSubscriberInfo();
            if (findState.clazz == superclassInfo.getSubscriberClass()) {
                return superclassInfo;
            }
        }
        //第一次会走这里
        if (subscriberInfoIndexes != null) {
            for (SubscriberInfoIndex index : subscriberInfoIndexes) {
                SubscriberInfo info = index.getSubscriberInfo(findState.clazz);
                if (info != null) {
                    return info;
                }
            }
        }
        return null;
    }

    /**
     * 查找通过反射
     * @param subscriberClass
     * @return
     */
    private List<SubscriberMethod> findUsingReflection(Class<?> subscriberClass) {
        //从对象池中取出一个FindState 或者重新new一个
        FindState findState = prepareFindState();
        //初始化订阅者
        findState.initForSubscriber(subscriberClass);
        while (findState.clazz != null) {
            findUsingReflectionInSingleClass(findState);
            //是否处理超类
            findState.moveToSuperclass();
        }
        //返回findstate中存入的订阅者方法集合,并回收掉findState
        return getMethodsAndRelease(findState);
    }

    private void findUsingReflectionInSingleClass(FindState findState) {
        Method[] methods;
        try {
            // This is faster than getMethods, especially when subscribers are fat classes like Activities
            //获取当前类中的方法
            methods = findState.clazz.getDeclaredMethods();
        } catch (Throwable th) {
            //获取当前类和父类中的方法
            // Workaround for java.lang.NoClassDefFoundError, see https://github.com/greenrobot/EventBus/issues/149
            methods = findState.clazz.getMethods();
            //是否跳过父类置为true
            findState.skipSuperClasses = true;
        }
        for (Method method : methods) {
            //获取方法修饰符
            int modifiers = method.getModifiers();
            //1.判断修饰符是public,并且不是抽象、静态、同步、bridge方法
            if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                //方法参数--泛型
                Class<?>[] parameterTypes = method.getParameterTypes();
                //2.参数个数为1个
                if (parameterTypes.length == 1) {
                    //获取方法上Subsribe注解
                    Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                    if (subscribeAnnotation != null) {
                        Class<?> eventType = parameterTypes[0];
                        if (findState.checkAdd(method, eventType)) {
                            //3.获取线程模式
                            ThreadMode threadMode = subscribeAnnotation.threadMode();
                            //4.存入到arraylist中
                            findState.subscriberMethods.add(new SubscriberMethod(method, eventType, threadMode,
                                    subscribeAnnotation.priority(), subscribeAnnotation.sticky()));
                        }
                    }
                } else if (strictMethodVerification && method.isAnnotationPresent(Subscribe.class)) {
                    //如果超过一个参数 并且有Suscribe的注解 会抛出异常
                    String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                    throw new EventBusException("@Subscribe method " + methodName +
                            "must have exactly 1 parameter but has " + parameterTypes.length);
                }
            } else if (strictMethodVerification && method.isAnnotationPresent(Subscribe.class)) {
                //如果超过一个参数 并且有Suscribe的注解 会抛出异常
                String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                throw new EventBusException(methodName +
                        " is a illegal @Subscribe method: must be public, non-static, and non-abstract");
            }
        }
    }

    static void clearCaches() {
        METHOD_CACHE.clear();
    }

    static class FindState {
        final List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        final Map<Class, Object> anyMethodByEventType = new HashMap<>();
        final Map<String, Class> subscriberClassByMethodKey = new HashMap<>();
        final StringBuilder methodKeyBuilder = new StringBuilder(128);

        Class<?> subscriberClass;
        Class<?> clazz;
        /**是否跳过超类 默认为false*/
        boolean skipSuperClasses;
        SubscriberInfo subscriberInfo;

        /**
         * 初始化订阅者
         * @param subscriberClass
         */
        void initForSubscriber(Class<?> subscriberClass) {
            this.subscriberClass = clazz = subscriberClass;
            //不跳过超类
            skipSuperClasses = false;
            //订阅者信息为null
            subscriberInfo = null;
        }

        /**
         * 回收掉findstate的资源
         */
        void recycle() {
            subscriberMethods.clear();
            anyMethodByEventType.clear();
            subscriberClassByMethodKey.clear();
            methodKeyBuilder.setLength(0);
            subscriberClass = null;
            clazz = null;
            skipSuperClasses = false;
            subscriberInfo = null;
        }

        /**
         * 对eventType method进行check
         * @param method
         * @param eventType
         * @return
         */
        boolean checkAdd(Method method, Class<?> eventType) {
            // 2 level check: 1st level with event type only (fast), 2nd level with complete signature when required.
            // Usually a subscriber doesn't have methods listening to the same event type.
            //拿上之前evntType对应的method
            Object existing = anyMethodByEventType.put(eventType, method);
            //之前没有返回true
            if (existing == null) {
                return true;
            } else {
                //类型为Method
                if (existing instanceof Method) {
                    if (!checkAddWithMethodSignature((Method) existing, eventType)) {
                        // Paranoia check
                        throw new IllegalStateException();
                    }
                    // Put any non-Method object to "consume" the existing Method
                    anyMethodByEventType.put(eventType, this);
                }
                return checkAddWithMethodSignature(method, eventType);
            }
        }

        private boolean checkAddWithMethodSignature(Method method, Class<?> eventType) {
            //把StringBuilder中的内容清空
            methodKeyBuilder.setLength(0);
            //方法名称
            methodKeyBuilder.append(method.getName());
            //类名
            methodKeyBuilder.append('>').append(eventType.getName());

            //生成methodKey
            String methodKey = methodKeyBuilder.toString();
            //方法定义所在的类
            Class<?> methodClass = method.getDeclaringClass();
            //把旧的订阅者方法类取出
            Class<?> methodClassOld = subscriberClassByMethodKey.put(methodKey, methodClass);
            //旧的订阅者为空或(ClassA表示的类或接口是否为参数ClassB的父类或父接口) 旧的类是方法定义类的父类 返回true
            if (methodClassOld == null || methodClassOld.isAssignableFrom(methodClass)) {
                // Only add if not already found in a sub class
                return true;
            } else {
                // Revert the put, old class is further down the class hierarchy
                subscriberClassByMethodKey.put(methodKey, methodClassOld);
                return false;
            }
        }

        /**
         * 如果没有忽略超类的话,findstate中的claszz将指向超类
         */
        void moveToSuperclass() {
            //忽略父类
            if (skipSuperClasses) {
                clazz = null;
            } else {
                //获取超类
                clazz = clazz.getSuperclass();
                //获取超类的名字
                String clazzName = clazz.getName();
                /** Skip system classes, this just degrades performance. */
                //忽略系统的类
                if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("android.")) {
                    clazz = null;
                }
            }
        }
    }

}
