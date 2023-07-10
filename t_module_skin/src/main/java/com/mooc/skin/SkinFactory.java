package com.mooc.skin;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.mooc.skin.SkinItem;
import com.mooc.skin.SkinView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.mooc.skin.R;

public class SkinFactory implements LayoutInflater.Factory2 {
    //当Activity继承自AppcompatActivity的时候  去通过这个对象来实例化控件
    private AppCompatDelegate delegate;
    private static final String[] prxfixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    //缓存构造方法的map
    private HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();
    private Class<?>[] mConstructorSignature = new Class[] {
            Context.class, AttributeSet.class};
    List<SkinView> skinViews = new ArrayList<>();

    public SkinFactory(AppCompatDelegate delegate){
        this.delegate = delegate;
    }

    @Override
    public View onCreateView(View view, String s, Context context, AttributeSet attributeSet) {
        if(s.contains("LinearLayout")){
            Log.e("MN-------->","1111111111");
        }
        //实例化每个控件
        View crrentView = null;
        if(delegate !=null){
            crrentView = delegate.createView(view, s, context, attributeSet);
        }
        //兼顾了两种情况  第一种情况就是delegate为空 没有去实例化控件
        // 第二种情况就是delegate不为空 但是它没有替我们去实例化控件
        if(crrentView == null){
            //如果是Activity的情况下  通过反射去讲控件实例化的
            //1.带包名  2.不带包名
            if(s.contains(".")){
                crrentView = onCreateView(s,context,attributeSet);
            }else{
                for (String s1 : prxfixList) {
                    String className = s1+s;
                    crrentView = onCreateView(className,context,attributeSet);
                    if(crrentView!=null){
                        break;
                    }
                }
            }
        }
        //收集需要换肤的控件
        if(crrentView!=null){
            paserView(context,crrentView,attributeSet);
        }

        return crrentView;
    }

    public void apply(){
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    /**
     * 收集需要换肤的控件
     * @param context
     * @param view
     * @param attributeSet
     */
    private void paserView(Context context, View view, AttributeSet attributeSet) {

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.Skinable);
        boolean isKin = typedArray.getBoolean(R.styleable.Skinable_is_skin, false);
        if(!isKin){
            return;
        }
        //再去找到这个控件需要换肤的属性
        List<SkinItem> skinItems =  new ArrayList<>();
        for(int x=0;x<attributeSet.getAttributeCount();x++){
            //属性的名字  类似 textColor  src
            String attributeName = attributeSet.getAttributeName(x);
            //如果说符合条件 句证明这条属性是需要换肤的
            if(attributeName.contains("textColor") || attributeName.contains("src")
            || attributeName.contains("background")){
                //属性的名字  ID  类型
                String resIdStr = attributeSet.getAttributeValue(x);
                int resId = Integer.parseInt(resIdStr.substring(1));
                String resourceTypeName = context.getResources().getResourceTypeName(resId);
                String resourceEntryName = context.getResources().getResourceEntryName(resId);
                SkinItem skinItem = new SkinItem(attributeName,resourceTypeName,
                        resourceEntryName,resId);
                skinItems.add(skinItem);
            }
        }
        if(skinItems.size()>0){
            SkinView skinView = new SkinView(skinItems,view);
            skinViews.add(skinView);
        }
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        View  view = null;
        try {
            //获取到控件的class对象
            Class aClass = context.getClassLoader().loadClass(s);
            Constructor<? extends View> constructor;
            constructor = sConstructorMap.get(s);
            if(constructor == null){
                constructor = aClass.getConstructor(mConstructorSignature);
                sConstructorMap.put(s,constructor);
            }
            view = constructor.newInstance(context,attributeSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }


    public List<SkinView> getSkinViews() {
        return skinViews;
    }
}
