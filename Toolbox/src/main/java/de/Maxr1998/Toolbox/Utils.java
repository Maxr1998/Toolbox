package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.collect.ImmutableSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class Utils {

    private static String dataDir;

    public static String getDataDir(Activity activity) {
        if (dataDir == null) {
            PackageManager m = activity.getPackageManager();
            String xdataDir = activity.getPackageName();
            try {
                PackageInfo p = m.getPackageInfo(xdataDir, 0);
                dataDir = p.applicationInfo.dataDir;
                return dataDir;
            } catch (Exception e) {
                return "FIRE!!!!!!!!";
            }
        } else {
            return dataDir;
        }
    }

    public static ArrayList<String> catToList(String file) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                list.add(str);
            }
        } catch (Exception e) {
            list.clear();
            list.add("FIRE!!!!!!!!");
            return list;
        }
        return list;
    }

    public static void showToast(Context c, int id) {
        Toast.makeText(c, id, Toast.LENGTH_SHORT).show();
    }

    public static void logToFile(String msg) {
        try {
            FileWriter Logger = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/logs/Toolbox.log", true);
            Logger.write(msg);
            Logger.write("\n");
            Logger.flush();
            Logger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void d(String msg) {
        logToFile(msg);
    }

    public static void addNewProfile(Context context, String profile_name, String source, String server, String port, String user, String destination, String arguments) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> prefProfiles = pref.getStringSet(Common.PREF_PROFILES, null);

        ImmutableSet.Builder<String> newProfiles = new ImmutableSet.Builder<String>().add(profile_name);

        if (prefProfiles != null)
            newProfiles.addAll(prefProfiles);

        pref.edit().putStringSet(Common.PREF_PROFILES, newProfiles.build()).commit();

        String currProfileValues = source + ":" + server + ":" + port + ":" + user + ":" + destination + ":" + arguments;
        pref.edit().putString(profile_name, currProfileValues).commit();
    }

    public static void changeProfile(Context context, String profile_name, String source, String server, String port, String user, String destination, String arguments, String old_name) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> prefProfiles = pref.getStringSet(Common.PREF_PROFILES, null);

        prefProfiles.remove(old_name);
        pref.edit().putStringSet(Common.PREF_PROFILES, prefProfiles).commit();
        pref.edit().remove(old_name).commit();

        addNewProfile(context, profile_name, source, server, port, user, destination, arguments);
    }

    public static String[] getValuesForProfile(Context context, String profile) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String prefProfiles = pref.getString(profile, null);

        if (prefProfiles == null)
            throw new NullPointerException("Values of selected profile are null!");

        return prefProfiles.split(":");

    }
}
