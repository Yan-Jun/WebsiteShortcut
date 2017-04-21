package edu.cise.stu.websiteshortcut;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private WSBroadcastReceiver receiver;
    private TabLayout mTabLayout;
    private ViewPager mPageView;
    private Toolbar toolbar;
    private static List<PageView> pageList;
    private static String KeyWord;
    private static String ShareWord;

    static {
        pageList = null;
        KeyWord = "";
        ShareWord = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView(){
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        //設定TabLayout要顯示的圖片，TabLayout的setIcon圖片太小
        View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
        //設定圖片資源
        view1.findViewById(R.id.icon).setBackgroundResource(android.R.drawable.ic_menu_edit);
        //透過setCustom來設定我們的圖片
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(view1));

        View view2 = getLayoutInflater().inflate(R.layout.customtab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(android.R.drawable.ic_menu_save);
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(view2));

        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(android.R.drawable.ic_menu_manage);
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(view3));
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //把新增好的ToolBar給設定好
        setSupportActionBar(toolbar);
        //把要inflate的菜單資源設定好
        toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar監聽器
        toolbar.setOnMenuItemClickListener(menuListener);
        //設定在toolbar上的菜單按鈕顯示出來
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //設定在toolbar上的菜單按鈕能起作用
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(R.string.btn_split);




        mPageView = (ViewPager) findViewById(R.id.pager);
        mPageView.setAdapter(new SamplePagerAdapter());
        initListener();
    }

    private void initData(){

        //取得分享資料
        String sharedData = getIntent().getStringExtra("SHARING");
        if(!TextUtils.isEmpty(sharedData)){
            ShareWord = sharedData;
        }

        pageList = new ArrayList<>();
        pageList.add(new SplitsActivity(MainActivity.this));
        pageList.add(new SaveActivity(MainActivity.this));
        pageList.add(new BackupActivity(MainActivity.this));
    }

    private void initListener(){
        //選擇後更換標題
        final int[] title = {R.string.btn_split, R.string.txt_title_page2, R.string.txt_title_page3};

        //標籤選擇監聽器
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //選擇後
                mPageView.setCurrentItem(tab.getPosition());
                toolbar.setTitle(title[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //未選擇
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //重新選擇
            }
        });
        //對ViewPage設定案一下改變監聽器，由標籤來監聽
        mPageView.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    private class SamplePagerAdapter extends PagerAdapter{

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //實例項目
            //instantiate = 實例
            container.addView(pageList.get(position));
            return pageList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //銷毀項目
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            //取得長度
            return pageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            //這個畫面是否來至物件本身
            return object == view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //建立選項菜單
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        //製作搜尋畫面
        MenuItem searchItem = menu.findItem(R.id.search); //尋找要處理的標籤項目
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE); //取得搜尋服務給管理者

        SearchView searchView = null;
        if (searchItem != null) {
            //如果有抓到標籤項目，就給SearchView 處理
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            //如果SearchView存在，就設定可搜尋的訊息
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //提交時做什麼
                    //if( !searchView.isIconified()) {
                        //searchView.setIconified(true);
                    //}
                    //searchItem.collapseActionView();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //改變時做什麼
                    mPageView.setCurrentItem(1); //並移動到Save頁面
                    KeyWord = newText;
                    updateView();

                    return true;
                }
            });
        }

        return true;
    }

    private Toolbar.OnMenuItemClickListener menuListener = new Toolbar.OnMenuItemClickListener() {
        //這是Toolbar菜單監聽器
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.more:

                    /*
                    // 2016.9.6 由於無法顯示於中心，所以不使用。
                    TextView textView = new TextView(MainActivity.this);
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, textView, Gravity.CENTER);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(popupMenuListener);
                    popupMenu.show();
                    */

                    ArrayAdapter<String> popupMenuData = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1);
                    popupMenuData.add(getResources().getString(R.string.popupMenu1));
                    popupMenuData.add(getResources().getString(R.string.popupMenu2));
                    popupMenuData.add(getResources().getString(R.string.popupMenu3));
                    popupMenuData.add(getResources().getString(R.string.popupMenu4));

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("其它設定")
                            .setAdapter(popupMenuData, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            //about app
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setMessage((getResources().getString(R.string.aboutApp)))
                                                    .setCancelable(true)
                                                    .show();
                                            break;
                                        case 1:
                                            //sent email
                                            Intent i = new Intent(Intent.ACTION_SEND);
                                            i.setType("message/rfc822");
                                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"s14113136@stu.edu.tw"});
                                            i.putExtra(Intent.EXTRA_SUBJECT, "Website Shortcut Beta 1.0.0");
                                            i.putExtra(Intent.EXTRA_TEXT   , "Return application errors:");
                                            try {
                                                startActivity(Intent.createChooser(i, "請選擇寄信程式"));
                                            } catch (android.content.ActivityNotFoundException ex) {
                                                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                        case 2:
                                            //set language
                                            break;
                                        case 3:
                                            //set password
                                            final EditText editText = new EditText(MainActivity.this);
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle(getResources().getString(R.string.popupMenu4))
                                                    .setMessage("請輸入密碼")
                                                    .setView(editText)
                                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String tmp = editText.getText().toString();
                                                            if(tmp.equals("")){

                                                            } else {

                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                            break;
                                    }
                                }
                            })
                            .setCancelable(true)
                            .show();
                    break;
                case R.id.search:
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("標題")
                            .setMessage("search")
                            .setNeutralButton("確定", null)
                            .setCancelable(true)
                            .show();
                    break;
            }
            return true;
        }
    };

    public static void updateView(){
        //更新所有的畫面
        try {
            for (PageView view : pageList) {
                view.refreshView();
            }
        } catch (Exception e){
            Log.e("WSLog", e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
        receiver = new WSBroadcastReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public static String getKeyWord(){
        return KeyWord;
    }

    public static String getShareWord(){
        return ShareWord;
    }

    // 2016.9.6 由於無法顯示於中心，所以不使用。
    private PopupMenu.OnMenuItemClickListener popupMenuListener = new PopupMenu.OnMenuItemClickListener() {
        //這是PopupMenu菜單監聽器
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return true;
        }
    };
}

