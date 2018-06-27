# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# copyright zhonghanwen
#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-ignorewarnings
#忽略警告，避免打包时某些警告出现
-optimizationpasses 5
# 指定代码的压缩级别
-dontusemixedcaseclassnames
# 包明不混合大小写
-dontskipnonpubliclibraryclasses
# 不去忽略非公共的库类
-dontpreverify
# 预校验
-verbose
# 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 混淆时所采用的算法
-keepattributes *Annotation*
# 保护注解
-keepattributes Signature
# 避免混淆泛型
-dontwarn android.support.**
# 引用v4、v7包不被混淆
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# 保持 native 方法 不被混淆
-keepclasseswithmembernames class *  {
    native <methods>;
}

# 保持自定 义控件类不被混淆
-keepclassmembers class *  {
    public void *(android.view.View);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#保持自定义控件 类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum *  {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保持 Parcelable 不被混淆
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# 不混淆资源类
-keepclassmembers class **.R$*  {
    public static <fields>;
}
# 移除log
-assumenosideeffects class android.util.Log {
    public static boolean  isLoggable(java.lang.String,int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------
#---------------------------------实体类---------------------------------
#修改成你对应的包名
-keep class com.android.hcbd.whsw.entity.** { *; }

#---------------------------------第三方包-------------------------------
##百度定位
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**
#okgo
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
#utilcode工具库
-keep class com.blankj.utilcode.** { *; }
-keepclassmembers class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**
#Picker选择器
-dontwarn com.bigkoo.pickerview.**
-keep class com.bigkoo.pickerview.**{*;}
#蒲公英sdk混淆
-dontwarn com.pgyersdk.**
-keep class com.pgyersdk.** { *; }
#easyrecyclerview
-dontwarn com.jude.easyrecyclerview.**
-keep class com.jude.easyrecyclerview.** { *; }
#materialedittext
-dontwarn com.rengwuxian.materialedittext.**
-keep class com.rengwuxian.materialedittext.** { *; }
#AndPermissions
-dontwarn com.yanzhenjie.permission.**
-keep class com.yanzhenjie.permission.** { *; }
#statusbarutil
-dontwarn com.jaeger.library.**
-keep class com.jaeger.library.** { *; }
#imagepicker
-dontwarn com.lzy.imagepicker.**
-keep class com.lzy.imagepicker.** { *; }
#pagerbottomtabstrip
-dontwarn me.majiajie.pagerbottomtabstrip.**
-keep class me.majiajie.pagerbottomtabstrip.** { *; }
#echarts
#-dontwarn com.github.abel533.echarts.**
#-keep class com.github.abel533.echarts.** { *; }


####################zxing#####################
#-keep class com.google.zxing.** {*;}
#-dontwarn com.google.zxing.**
#-ButterKnife 7.0
 -keep class butterknife.** { *; }
 -dontwarn butterknife.internal.**
 -keep class **$$ViewBinder { *; }
 -keepclasseswithmembernames class * {
  @butterknife.* <fields>;
 }
 -keepclasseswithmembernames class * {
 @butterknife.* <methods>;
 }

#eventbus 3.0
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

################gson##################
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# support-v4
#https://stackoverflow.com/questions/18978706/obfuscate-android-support-v7-widget-gridlayout-issue
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }


# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }

# support design
#@link http://stackoverflow.com/a/31028536
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
#-------------------------------------------------------------------------

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# #  ######## greenDao混淆  ##########
# # -------------------------------------------
#-keep class de.greenrobot.dao.** {*;}
#-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
#    public static Java.lang.String TABLENAME;
#}
#-keep class **$Properties

#log4j
#-dontwarn org.apache.log4j.**
#-keep class  org.apache.log4j.** { *;}

