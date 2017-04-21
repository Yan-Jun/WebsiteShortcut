package edu.cise.stu.websiteshortcut;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitsActivity extends PageView {

    ArrayList<Map<String,String>> initList;
    private ArrayList urlList;
    private TextView txt_title;
    private EditText editText;
    private ImageButton btn_start,btn_show;
    private ListView lsv_result;
    private View view;
    private Context context;
    private ProgressDialog pd;

    public static final int URL_STATE_ISWEB = 1010;
    public static final int URL_STATE_UNKONWWEB = 1011;
    public static final int URL_STATE_ERRORWEB = 1012;

    private static final int URL_HANDLER_OK = 1000;
    private static final int URL_HANDLER_WORKING = 1001;
    private static final int URL_HANDLER_ERROR = 1002;

    public SplitsActivity(Context context) {
        super(context);
        this.context = context;
        //設定View 等於佈局打氣筒...
        view = LayoutInflater.from(context).inflate(R.layout.activity_splits, null);
        initView();
        initData();
        addView(view);
    }

    private void initView(){

        txt_title = (TextView) view.findViewById(R.id.txt_title_p1);
        editText = (EditText) view.findViewById(R.id.edt_Text_p1);
        btn_start = (ImageButton) view.findViewById(R.id.btn_start_p1);
        btn_start.setOnClickListener(clickListener);
        btn_show = (ImageButton) view.findViewById(R.id.btn_start_p2);
        btn_show.setOnClickListener(clickListener);
        lsv_result = (ListView) view.findViewById(R.id.lsv_result_p1);

    }

    private void initData(){

        //若是有傳送資料，就設定完成
        editText.setText(MainActivity.getShareWord());

        //測試用
        //editText.setText("http://www.stu.edu.tw/#&panel1-1");

        // 這只是初始化清單資料，於2016/9/15 不使用
        //initList = new ArrayList<>();
        //HashMap<String,String> initData = new HashMap<>();
        //initData.put("title","點擊這裡");
        //initData.put("url","輸入含有網址的文字");
        //initList.add(initData);
        //lsv_result.setAdapter(new SimpleAdapter(view.getContext(), initList, R.layout.list_layout, new String[]{"title", "url"}, new int[]{R.id.txt_title_list, R.id.txt_url_list}));
        //lsv_result.setOnItemClickListener(listListener);

        // nest 巢穴
        // 限定 Android 5.0	api21 於 2016/9/8 不使用
        // lsv_result.setNestedScrollingEnabled(true);
    }

    private Button.OnClickListener clickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btn_start_p1:
                    //work handler
                    String tmp = editText.getText().toString();
                    if(!tmp.equals("")){
                        tmp += "\n";
                        urlList = getURL(tmp); //取得網址
                        Log.d("WSLog", "get url : " + urlList);
                        editText.setText("");
                        if(urlList.size() > 0) {
                            txt_title.setText("解析文字取得網址完成 發現 "+ urlList.size() +" 筆");
                            new Thread(new mSplitRunnable(urlList)).start();
                        } else {
                            txt_title.setText("解析文字取得網址完成 發現 0 筆");
                        }
                    } else {
                        txt_title.setText("您輸入的內容是空的");
                    }
                    break;
                case R.id.btn_start_p2:
                    //show
                    final EditText input = new EditText(view.getContext());
                    input.setMaxHeight(view.getHeight()-50);
                    input.setMaxWidth(view.getWidth()-50);
                    input.setText(editText.getText().toString());
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("輸入對話框")
                            .setMessage("請輸入要取得網址的文字")
                            .setView(input)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editText.setText(input.getText());
                                }
                            })
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("清除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editText.setText("");
                                }
                            })
                            .show();
                    break;
            }


        }
    };



    private Handler mSplitHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case URL_HANDLER_OK:
                    if(pd!=null && pd.isShowing()){
                        pd.dismiss();
                    }
                    lsv_result.setAdapter(null);
                    lsv_result.setAdapter(new SplitsListTheme(context, (List<Map>) msg.obj));
                    Toast.makeText(context, "完成", Toast.LENGTH_SHORT).show();
                    break;
                case URL_HANDLER_WORKING:
                    if(pd!=null && pd.isShowing()){
                        pd.dismiss();
                    } else {
                        pd = new ProgressDialog(context);
                        pd.setTitle("提示");
                        pd.setMessage("處理中");
                        pd.setCancelable(false);
                        pd.setButton(ProgressDialog.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                    pd.show();
                    break;
                case URL_HANDLER_ERROR:
                    if(pd!=null && pd.isShowing()){
                        pd.dismiss();
                    }
                    Toast.makeText(context, "發生錯誤", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private class mSplitRunnable implements Runnable{
        private List mList;
        private List<Map> mNewList;
        private Message message;

        public mSplitRunnable(ArrayList<String> data){
            mList = data;
        }

        @Override
        public void run() {
            //這個是利用Jsoup針對Xml標籤的爬蟲來取資料
            try {
                mNewList = new ArrayList<>();

                //處理訊息工作 (工作中)
                message = mSplitHandler.obtainMessage();
                message.what = URL_HANDLER_WORKING;
                message.sendToTarget();

                for(int i = 0; i<mList.size(); i++) {
                    //取得網頁名稱工作
                    String tmp = "";
                    int state = 0;
                    try{
                        URL url = new URL((String) mList.get(i));
                        Document xmlDoc = Jsoup.parse(url, 1000);
                        Elements title = xmlDoc.select("title");
                        tmp = title.get(0).text();
                        state = URL_STATE_ISWEB;
                    } catch (SocketTimeoutException e1){
                        //超時例外
                        tmp = "未知網站 - 讀取超時 ";
                        state = URL_STATE_UNKONWWEB;
                    } catch (UnknownHostException e2){
                        //未知主機例外
                        tmp = "未知網站 - 無發現主機";
                        state = URL_STATE_ERRORWEB;
                    } catch (HttpStatusException e3){
                        //連結404例外
                        tmp = "未知網站 - 無發現網站";
                        state = URL_STATE_ERRORWEB;
                    } catch (ConnectException e4){
                        //連結失敗例外
                        tmp = "未知網站 - 連結失敗";
                        state = URL_STATE_ERRORWEB;
                    } catch (IndexOutOfBoundsException e5){
                        //讀取標籤失敗
                        tmp = "未知網站 - 取得失敗";
                        state = URL_STATE_UNKONWWEB;
                    } catch (Exception e6){
                        //連結失敗例外
                        tmp = "未知網站 - " + e6;
                        state = URL_STATE_ERRORWEB;
                    }

                    //處理資料工作
                    HashMap<String,String> mapData = new HashMap<>();
                    mapData.put("title", tmp);
                    mapData.put("url", (String) mList.get(i));
                    mapData.put("state", String.valueOf(state));
                    mNewList.add(mapData);

                }

                //處理訊息工作 (完成)
                message = mSplitHandler.obtainMessage();
                message.what = URL_HANDLER_OK;
                message.obj = mNewList;
                message.sendToTarget();
                Log.e("WSLog", "get title :" + mNewList);

            } catch (Exception e){
                e.printStackTrace();
                Log.e("WSLog", "get title error : " + e.getMessage());
                //處理訊息工作 (錯誤)
                message = mSplitHandler.obtainMessage();
                message.what = URL_HANDLER_ERROR;
                message.sendToTarget();
            }
        }

    }

    private ArrayList<String> getURL(String tmp){
        ArrayList<String> setURL = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new StringReader(tmp));
            String line;

            while((line = br.readLine())!=null){

                //符合正規表示
                String regularExpression = "[\\w]{2,}[.]{1}[\\w]{2,}.*";
                if(line.matches(regularExpression)){
                    setURL.add("http://" + line.trim());
                }

                //開頭有http
                if(line.indexOf("http", 0) != -1){
                    setURL.add(line.substring(line.indexOf("http", 0)).trim());
                } else if(line.indexOf("HTTP", 0) != -1){
                    setURL.add(line.substring(line.indexOf("HTTP", 0)).trim());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return setURL;
    }

    @Override
    public void refreshView() {
        Log.e("WSLog", "SplitsActivity");
    }


}
