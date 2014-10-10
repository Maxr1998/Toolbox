package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class LogCatFragment extends Fragment {

    static Activity ACTIVITY;
    SharedPreferences prefs;

    private boolean isLCServiceRunning() {
        ActivityManager manager = (ActivityManager) ACTIVITY.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("de.Maxr1998.Toolbox.LogCatService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logcat, container, false);

        ACTIVITY = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(ACTIVITY);

        ToggleButton LCServiceToggle = (ToggleButton) rootView.findViewById(R.id.LCServiceToggle);

        if (isLCServiceRunning()) {
            LCServiceToggle.setChecked(true);
            Log.d("LogCatService", "Service running");
        } else Log.d("LogCatService", "Service NOT running");

        LCServiceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //@Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent LCService = new Intent(ACTIVITY, LogCatService.class);
                if (isChecked) {
                    ACTIVITY.startService(LCService);
                } else {
                    ACTIVITY.stopService(LCService);
                }
            }
        });
        return rootView;
    }
}