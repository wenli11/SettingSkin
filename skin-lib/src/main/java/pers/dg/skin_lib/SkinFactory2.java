package pers.dg.skin_lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import pers.dg.skin_lib.entity.SkinAttr;
import pers.dg.skin_lib.entity.SkinItem;

public class SkinFactory2 implements LayoutInflater.Factory2 {

    private final List<SkinItem> mSkinItems = new ArrayList<>();

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        boolean isSkinEnable = attrs.getAttributeBooleanValue(SkinConstants.SKIN_NAME_SPACE, SkinConstants.SKIN_CUSTOM_ATTR, false);
        if(isSkinEnable){
            View view = null;
            try {
                if (-1 == name.indexOf('.')) {
                    if ("View".equals(name)) {
                        view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                    }
                    if (view == null) {
                        view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                    }
                    if (view == null) {
                        view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
                    }
                } else {
                    view = LayoutInflater.from(context).createView(name, null, attrs);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(view != null){
                parseAttr(context, attrs, view);
            }
            return view;
        }
        return null;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    private void parseAttr(Context context, AttributeSet attrs, View view) {
        List<SkinAttr> viewAttrs = new ArrayList<>();//存储View可更换皮肤属性的集合
        for(int i = 0; i < attrs.getAttributeCount(); i++){
            String attrName = attrs.getAttributeName(i);//属性名
            String attrValue = attrs.getAttributeValue(i);//属性值

            try {
                Log.e("parseAttr: ", context.getPackageName());
                Class skinAttrFactory = Class.forName(context.getPackageName()+".SkinAttrFactory");
                Method isSupportedAttr = skinAttrFactory.getDeclaredMethod("isSupportedAttr", String.class);
                if(!(boolean)(isSupportedAttr.invoke(skinAttrFactory.newInstance(), attrName))){
                    continue;
                }
                if(attrValue.startsWith("@")) {
                    int id = Integer.parseInt(attrValue.substring(1));
                    String entryName = context.getResources().getResourceEntryName(id);
                    String typeName = context.getResources().getResourceTypeName(id);

                    Method get = skinAttrFactory.getDeclaredMethod("get", String.class, int.class, String.class, String.class);
                    SkinAttr mSkinAttr = (SkinAttr) get.invoke(skinAttrFactory.newInstance(), attrName, id, entryName, typeName);
                    if (mSkinAttr != null) {
                        viewAttrs.add(mSkinAttr);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if(viewAttrs.size() > 0){
            SkinItem skinItem = new SkinItem();
            skinItem.view = view;
            skinItem.attrs = viewAttrs;

            mSkinItems.add(skinItem);

            if(SkinManager.getInstance().isExternalSkin()){
                skinItem.apply();
            }
        }
    }

    public void applySkin(){
        for(SkinItem si : mSkinItems){
            if(si.view == null){
                continue;
            }
            si.apply();
        }
    }
}
