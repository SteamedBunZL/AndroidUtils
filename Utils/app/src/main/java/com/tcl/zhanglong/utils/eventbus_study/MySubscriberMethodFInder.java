package com.tcl.zhanglong.utils.eventbus_study;

import android.net.rtp.RtpStream;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Steve on 16/12/13.
 */

class MySubscriberMethodFInder {

    private static final Map<Class<?>,List<MySubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();

    private static final int POOL_SIZE = 4;
    private static final FindState[] FIND_STATE_POOL = new FindState[POOL_SIZE];

    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;

    public List<MySubscriberMethod> findSubscriberMethod(Class<?> subscriberClass){
        List<MySubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);

        if (subscriberMethods!=null)
            return subscriberMethods;


        subscriberMethods = findUsingReflection(subscriberClass);


        if (subscriberMethods.isEmpty())
            throw new MyEventBusException("Subscriber " + subscriberClass + " and its super classes " +
                    "have no public methods with the @Subscriber annotation");
        else{
            METHOD_CACHE.put(subscriberClass,subscriberMethods);
            return subscriberMethods;
        }
    }



    private List<MySubscriberMethod> findUsingReflection(Class<?> subscriberClass){

        FindState findState = prepareFindState();

        findState.initForSubscriber(subscriberClass);

        while(findState.clazz!=null){
            findUsingReflectionInSingleClass(findState);
            findState.moveToSuperclass();
        }

        return getMethodsAndRelease(findState);
    }

    private List<MySubscriberMethod> getMethodsAndRelease(FindState findState){
        List<MySubscriberMethod> subscriberMethods = new ArrayList<>(findState.subscriberMethods);

        findState.recycle();

        synchronized (FIND_STATE_POOL){
            for(int i = 0;i<POOL_SIZE;i++){
                if (FIND_STATE_POOL[i] == null){
                    FIND_STATE_POOL[i] = findState;
                    break;
                }
            }
        }

        return subscriberMethods;
    }

    private void findUsingReflectionInSingleClass(FindState findState){
        Method[] methods;
        try {
            methods = findState.clazz.getDeclaredMethods();
        } catch (Throwable th) {
            methods = findState.clazz.getMethods();
            findState.skipSuperClasses = true;
        }

        for(Method method:methods){
            //获取修饰符
            int modifiers = method.getModifiers();
            //按位与,或的判断吊炸天
            if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0){
                //方法参数
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length==1){
                    Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                    if (subscribeAnnotation!=null){
                        Class<?> eventType = parameterTypes[0];
                        if (findState.checkAdd(method,eventType)){
                            ThreadMode threadMode = subscribeAnnotation.threadMode();
                            findState.subscriberMethods.add(new MySubscriberMethod());
                        }
                    }
                }else if(method.isAnnotationPresent(Subscribe.class)){
                    throw new MyEventBusException("");
                }
            }else if(method.isAnnotationPresent(Subscribe.class)){
                throw new MyEventBusException("");
            }
        }
    }


    /**
     * 4.对象池技术,一定要掌握
     * @return
     */
    private FindState prepareFindState(){
        synchronized (FIND_STATE_POOL){
            for(int i = 0;i<POOL_SIZE;i++){
                FindState state = FIND_STATE_POOL[i];
                if (state!=null){
                    FIND_STATE_POOL[i] = null;
                    return state;
                }
            }
        }
        return new FindState();
    }


    static class FindState{

        final List<MySubscriberMethod> subscriberMethods = new ArrayList<>();

        Class<?> subsrciberClass;
        Class<?> clazz;

        final Map<Class,Object> anyMethodByEventType = new HashMap<>();

        final StringBuilder methodKeyBuilder = new StringBuilder(128);

        final Map<String,Class> subscriberClassByMethodKey = new HashMap<>();

        boolean skipSuperClasses;

        void recycle(){
            subscriberMethods.clear();
            anyMethodByEventType.clear();
            methodKeyBuilder.setLength(0);
            clazz = null;
            subsrciberClass = null;
            skipSuperClasses =false;
        }


        void initForSubscriber(Class<?> subsriberClass){
            this.subsrciberClass = clazz = subsriberClass;

            skipSuperClasses = false;
        }

        boolean checkAdd(Method method,Class<?> eventType){
            Object existing = anyMethodByEventType.put(eventType,method);

            if (existing == null)
                return true;
            else{
                if (existing instanceof Method){
                    if (!checkAddWithMethodSignature((Method) existing,eventType)){
                        throw new IllegalStateException();
                    }

                    anyMethodByEventType.put(eventType,this);
                }

                return checkAddWithMethodSignature(method,eventType);
            }
        }

        private boolean checkAddWithMethodSignature(Method method,Class<?> eventType){

            methodKeyBuilder.setLength(0);
            methodKeyBuilder.append(method.getName());
            methodKeyBuilder.append(">").append(eventType.getName());

            String methodKey = methodKeyBuilder.toString();
            Class<?> methodClass = method.getDeclaringClass();
            Class<?> methodClassOld = subscriberClassByMethodKey.put(methodKey,methodClass);

            if (methodClassOld == null || methodClassOld.isAssignableFrom(methodClass)){
                return true;
            }else{
                subscriberClassByMethodKey.put(methodKey,methodClassOld);
                return false;
            }

        }

        void moveToSuperclass(){
            if (skipSuperClasses)
                clazz = null;
            else {
                clazz = clazz.getSuperclass();
                String clazzName = clazz.getName();

                if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("android."))
                    clazz = null;
            }
        }

    }
}
