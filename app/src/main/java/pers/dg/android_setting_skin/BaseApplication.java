package pers.dg.android_setting_skin;

import android.app.Application;

import pers.dg.skin_lib.SkinManager;

/**
 * Created by b on 2019/3/12.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initSkin();
    }

    private void initSkin() {
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load(null, null);
    }
}
