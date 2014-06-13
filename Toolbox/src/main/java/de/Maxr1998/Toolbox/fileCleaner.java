package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class fileCleaner extends AsyncTask<String, String, Boolean> {

    public static boolean suAvailable;
    private final Activity activity;
    File manifestFile;
    // Declaring list variables
    ArrayList<String> ads;
    ArrayList<String> pAds;
    ArrayList<String> thumbs;
    ArrayList<String> pThumbs;
    private ArrayList<String> tasks;
    private Integer nrOfTasks;
    private ProgressDialog progressDialog;

    fileCleaner(Activity activity, Integer nrOfTasks, ArrayList<String> tasks) {
        this.activity = activity;
        this.nrOfTasks = nrOfTasks;
        this.tasks = tasks;
    }

    @Override
    public void onPreExecute() {
        File dataDir = new File(Utils.getDataDir(activity));
        manifestFile = new File(dataDir + File.separator + activity.getString(R.string.manifest_filename));

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(activity.getString(R.string.pleaseWait));
        //progressDialog.setMessage(activity.getString(R.string.deleting_files));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(nrOfTasks);
        progressDialog.setProgress(0);
        progressDialog.show();

    }

    @Override
    protected Boolean doInBackground(String... strings) {
        // Checking SU
        suAvailable = Shell.SU.available();

        try {
            ArrayList<String> content = Utils.catToList(manifestFile.toString());

            // Creating lists
            ads = new ArrayList<String>();
            pAds = new ArrayList<String>();
            thumbs = new ArrayList<String>();
            pThumbs = new ArrayList<String>();

            // Filling lists
            for (String line : content) {
                if (line.contains("ads")) {
                    ads.add(Environment.getExternalStorageDirectory().getPath() + File.separator + (((line.split(":"))[1])).trim());
                } else if (line.contains("pAds")) {
                    pAds.add((((line.split(":"))[1])).trim());
                } else if (line.contains("thumbs")) {
                    thumbs.add(Environment.getExternalStorageDirectory().getPath() + File.separator + (((line.split(":"))[1])).trim());
                } else if (line.contains("pThumbs")) {
                    pThumbs.add((((line.split(":"))[1])).trim());
                } else Log.d("Manifest processing", "Ignoring comment");
            }

            for (String mode : tasks) {

                progressDialog.setMessage(/*activity.getString(R.string.cancel_nr)*/"Test");

                // DELETING FILES!
                if (mode.equals("ads")) {
                    for (String a_file : ads) {
                        Shell.SH.run("rm -r " + a_file);
                    }
                    for (String pa_file : pAds) {
                        if (suAvailable) {
                            Shell.SU.run("find -name " + pa_file + " -exec rm -r {} \\;");
                        } else {
                            String[] cmds = {"cd " + Environment.getExternalStorageDirectory().getPath(), "find -name " + pa_file + " -exec rm -r {} \\;"};
                            Shell.SH.run(cmds);
                        }
                    }
                } else if (mode.equals("thumbs")) {
                    for (String t_file : thumbs) {
                        Shell.SH.run("rm -r " + t_file);
                    }
                    for (String pt_file : pThumbs) {
                        if (suAvailable) {
                            Shell.SU.run("find -name " + pt_file + " -exec rm -r {} \\;");
                        } else {
                            String[] cmds = {"cd " + Environment.getExternalStorageDirectory().getPath(), "find -name " + pt_file + " -exec rm -r {} \\;"};
                            Shell.SH.run(cmds);
                        }
                    }
                } else if (mode.equals("logs")) {
                    if (suAvailable) {
                        String[] cmds = {"find -name *.log -type f -exec rm -r {} \\;", "find -name log -type d -exec rm -r {} \\;", "find -name logs -type d -exec rm -r {} \\;"};
                        Shell.SU.run(cmds);
                    } else {
                        String[] cmds = {"cd " + Environment.getExternalStorageDirectory().getPath(), "find -name *.log -type f -exec rm -r {} \\", "find -name \"log\" -or -name \"logs\" -type d -exec rm -r {} \\;"};
                        Shell.SH.run(cmds);
                    }
                } else if (mode.equals("tmp")) {
                    if (suAvailable) {
                        String[] cmds = {"find -name *.tmp -type f -exec rm -r {} \\;", "find -name tmp -type d -exec rm -r {} \\;"};
                        Shell.SU.run(cmds);
                    } else {
                        String[] cmds = {"cd " + Environment.getExternalStorageDirectory().getPath(), "find -name *.tmp -type f -exec rm -r {} \\;", "find -name tmp -type d -exec rm -r {} \\;"};
                        Shell.SH.run(cmds);
                    }
                } else if (mode.equals("cache")) {
                    if (suAvailable) {
                        List<String> cache_partition_output = Shell.SU.run("mount | grep /cache");
                        String cache_partition1 = cache_partition_output.get(0);
                        String cache_partition = ((cache_partition1.split(" "))[0]).trim();
                        Log.d("Cache partition of device", cache_partition);
                        Shell.SU.run("make_ext4fs -a /cache " + cache_partition);
                    } else {
                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.error)
                                .setIcon(R.drawable.ic_action_error)
                                .setMessage(R.string.no_root)
                                .setNeutralButton(android.R.string.ok, null)
                                .create().show();
                    }

                } else if (mode.equals("dalvik")) {
                    if (suAvailable) {
                        Shell.SU.run("rm -rf //data//dalvik-cache");
                    } else {
                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.error)
                                .setIcon(R.drawable.ic_action_error)
                                .setMessage(R.string.no_root)
                                .setNeutralButton(android.R.string.ok, null)
                                .create().show();
                    }

                } //else if...

                progressDialog.incrementProgressBy(1);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onPostExecute(Boolean successful) {
        if (progressDialog != null) progressDialog.dismiss();
        if (successful) {
            if (tasks.contains("dalvik")) {
                new AlertDialog.Builder(activity)
                        .setIcon(R.drawable.ic_action_warning)
                        .setMessage(R.string.dalvik_post_reboot)
                        .setTitle(R.string.warning)
                        .setPositiveButton(R.string.reboot_now, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                class reboot extends AsyncTask<Void, Void, Void> {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        Shell.SU.run("reboot");
                                        return null;
                                    }

                                }
                                new reboot().execute();
                            }

                        })
                        .setNegativeButton(R.string.cancel_nr, null)
                        .create().show();
            }
            Utils.showToast(activity, R.string.done);
        } else {
            new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(":(")
                    .setMessage(activity.getString(R.string.service_not_available))
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        }
    }
}

