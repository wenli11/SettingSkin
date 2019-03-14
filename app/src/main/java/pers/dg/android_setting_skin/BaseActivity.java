package pers.dg.android_setting_skin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import pers.dg.skin_lib.SkinFactory2;
import pers.dg.skin_lib.listener.SkinAttrsUpdate;
import pers.dg.skin_lib.SkinManager;

/**
 * Created by b on 2019/3/12.
 */

public class BaseActivity extends AppCompatActivity implements SkinAttrsUpdate {

    private final SkinFactory2 skinFactory2 = new SkinFactory2();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), skinFactory2);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onThemeUpdate() {
        skinFactory2.applySkin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SkinManager.getInstance().attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().detach(this);
    }
}
