package edu.cise.stu.websiteshortcut.setting;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import edu.cise.stu.websiteshortcut.R;

/**
 * Created by USOON on 2016/9/10.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }
}
