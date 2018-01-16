
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

/**
* jstring : 返回值
* Java_全类名_方法名
* JNIEnv* env :里面有很多方法
* jobject 谁调用的这个方法，谁就是这个的实例
* 其实当就是JNI.this
*/
jstring Java_com_tcl_zhanglong_utils_jni_JNI_sayHello(JNIEnv* env,jobject jobj){

//  jstring     (*NewStringUTF)(JNIEnv*, const char*);
    char* text = "I am from C";
    return (*env)->NewStringUTF(env,text);
}