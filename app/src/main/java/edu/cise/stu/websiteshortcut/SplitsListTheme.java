package edu.cise.stu.websiteshortcut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by USOON on 2016/9/1.
 */
public class SplitsListTheme extends BaseAdapter {

    private int[] icon = {android.R.drawable.presence_online,
                            android.R.drawable.presence_invisible,
                            android.R.drawable.presence_busy};
    private int[] status = {R.string.txt_availableURL,
                            R.string.txt_unavailableURL};
    private LayoutInflater inflater;
    private List<Map> listData;
    private List<Boolean> isAdd;
    private Context context;

    public SplitsListTheme(Context context, List<Map> data) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listData = data;
        isAdd  = new ArrayList<>();
        for (int i = 0; i < this.listData.size(); i++) {
            isAdd.add(Boolean.FALSE);
        }
        Log.e("WSLog", "listData size = " + listData.size());
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.splits_list_layout, null);
        ImageView img_status = (ImageView) convertView.findViewById(R.id.s_list_img);
        TextView txt_title = (TextView) convertView.findViewById(R.id.s_list_txt);
        TextView txt_url = (TextView) convertView.findViewById(R.id.s_list_url);
        ImageButton btn_shortcut = (ImageButton) convertView.findViewById(R.id.s_btn_shortcut);
        ImageButton btn_cenn = (ImageButton) convertView.findViewById(R.id.s_btn_cenn);
        ImageButton btn_share = (ImageButton) convertView.findViewById(R.id.s_btn_share);
        ImageButton btn_delete = (ImageButton) convertView.findViewById(R.id.s_btn_delete);

        //取得下一筆資料
        Map<String,String> data = listData.get(position);
        //這筆資料存在，且未點擊加入
        if(data != null && !isAdd.get(position)){

            //取得各項資料
            String title = data.get("title");
            String url = data.get("url");
            String stateTmp = data.get("state");
            //Log.e("WSLog", "Splits List[" + position + "] = [title]:" + title + " [url]:" + title + " [state]:" + stateTmp);
            int state = Integer.parseInt(stateTmp);

            //處理按鈕工作
            btn_shortcut.setOnClickListener(new splitsButtonListener(title, url));
            btn_cenn.setOnClickListener(new splitsButtonListener(title, url));
            btn_share.setOnClickListener(new splitsButtonListener(title, url));
            btn_delete.setOnClickListener(new splitsButtonListener(title, url));

            //針對不同資料做處理
            switch (state){
                case SplitsActivity.URL_STATE_ISWEB:
                    //可用網站
                    img_status.setImageResource(icon[0]);
                    txt_title.setText(title); //網站名稱
                    txt_url.setText(url); //網站位置
                    break;
                case SplitsActivity.URL_STATE_UNKONWWEB:
                    //未知網站
                    img_status.setImageResource(icon[1]);
                    txt_title.setText(title); //未知網站
                    txt_url.setText(url); //網站位置
                    break;
                case SplitsActivity.URL_STATE_ERRORWEB:
                    //錯誤網站
                    img_status.setImageResource(icon[2]);
                    txt_title.setText(title); //錯誤網址
                    txt_url.setText(url); //網站位置
                    break;
            }
        }

        return convertView;
    }

    private class splitsButtonListener implements ImageButton.OnClickListener{
        private String webName;
        private String webUrl;

        public splitsButtonListener(String name, String url){
            webName = name;
            webUrl = url;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.s_btn_shortcut:

                    /** 建立捷徑 **/
                    //Intent shortcutIntent = new Intent(context, MainActivity.class);
                    //shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    /** 建立捷徑後要做什麼動作 **/
                    Intent shortcutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                    /** 建立捷徑前至動作 **/
                    final Intent addIntent = new Intent();
                    //捷徑行為
                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                    //捷徑名稱
                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, webName);
                    //捷徑圖示
                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                            Intent.ShortcutIconResource.fromContext(context, R.drawable.ws_icon));
                    //行為
                    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

                    EditText enterName = new EditText(context);
                    enterName.setText(webName);
                    new AlertDialog.Builder(context)
                            .setTitle("新增至主畫面")
                            .setView(enterName)
                            .setPositiveButton("加入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.sendBroadcast(addIntent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
                case R.id.s_btn_cenn:
                    /** 連結網頁 **/
                    Intent cennIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                    context.startActivity(Intent.createChooser(cennIntent, "請選擇要開啟的瀏覽器"));
                    break;
                case R.id.s_btn_share:
                    /** 分享連結 **/
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, webName);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, webName + "\n" +webUrl);
                    context.startActivity(Intent.createChooser(shareIntent, "請選擇要分享程式"));
                    break;
                case R.id.s_btn_delete:
                    /**  儲存連結 **/
                    //使用者輸入資料
                    final EditText input = new EditText(context);
                    input.setText(webName);

                    new AlertDialog.Builder(context)
                            .setTitle("儲存至資料庫")
                            .setIcon(R.drawable.ws_icon)
                            .setMessage("請輸入網址的名稱")
                            .setView(input)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!input.getText().toString().equals("")){
                                        new WSDBHandler().insert(context, input.getText().toString() , webUrl);
                                    } else {
                                        new WSDBHandler().insert(context, context.getResources().getString(R.string.txt_availableURL), webUrl);
                                    }
                                    MainActivity.updateView(); //更新所有畫面
                                    Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
            }
        }
    }
}
