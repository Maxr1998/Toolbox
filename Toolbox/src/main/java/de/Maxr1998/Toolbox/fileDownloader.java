package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class fileDownloader extends AsyncTask<String, Integer, Boolean> {

    static HttpURLConnection URL_CONNECTION;
    private final Activity activity;
    private final URL fileUrl;
    private final File fileLocation;
    private final Boolean showProgress;
    private ProgressDialog progressDialog;

    fileDownloader(Activity activity, URL fileUrl, File fileLocation, Boolean showProgress) {
        this.activity = activity;
        this.fileUrl = fileUrl;
        this.fileLocation = fileLocation;
        this.showProgress = showProgress;
    }

    @Override
    public void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(activity.getString(R.string.pleaseWait));
        progressDialog.setMessage(activity.getString(R.string.downloadingManifest));
        if (showProgress) progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            URL url = new URL(activity.getString(R.string.manifest_link).trim());

            URL_CONNECTION = (HttpURLConnection) fileUrl.openConnection();
            URL_CONNECTION.connect();
            FileOutputStream fileOutput = new FileOutputStream(fileLocation);
            InputStream inputStream = URL_CONNECTION.getInputStream();

            int totalSize = URL_CONNECTION.getContentLength();
            progressDialog.setMax(totalSize);
            int downloadedSize = 0;

            byte[] buffer = new byte[1024];
            int bufferLength;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                progressDialog.setProgress(downloadedSize);

            }
            fileOutput.close();
            URL_CONNECTION.disconnect();
            return true;

        } catch (MalformedURLException e) {
            Log.d("File download error", e.toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onPostExecute(Boolean successful) {
        if (progressDialog != null) progressDialog.dismiss();
        if (successful) {
            Utils.showToast(activity, R.string.done);
        } else {

            new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(":(")
                    .setMessage(activity.getString(R.string.service_not_available))
                    .setNeutralButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
        }
    }
}
