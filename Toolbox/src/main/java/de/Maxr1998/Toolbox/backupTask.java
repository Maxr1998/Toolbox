package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import eu.chainfire.libsuperuser.Shell;

public class backupTask extends AsyncTask<Void, Void, Void> {
    private final Activity activity;
    // Declaring list variables
    private ProgressDialog progressDialog;

    backupTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onPreExecute() {
        //File dataDir = new File(Utils.getDataDir(activity));
        //manifestFile = new File(dataDir + File.separator + activity.getString(R.string.manifest_filename));

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(activity.getString(R.string.pleaseWait));
        progressDialog.setMessage("");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }

    @Override
    protected Void doInBackground(Void... params) {

        //Shell.SH.run("tar -czf /sdcard/Backup/test.tar.gz /sdcard/test/*" + " " + "" + "--exclude=1 --exclude=4 --exclude=3");

        Utils.d("External Storage: " + Environment.getExternalStorageDirectory().getAbsolutePath() + " =-=-= Download Cache: " + Environment.getDownloadCacheDirectory().getAbsolutePath() + " =-=-= Data: " + Environment.getDataDirectory().getAbsolutePath());

        for (String line : Shell.SU.run("ls -la /storage")) {
            Utils.d(line);
        }

        return null;
    }

    @Override
    public void onPostExecute(Void param) {
        progressDialog.dismiss();

    }
}