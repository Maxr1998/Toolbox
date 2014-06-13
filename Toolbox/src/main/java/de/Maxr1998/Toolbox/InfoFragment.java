package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.chainfire.libsuperuser.Shell;

public class InfoFragment extends Fragment {

    public static boolean SU_AVAILABLE;
    private static Activity ACTIVITY;
    View rootView;
    PackageInfo pInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);

        ACTIVITY = getActivity();

        new StartUp().execute();

        return rootView;
    }


    private class StartUp extends AsyncTask<Void, Void, Void> {

        public void storage(long space, int layout_id, int resource_id) {
            TextView storage = (TextView) rootView.findViewById(layout_id);
            storage.setText(getActivity().getResources().getString(resource_id) + " " + Long.toString(space) + "MB");
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Checking SU
            SU_AVAILABLE = Shell.SU.available();

            return null;
        }

        @Override
        public void onPostExecute(Void successful) {
            try {
                pInfo = ACTIVITY.getPackageManager().getPackageInfo(ACTIVITY.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("Error", e.toString());
                e.printStackTrace();
            }
            TextView app_version = (TextView) rootView.findViewById(R.id.version_name);
            app_version.setText(getActivity().getResources().getString(R.string.app_name) + " " + pInfo.versionName);

            TextView device_name = (TextView) rootView.findViewById(R.id.device_name);
            device_name.setText(Build.MODEL + " (" + Build.DEVICE + ") @ Android " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")");

            TextView root_availability = (TextView) rootView.findViewById(R.id.root_availability);
            if (SU_AVAILABLE) {
                root_availability.setText(R.string.root_available);
                root_availability.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            } else {
                root_availability.setText(R.string.root_unavailable);
                root_availability.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }


            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize()) / 1048576;
            long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;
            long Used = Total - Free;

            storage(Total, R.id.storage, R.string.storage_total);

            ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.storage_bar);
            pb.setMax((int) Total);
            pb.setProgress((int) Used);

            storage(Used, R.id.used_storage, R.string.storage_used);
            storage(Free, R.id.free_storage, R.string.storage_free);

        }
    }
}

