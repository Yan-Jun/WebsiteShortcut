package edu.cise.stu.websiteshortcut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveActivity extends PageView {

    private View view;
    private Context context;
    private RecyclerView recycler_view;
    private LinearLayoutManager layoutManager;
    private adapter recyclerAdapter;

    public SaveActivity(Context context) {
        super(context);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_save, null);
        initView();
        initData();
        addView(view);
    }

    private void initView(){
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    private void initData(){
        layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(layoutManager);
        recyclerAdapter = new adapter(WSDBHandler.query(view.getContext()));

        //這是設定項目之間的距離
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 5;
            }
        };
        recycler_view.addItemDecoration(itemDecoration);
        recycler_view.setAdapter(recyclerAdapter);
    }

    //建立一個適配器，繼承Recycler View 的適配器，設定 Template 放入自己的 View Holder
    public class adapter extends RecyclerView.Adapter<adapter.ViewHolder>{
        private List<Map<String,String>> mData;

        public adapter(List data){
            mData = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView mTextView1, mTextView2;
            public ImageButton mShortcut, mCenn, mShare, mDelete;

            public ViewHolder(View itemView) {
                super(itemView);

                // Text View
                mTextView1 = (TextView) itemView.findViewById(R.id.q_list_txt);
                mTextView2 = (TextView) itemView.findViewById(R.id.q_list_url);

                // Image View
                mShortcut = (ImageButton) itemView.findViewById(R.id.q_btn_shortcut);
                mCenn = (ImageButton) itemView.findViewById(R.id.q_btn_cenn);
                mShare = (ImageButton) itemView.findViewById(R.id.q_btn_share);
                mDelete = (ImageButton) itemView.findViewById(R.id.q_btn_delete);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.query_list_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            HashMap<String,String> hashMap = (HashMap<String, String>) mData.get(position);
            holder.mTextView1.setText(hashMap.get("webName"));
            holder.mTextView2.setText(hashMap.get("urlStr"));
            holder.mShortcut.setOnClickListener(new addShortcutListener(hashMap.get("webName"), hashMap.get("urlStr"), hashMap.get("webId")));
            holder.mCenn.setOnClickListener(new addShortcutListener(hashMap.get("webName"), hashMap.get("urlStr"), hashMap.get("webId")));
            holder.mShare.setOnClickListener(new addShortcutListener(hashMap.get("webName"), hashMap.get("urlStr"), hashMap.get("webId")));
            holder.mDelete.setOnClickListener(new addShortcutListener(hashMap.get("webName"), hashMap.get("urlStr"), hashMap.get("webId")));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void swap(List data){
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

        public void searchData(String keyWord){
            mData.clear();
            mData.addAll(WSDBHandler.queryIf(context, keyWord));
            notifyDataSetChanged();
        }

    }

    private class addShortcutListener implements ImageButton.OnClickListener{
        private String webId;
        private String webName;
        private String webUrl;

        public addShortcutListener(String name, String url, String id){
            webId = id;
            webName = name;
            webUrl = url;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.q_btn_shortcut:

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
                case R.id.q_btn_cenn:
                    /** 連結網頁 **/
                    Intent cennIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                    context.startActivity(Intent.createChooser(cennIntent, "請選擇要開啟的瀏覽器"));
                    break;
                case R.id.q_btn_share:
                    /** 分享連結 **/
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, webName);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, webName + "\n" +webUrl);
                    context.startActivity(Intent.createChooser(shareIntent, "請選擇要分享程式"));
                    break;
                case R.id.q_btn_delete:
                    /**  刪除連結 **/
                    new AlertDialog.Builder(context)
                            .setTitle("刪除提醒")
                            .setMessage("您確定要刪除這個網址嗎 ?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new WSDBHandler().delete(context, webId);
                                    MainActivity.updateView(); //更新所有畫面
                                }
                            })
                            .setNegativeButton("Cancer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
            }
        }
    }

    @Override
    public void refreshView() {
        Log.e("WSLog", "SaveActivity");

        if(!MainActivity.getKeyWord().equals("")){
            //若使用者有使用搜尋就開始尋找項目
            recyclerAdapter.searchData(MainActivity.getKeyWord());
        } else {
            //呼叫RecyclerView.Adapter自定義的方法更新資料
            recyclerAdapter.swap(WSDBHandler.query(view.getContext()));
        }
    }


}
