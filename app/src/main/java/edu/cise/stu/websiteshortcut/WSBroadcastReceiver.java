package edu.cise.stu.websiteshortcut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by USOON on 2016/9/8.
 */
public class WSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("WSLog", "OK");

        if(intent.getAction().equals("com.android.launcher.action.INSTALL_SHORTCUT")){

            Log.e("WSLog", "Shortcut OK");

            //取得資料
            Bundle bundle = intent.getExtras();
            //取得捷徑名稱
            String name = bundle.getString(Intent.EXTRA_SHORTCUT_NAME);
            //取得捷徑存放的動作
            Intent shortcutIntent = (Intent) bundle.get(Intent.EXTRA_SHORTCUT_INTENT);
            //取得動作裡面的網址
            String url = shortcutIntent.getData().toString();
            //輸出
            String message = "名稱 : " + name + " 網址 : " + url;

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        }
    }
}
