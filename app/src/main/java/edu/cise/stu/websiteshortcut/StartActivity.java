package edu.cise.stu.websiteshortcut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

public class StartActivity extends AppCompatActivity {

    private RoundCornerProgressBar progress1;
    private TextView txt_data;
    private ScrollView sv;
    private String showInfo;
    private SharedPreferences sharedPreferences;
    private SharedPreferences firstHandler;
    private String InputPasswd = "";
    private final int PROGRESS_MAX = 40;

    private final int CHECK_ERROR = 4;
    private final int CHECK_DB = 0;
    private final int CHECK_LANGUAGE = 1;
    private final int CHECK_PW = 2;
    private final int CHECK_OK = 3;
    private final int CHECK_SHARING = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getSupportActionBar().hide(); // 在Style中已經關閉ActionBar
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_start);

        findView();
        init();
    }

    private void findView(){
        sv = (ScrollView) findViewById(R.id.scrollView);
        txt_data = (TextView) findViewById(R.id.txt_data);
        progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
    }

    private void init(){
        showInfo = getResources().getString(R.string.txt_showInfo) +"\n";
        sharedPreferences = getSharedPreferences("passWd", MODE_PRIVATE);

        progress1.setProgressColor(Color.parseColor("#AA00ffff"));
        progress1.setProgressBackgroundColor(Color.parseColor("#FFFFFF"));
        progress1.setMax(PROGRESS_MAX);

        txt_data.setText(showInfo);
        new Thread(checkData).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHECK_DB:
                    progress1.setProgress(10);
                    txt_data.setText(showInfo);
                    sv.fullScroll(View.FOCUS_DOWN);
                    break;
                case CHECK_LANGUAGE:
                    progress1.setProgress(20);
                    txt_data.setText(showInfo);
                    sv.fullScroll(View.FOCUS_DOWN);
                    break;
                case CHECK_PW:
                    progress1.setProgress(30);
                    txt_data.setText(showInfo);
                    sv.fullScroll(View.FOCUS_DOWN);
                    break;
                case CHECK_SHARING:
                    progress1.setProgress(40);
                    txt_data.setText(showInfo);
                    sv.fullScroll(View.FOCUS_DOWN);
                    Intent intentAndData = new Intent(StartActivity.this, MainActivity.class);
                    intentAndData.putExtra("SHARING", (String) msg.obj);
                    startActivity(intentAndData);
                    finish();
                    break;
                case CHECK_OK:
                    progress1.setProgress(40);
                    txt_data.setText(showInfo);
                    sv.fullScroll(View.FOCUS_DOWN);
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                    break;
                case CHECK_ERROR:
                    txt_data.setText(showInfo);
                    sv.fullScroll(View.FOCUS_DOWN);
                    break;
            }
        }
    };

    private Runnable checkData = new Runnable() {
        Message msg = null;
        @Override
        public void run() {
            try {
                firstHandler = getSharedPreferences("FirstStart", MODE_PRIVATE);
                if(firstHandler.getBoolean("isFirst", true)){

                    /** 檢查是否第一次開啟 */
                    firstHandler.edit()
                            .putBoolean("isFirst", false)
                            .apply();

                    /** 檢查是否建立資料庫 */
                    new WSDBHelper(StartActivity.this, WSFirstData.DB_NAME, WSFirstData.DB_VERSION).getReadableDatabase();
                    showInfo += getResources().getString(R.string.init_db) + "\n";
                    msg = handler.obtainMessage();
                    msg.what = CHECK_DB;
                    msg.sendToTarget();

                    /** 檢查預設語言 */
                    showInfo += getResources().getString(R.string.init_language) + "\n";
                    msg = handler.obtainMessage();
                    msg.what = CHECK_LANGUAGE;
                    msg.sendToTarget();
                }


                firstHandler = getSharedPreferences("UserPassword", MODE_PRIVATE);
                if(!firstHandler.getString("passWd","").equals("")) {

                    /** 檢查是否設定密碼 */
                    showInfo += getResources().getString(R.string.init_password) + "\n";
                    InputPasswd = sharedPreferences.getString("passWd", "");
                    msg = handler.obtainMessage();
                    msg.what = CHECK_PW;
                    msg.sendToTarget();

                }

                /** 取得分享資料 **/
                String data = sharingIntent(StartActivity.this, getIntent());
                String type = getIntent().getType();
                if(!TextUtils.isEmpty(data)){
                    if("text/plain".equals(type)) {
                        Log.e("StartActivity", "Shared Date : " + data);
                        Thread.sleep(500);
                        showInfo += getResources().getString(R.string.init_sharing) + "\n";
                        msg = handler.obtainMessage();
                        msg.obj = data;
                        msg.what = CHECK_SHARING;
                        msg.sendToTarget();
                        return;
                    }
                }

                /** 檢查完畢 */
                Thread.sleep(500);
                msg = handler.obtainMessage();
                msg.what = CHECK_OK;
                msg.sendToTarget();

            } catch (Exception e){

                /** 檢查發生錯誤 */
                showInfo += getResources().getString(R.string.init_error) + "\n";
                Log.e("StartActivity", "Error Message : " + e.getMessage());
                msg = handler.obtainMessage();
                msg.what = CHECK_ERROR;
                msg.sendToTarget();

            }
        }
    };

    public static String sharingIntent(Context context, Intent intent){
        String action = intent.getAction();
        String type = intent.getType();
        if(Intent.ACTION_SEND.equals(action) && type!=null){
            if("text/plain".equals(type)){
                return handleSendText(intent);
            }
        }
        return "";
    }

    private static String handleSendText(Intent intent){
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedText != null){
            return sharedText;
        }
        return "";
    }


}
