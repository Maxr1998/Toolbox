package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BackupFragment extends Fragment {

    private static Activity ACTIVITY;
    SharedPreferences prefs;
    String internalStorageLoc;
    String externalStorageLoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(android.R.layout.list_content, container, false);

        return rootView;
    }
}