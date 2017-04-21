package edu.cise.stu.websiteshortcut;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class BackupActivity extends PageView {

    public BackupActivity(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_backup, null);
        addView(view);
    }

    @Override
    public void refreshView() {
        Log.e("WSLog", "BackupActivity");
    }
}
