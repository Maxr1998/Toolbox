package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

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
}
