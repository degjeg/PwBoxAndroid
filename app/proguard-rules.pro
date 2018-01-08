# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/danger/Android/sdk/sdk/tools/proguard/proguard-android.txt
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


###### greendao start ########################
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

-dontwarn org.greenrobot.greendao.database.**
-dontwarn org.greenrobot.greendao.rx.**
###### greendao start ########################


###### netty start ########################
#-keep classwithmember io.netty.** {* ;}
#-dontwarn io.netty.**
-dontwarn rx.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.**
###### netty end ########################


# 腾讯 mta
-keep class com.tencent.stat.**  {* ;}
-keep class com.tencent.mid.**  {* ;}


# protobuf
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}
-keep public class * extends com.google.protobuf.** { *; }

#wire
-keep class com.squareup.wire.** { *; }
-keep class com.pw.box.bean.protobuf.** { *; }

#广点通
-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.app.NotificationCompat**{
    public *;
}

# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}