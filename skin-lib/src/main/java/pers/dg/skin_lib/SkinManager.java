package pers.dg.skin_lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import pers.dg.skin_lib.listener.SkinAttrsUpdate;
import pers.dg.skin_lib.listener.SkinLoad;

/**
 * Created by b on 2019/3/12.
 */

public class SkinManager {

    private static final String TAG = "SkinManager";

    private List<SkinAttrsUpdate> skinObservers;
    private Context context;
    private String skinPackageName;
    private Resources mResources;
    private boolean isDefaultSkin = false;

    private static volatile SkinManager skinManager;

    public static SkinManager getInstance(){
        if(skinManager == null){
            synchronized (SkinManager.class){
                if(skinManager == null){
                    skinManager = new SkinManager();
                }
            }
        }
        return skinManager;
    }

    private SkinManager(){

    }

    public void init(Context ctx){
        context = ctx.getApplicationContext();
    }

    public boolean isExternalSkin(){
        return !isDefaultSkin && mResources != null;
    }

    /**
     * 用户第一次进入：路径为空，不执行任何方法
     * 用户刚打开应用：路径为默认，不执行任何方法
     *                 路径为其他，根据路径执行
     * 调用load方法：路径与存储路径一致，不需要修改
     *               路径为默认，执行默认皮肤
     *               路径为其他，根据路径执行
     * @param skinPackagePath
     * @param callback
     */

    public void load(String skinPackagePath, final SkinLoad callback) {

        if(skinPackagePath != null){
            if(skinPackagePath.equals(SkinSharedPreferences.getString(context))){
                Log.i(TAG, "皮肤已设置，不需要修改");
                return;
            }
            if(skinPackagePath.equals(SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH)){
                isDefaultSkin = true;
                SkinSharedPreferences.putString(context, SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH);
                notifySkinUpdate();
                return;
            }
        }else {
            skinPackagePath = SkinSharedPreferences.getString(context);
            if(skinPackagePath == null){
                isDefaultSkin = true;
                SkinSharedPreferences.putString(context, SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH);
                return;
            }
            if(skinPackagePath.equals(SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH)){
                isDefaultSkin = true;
                return;
            }
        }

        new AsyncTask<String, Void, Resources>() {

            protected void onPreExecute() {
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            protected Resources doInBackground(String... params) {
                try {
                    if (params.length == 1) {
                        String skinPkgPath = params[0];

                        if(SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH.equals(skinPkgPath)){
                            skinPackageName = context.getPackageName();
                            return context.getResources();
                        }

                        File file = new File(skinPkgPath);
                        if(!file.exists()){
                            return null;
                        }

                        PackageManager mPm = context.getPackageManager();
                        PackageInfo mInfo = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
                        skinPackageName = mInfo.packageName;

                        AssetManager assetManager = AssetManager.class.newInstance();
                        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                        addAssetPath.invoke(assetManager, skinPkgPath);

                        Resources superRes = context.getResources();
                        Resources skinResource = new Resources(assetManager,superRes.getDisplayMetrics(),superRes.getConfiguration());

                        SkinSharedPreferences.putString(context, skinPkgPath);

                        isDefaultSkin = false;
                        return skinResource;
                    }
                    SkinSharedPreferences.putString(context, SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(Resources result) {
                mResources = result;

                if (mResources != null) {
                    if (callback != null) callback.onSuccess();
                    notifySkinUpdate();
                }else{
                    isDefaultSkin = true;
                    if (callback != null) callback.onFailed();
                }
            }

        }.execute(skinPackagePath);
    }

    public void attach(SkinAttrsUpdate observer) {
        if (skinObservers == null) {
            skinObservers = new ArrayList<>();
        }
        if (!skinObservers.contains(observer)) {
            skinObservers.add(observer);
        }
    }

    public void detach(SkinAttrsUpdate observer) {
        if (skinObservers == null) return;
        if (skinObservers.contains(observer)) {
            skinObservers.remove(observer);
        }
    }

    private void notifySkinUpdate() {
        if (skinObservers == null) return;
        for (SkinAttrsUpdate observer : skinObservers) {
            observer.onThemeUpdate();
        }
    }

    public int getColor(int resId){
        int originColor = context.getResources().getColor(resId);
        if(mResources == null || isDefaultSkin){
            return originColor;
        }

        String resName = context.getResources().getResourceEntryName(resId);

        int trueResId = mResources.getIdentifier(resName, "color", skinPackageName);
        int trueColor;

        try{
            trueColor = mResources.getColor(trueResId);
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
            trueColor = originColor;
        }

        return trueColor;
    }

    @SuppressLint("NewApi")
    public Drawable getDrawable(int resId){
        Drawable originDrawable = context.getResources().getDrawable(resId);
        if(mResources == null || isDefaultSkin){
            return originDrawable;
        }
        String resName = context.getResources().getResourceEntryName(resId);

        int trueResId = mResources.getIdentifier(resName, "drawable", skinPackageName);

        Drawable trueDrawable;
        try{
            if(android.os.Build.VERSION.SDK_INT < 22){
                trueDrawable = mResources.getDrawable(trueResId);
            }else{
                trueDrawable = mResources.getDrawable(trueResId, null);
            }
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
            trueDrawable = originDrawable;
        }

        return trueDrawable;
    }

    /**
     * 加载指定资源颜色drawable,转化为ColorStateList，保证selector类型的Color也能被转换。</br>
     * 无皮肤包资源返回默认主题颜色
     * @author pinotao
     * @param resId
     * @return
     */
    public ColorStateList convertToColorStateList(int resId) {

        boolean isExtendSkin = true;

        if (mResources == null || isDefaultSkin) {
            isExtendSkin = false;
        }

        String resName = context.getResources().getResourceEntryName(resId);

        if (isExtendSkin) {
            int trueResId = mResources.getIdentifier(resName, "color", skinPackageName);
            ColorStateList trueColorList;
            if (trueResId == 0) { // 如果皮肤包没有复写该资源，但是需要判断是否是ColorStateList
                try {
                    return context.getResources().getColorStateList(resId);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    trueColorList = mResources.getColorStateList(trueResId);
                    return trueColorList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                return context.getResources().getColorStateList(resId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

        }

        int[][] states = new int[1][1];
        return new ColorStateList(states, new int[] { context.getResources().getColor(resId) });
    }

}
