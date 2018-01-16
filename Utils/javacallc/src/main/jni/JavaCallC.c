#include "com_zl_javacallc_JNI.h"
#include <string.h>


/**
* 把一个jstring转换成一个c语言的char*类型
*/
char* _JString2CStr(JNIEnv* env,jstring jstr){
    char* rtn = NULL;
  	jclass clsstring  = (*env)->FindClass(env,"java/lang/String");
  	jstring strencode = (*env)->NewStringUTF(env,"GB2312");
  	jmethodID mid = (*env)->GetMethodID(env,clsstring,"getBytes","(Ljava/lang/String;)[B");
  	jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env,jstr,mid,strencode);
  	jsize alen = (*env)->GetArrayLength(env,barr);
  	jbyte* ba = (*env)->GetByteArrayElements(env,barr,JNI_FALSE);
  	if(alen > 0){
        rtn = (char*)malloc(alen + 1);
      	memcpy(rtn,ba,alen);
      	rtn[alen] = 0;
    }
  	(*env)->ReleaseByteArrayElements(env,barr,ba,0);
  	return rtn;
}

/**
* jint : 返回值
  Java_全类名_方法名

*/
jint Java_com_zl_javacallc_JNI_add(JNIEnv *env, jobject jobj, jint x, jint y){
    jint result  = x + y;
    return result;
};

/**
   从java传入字符串，C代码进行拼接
*/
JNIEXPORT jstring JNICALL Java_com_zl_javacallc_JNI_sayHello(JNIEnv *env , jobject jobj, jstring jstr){
    char* fromJava = _JString2CStr(env,jstr);

    char* fromC  = " add I am from C";

    strcat(fromJava,fromC);

    return (*env)->NewStringUTF(env,fromJava);

};