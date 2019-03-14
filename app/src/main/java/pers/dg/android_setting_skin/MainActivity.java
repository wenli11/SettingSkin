package pers.dg.android_setting_skin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import pers.dg.skin_lib.SkinConstants;
import pers.dg.skin_lib.SkinManager;

public class MainActivity extends BaseActivity {

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void click(View view) {
        if(i == 0){
            if (Build.VERSION.SDK_INT >= 23) {
                int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //验证是否许可权限
                for (String str : permissions) {
                    if (MainActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        MainActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    }
                }
            }
            i = 2;
        }else if(i == 2){
            i = 1;
            SkinManager.getInstance().load(SkinConstants.SKIN_SHAREDPREFERENCES_VALUE_DEFAULT_PATH, null);
        }else if(i == 1){
            SkinManager.getInstance().load("/storage/emulated/0/Android/data/ren.solid.materialdesigndemo/cache/skin/skin_dark.skin", null);
            i = 2;
        }
    }
}
