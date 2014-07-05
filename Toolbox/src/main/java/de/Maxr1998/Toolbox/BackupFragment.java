package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class BackupFragment extends Fragment {

    private static Activity ACTIVITY;
    SharedPreferences prefs;
    String internalStorageLoc;
    String externalStorageLoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_backup, container, false);

        ACTIVITY = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(ACTIVITY);

        ImageButton button = (ImageButton) rootView.findViewById(R.id.backupButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new backupTask(ACTIVITY).execute();

                internalStorageLoc = prefs.getString("internal_storage_loc", null);
                externalStorageLoc = prefs.getString("external_storage_loc", null);
                if (internalStorageLoc == null || externalStorageLoc == null) {
                    String message = "";
                    if (internalStorageLoc == null)
                        message = ACTIVITY.getString(R.string.toast_internal_not_set);
                    if (externalStorageLoc == null) {
                        if (internalStorageLoc == null) {
                            message = message + "\n" + ACTIVITY.getString(R.string.toast_external_not_set);
                        } else message = ACTIVITY.getString(R.string.toast_external_not_set);
                    }
                    Toast.makeText(ACTIVITY, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ACTIVITY, BackupStorageSetupActivity.class);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }
}