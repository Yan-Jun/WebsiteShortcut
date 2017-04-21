package edu.cise.stu.websiteshortcut;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by USOON on 2016/8/31.
 */
public abstract class PageView extends LinearLayout {

    public PageView(Context context) {
        super(context);
    }

    public abstract void refreshView();
    //刷新畫面
}
