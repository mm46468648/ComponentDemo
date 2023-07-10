# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#将 modle 目录下包括子目录下的类排除不被混淆
-keep public class com.mooc.commonbusiness.model.**{*;}
#-keep class com.mooc.commonbusiness.**{*;}
-keep public class com.mooc.commonbusiness.net.**{*;}
#constants常量类
-keep public class com.mooc.commonbusiness.constants.** {*;}
#api
-keep public class com.mooc.commonbusiness.api.**{*;}
#ARouter path
-keep public class com.mooc.commonbusiness.route.Paths
#IBaseApplication
-keep public class * extends com.mooc.commonbusiness.base.IBaseApplication


-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.moocxuetang.R$*{
public static final int *;
}

