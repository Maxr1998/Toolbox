package de.Maxr1998.Toolbox;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.chainfire.libsuperuser.Shell;

public class LogCatService extends IntentService {

    public static boolean SU_AVAILABLE;

    public LogCatService() {
        super("LogCatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        File logfile = new File(Environment.getExternalStorageDirectory().getPath() + "/logs/logcat.log");

        if (logfile.exists()) {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(cDate);

            File old = new File(Environment.getExternalStorageDirectory().getPath() + "/logs/logcat_" + fDate + ".log");
            logfile.renameTo(old);
        }
        SU_AVAILABLE = Shell.SU.available();

        Shell.SH.run("mkdir " + Environment.getExternalStorageDirectory().getPath() + "/logs/");

        if (SU_AVAILABLE) {
            Shell.SU.run("logcat > " + logfile);
        } else {
            Shell.SH.run("logcat > " + logfile);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
