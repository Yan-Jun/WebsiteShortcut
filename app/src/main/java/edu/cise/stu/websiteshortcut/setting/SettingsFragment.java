package edu.cise.stu.websiteshortcut.setting;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import edu.cise.stu.websiteshortcut.R;

/**
 * Created by USOON on 2016/9/10.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
