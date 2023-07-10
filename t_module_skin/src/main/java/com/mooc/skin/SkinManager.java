package com.mooc.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

/**
 * 去加载sd卡中的资源包   然后能够从里面去获取我们想要的资源
 */
public class SkinManager {
    private static SkinManager skinManager = new SkinManager();
    private Context context;
    private Resources resources;
    //获取到资源包中的包名
    private String skinPackageName;


    private SkinManager(){}

    public static SkinManager getInstance(){
        return skinManager;
    }

    public void init(Context context){
        this.context = context;
    }

    /**
     * 根据资源包的 存储路劲去加载资源
     * @param path
     */
    public void loadSkinApk(String path){
        //真正的目的是得到资源包的资源对象
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(path,
                    PackageManager.GET_ACTIVITIES);
            skinPackageName = packageArchiveInfo.packageName;
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            //在assetManager中执行addAssetPath方法
            addAssetPath.invoke(assetManager,path);
            //创建一个资源对象  管理资源包里面的资源
            resources = new Resources(assetManager,context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //写匹配各种资源的方法
    /**
     * 去匹配颜色
     * @param resId 需要去匹配ID
     * @return  匹配到的资源ID（在资源包中的资源ID）
     */
    public int getColor(int resId){
        if(resourceIsNull()){
            return resId;
        }
        //获取到资源名字和资源类型
        String resourceTypeName = context.getResources().getResourceTypeName(resId);
        String resourceEntryName = context.getResources().getResourceEntryName(resId);
        //去资源包的资源对象中去匹配
        int identifier = resources.getIdentifier(resourceEntryName,
                resourceTypeName, skinPackageName);
        if(identifier == 0){
            return resId;
        }
        return resources.getColor(identifier);
    }


    /**
     * 从资源包中拿到drawable的资源id
     */
    public Drawable getDrawable(int id){
        if(resourceIsNull()){
            //获取到这个id在当前应用中的Drawable对象
            return ContextCompat.getDrawable(context,id);
        }
        //获取到资源id的类型
        String resourceTypeName = context.getResources().getResourceTypeName(id);
        //获取到的就是资源id的名字
        String resourceEntryName = context.getResources().getResourceEntryName(id);
        //就是colorAccent这个资源在外置APK中的id
        int identifier = resources.getIdentifier(resourceEntryName, resourceTypeName, skinPackageName);
        if(identifier == 0){
            return ContextCompat.getDrawable(context,id);
        }
        return resources.getDrawable(identifier);
    }

    /**
     * 从资源包中拿到drawable的资源id
     */
    public int getDrawableId(int id){
        if(resourceIsNull()){
            return id;
        }
        //获取到资源id的类型
        String resourceTypeName = context.getResources().getResourceTypeName(id);
        //获取到的就是资源id的名字
        String resourceEntryName = context.getResources().getResourceEntryName(id);
        //就是colorAccent这个资源在外置APK中的id
        int identifier = resources.getIdentifier(resourceEntryName, resourceTypeName, skinPackageName);
        if(identifier == 0){
            return id;
        }
        return identifier;
    }


    public boolean resourceIsNull(){
        if(resources == null){
            return true;
        }
        return false;
    }

    public Resources getResources() {
        return resources;
    }
}
