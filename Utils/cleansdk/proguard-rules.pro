-optimizationpasses 5
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings

#-libraryjars ..\openlib\android-support-v7-appcompat\libs\android-support-v7-appcompat.jar
#-keep class android.support.v7.** { *; }

#-libraryjars ..\openlib\android-support-v7-appcompat\libs\android-support-v7-recyclerview.jar
#-keep class android.support.v7.widget.** { *; }


-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# ---------------- native -----------------
# native method.
-keepclasseswithmembernames class * {
    native <methods>;
}

# ---------------- serializable -----------------
-keep class * implements java.io.Serializable {
    <fields>;
}

# ---------------- enumeration -----------------
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tcl.account.** {
    <fields>;
    <methods>;
}

# ---------------- android -----------------
# android app.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.view.View
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.google.vending.licensing.ILicensingService

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# android view.
-keepclasseswithmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keep public class * extends android.view.View$BaseSavedState{*;}

# android parcelable.
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# android R.
-keepclassmembers class **.R$* {
    public static <fields>;
}

# android support.
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-keep class mp.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
 -keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
#-keep class com.google.**{*;}
-keep class com.google.gson.**{*;}

#Google Play services SDK
# Proguard flags for consumers of the Google Play services SDK
# https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project
# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# Keep the names of classes/members we need for client functionality.
-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

# Needed when building against pre-Marshmallow SDK.
-dontwarn android.security.NetworkSecurityPolicy
-dontwarn com.google.android.gms.clearcut.**
-dontwarn com.google.android.gms.internal.**

-keep public class com.google.android.gms.analytics.** {
    public *;
}
-keep public class com.google.firebase.FirebaseApp{*;}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.object writeReplace();
    java.lang.object readResolve();
}

# keep android-support-v4
#-libraryjars libs/android-support-v4.jar
#-dontwarn android.support.v4.**
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
#-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.widget.RecyclerView$LayoutManager {
    public <init>(...);
}

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*,*JavascriptInterface*

# aidl
-keep class * implements android.os.IInterface {*;}

# glide
-keep class com.clean.spaceplus.base.imageloader.GlideConfiguration { *; }
#-keep class com.bumptech.glide.** {*; }
-keep public class * implements com.bumptech.glide.module.GlideModule
# Picasso
-dontwarn com.squareup.okhttp.**

# Retrofit 2.X
## https://square.github.io/retrofit/ ##
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#-libraryjar libs/hamcrest-core-1.3
-keep class org.hamcrest.** {*; }

#-libraryjar libs/junit-4.12
-keep class junit.** {*; }
-keep class org.junit.** {*; }

#-libraryjar libs/objectdatabase-1.0.3
-keep class com.tcl.framework.** {*; }

#-libraryjar libs/transitionseverywhere-1.6.4
#-keep class android.util.FloatProperty {*; }
#-keep class android.util.IntProperty {*; }
#-keep class com.transitionseverywhere.** {*; }

# 数据Bean
-keep class com.clean.spaceplus.cleansdk.setting.authorization.bean.** { *; }
-keep public class * extends com.clean.spaceplus.cleansdk.base.bean.BaseBean
-keepclassmembers class com.clean.spaceplus.cleansdk.base.strategy.SecularService {
   void *Service();
}
-keep class com.clean.spaceplus.cleansdk.junk.service.** { *; }
-keep interface com.clean.spaceplus.cleansdk.junk.service.** { *; }
-keep class com.clean.spaceplus.cleansdk.junk.cleanmgr.** { *; }
-keep class com.clean.spaceplus.cleansdk.junk.engine.bean.** { *; }
-keep class com.clean.spaceplus.cleansdk.junk.engine.DataTypeInterface { *; }
-keep class com.clean.spaceplus.cleansdk.app.SpaceApplication { *; }
#appsflyer
-keep class com.appsflyer.**
-keep class com.google.vending.**
-dontwarn com.appsflyer.**
-dontnote com.appsflyer.**
#新加不混淆规则,之前删除了photocompress.exif,导致整个space被混淆了，保留一个最短代码的不混淆就好
-keep class space.network.util.ConvertUtil { *; }