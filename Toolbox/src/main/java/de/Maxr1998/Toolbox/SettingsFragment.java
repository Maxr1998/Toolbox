package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static final String FORCE_UPDATE = "force_update_pref";
    public static final String ABOUT = "about_pref";
    public static final String VERSION = "version";
    private static Activity ACTIVITY;
    File manifestFile;
    SharedPreferences prefs;
    int mDevHitCountdown;
    Toast mDevHitToast;
    URL manifestUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ACTIVITY = getActivity();

        addPreferencesFromResource(R.xml.preferences);

        Preference force_update = findPreference(FORCE_UPDATE);
        force_update.setOnPreferenceClickListener(this);
        Preference about = findPreference(ABOUT);
        about.setOnPreferenceClickListener(this);
        Preference version = findPreference(VERSION);
        version.setOnPreferenceClickListener(this);


        try {
            version.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(ACTIVITY);
        if (prefs.getBoolean("dev_unlocked", false)) {
            mDevHitCountdown = 0;
        } else mDevHitCountdown = 6;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref.getKey().equals(FORCE_UPDATE)) {
            if (BaseActivity.isNetAvailable(getActivity())) {
                File dataDir = new File(Utils.getDataDir(getActivity()));
                manifestFile = new File(dataDir + File.separator + ACTIVITY.getString(R.string.manifest_filename));
                try {
                    manifestUrl = new URL(ACTIVITY.getString(R.string.manifest_link));
                } catch (MalformedURLException e) {
                    Log.d("Error", e.toString());
                    e.printStackTrace();
                }
                manifestFile.delete();
                BaseActivity.downloadManifestHelper(ACTIVITY, manifestUrl, manifestFile);
            } else new AlertDialog.Builder(ACTIVITY)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_network)
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        } else if (pref.getKey().equals(ABOUT)) {
            new AlertDialog.Builder(ACTIVITY)
                    .setIcon(R.drawable.ic_action_about)
                    .setTitle(R.string.about_pref)
                    .setMessage(R.string.about_text)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();

        } else if (pref.getKey().equals(VERSION)) {
            mDevHitCountdown--;
            if (mDevHitToast != null) {
                mDevHitToast.cancel();
            }
            if (mDevHitCountdown < 0) {
                mDevHitToast = Toast.makeText(ACTIVITY, R.string.show_dev_already, Toast.LENGTH_LONG);
                mDevHitToast.show();
            } else if (mDevHitCountdown == 0) {
                prefs.edit().putBoolean("dev_unlocked", true).commit();
                mDevHitToast = Toast.makeText(ACTIVITY, R.string.show_dev_on, Toast.LENGTH_LONG);
                mDevHitToast.show();
            } else {
                mDevHitToast = Toast.makeText(ACTIVITY, getResources().getQuantityString(
                        R.plurals.show_dev_countdown, mDevHitCountdown, mDevHitCountdown), Toast.LENGTH_SHORT);
                mDevHitToast.show();
            }
        }
        return false;
    }
}