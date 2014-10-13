package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.common.collect.ImmutableSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.chainfire.libsuperuser.Shell;

public class BackupFragment extends Fragment implements View.OnClickListener {

    public DemandReturnBackListener demandReturnBackListener;
    private SharedPreferences pref;
    private TextView terminal;
    private List<String> profilesInDialog;
    private String[] arguments_for_async;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            demandReturnBackListener = (DemandReturnBackListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(getActivity().getClass().getSimpleName() + "must implement DemandReturnBackListener to use this fragment!", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View
        View rootView = inflater.inflate(R.layout.fragment_backup, container, false);
        terminal = (TextView) rootView.findViewById(R.id.terminal_window);
        terminal.setMovementMethod(new ScrollingMovementMethod());
        Button start = (Button) rootView.findViewById(R.id.start_backup);
        start.setOnClickListener(this);

        // Misc
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        showProfilesDialog();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_backup:
                new BackupTask(terminal, arguments_for_async).execute();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void showProfilesDialog() {
        if (profilesInDialog != null)
            profilesInDialog.clear();

        Set<String> prefProfiles = pref.getStringSet(Common.PREF_PROFILES, null);
        if (prefProfiles == null) {
            prefProfiles = ImmutableSet.of(getResources().getString(R.string.dialog_item_no_profiles_available));
        }
        profilesInDialog = new LinkedList<String>(prefProfiles);

        // Dialogs
        ListAdapter profilesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, profilesInDialog);
        final AlertDialog.Builder profilesDialog = new AlertDialog.Builder(getActivity());
        profilesDialog.setTitle(R.string.dialog_title_choose_profile)
                .setAdapter(profilesAdapter, profilesInDialog.contains(getResources().getString(R.string.dialog_item_no_profiles_available)) ? null : new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedProfile = profilesInDialog.get(i);

                        arguments_for_async = Utils.getValuesForProfile(getActivity(), selectedProfile);
                    }
                })
                .setNeutralButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        profilesInDialog.clear();
                        // Add profile dialog
                        LayoutInflater li = LayoutInflater.from(getActivity());
                        // Views
                        final View dialogView = li.inflate(R.layout.dialog_backup_setup, null);

                        final EditText profile_name = (EditText) dialogView.findViewById(R.id.profile_name_input);

                        final EditText source = (EditText) dialogView.findViewById(R.id.source_input);
                        final EditText server = (EditText) dialogView.findViewById(R.id.server_name_input);
                        final EditText port = (EditText) dialogView.findViewById(R.id.server_name_port_input);
                        final EditText user = (EditText) dialogView.findViewById(R.id.user_name_input);
                        final EditText destination = (EditText) dialogView.findViewById(R.id.destination_input);
                        final EditText arguments = (EditText) dialogView.findViewById(R.id.extra_arguments_input);


                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setView(dialogView)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Utils.addNewProfile(getActivity(), profile_name.getText().toString(), source.getText().toString(),
                                                server.getText().toString(), port.getText().toString(),
                                                user.getText().toString(), destination.getText().toString(),
                                                arguments.getText().toString());

                                        showProfilesDialog();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        showProfilesDialog();
                                    }
                                })
                                .setCancelable(false)
                                .create().show();

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        demandReturnBackListener.onDemandReturnBack();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private class BackupTask extends AsyncTask<Void, String, Boolean> {

        private TextView display;
        private String source;
        private String server;
        private String port;
        private String user;
        private String destination;
        private String arguments;

        public BackupTask(TextView display, String[] arguments) {
            this.display = display;
            this.source = arguments[0];
            this.server = arguments[1];
            this.port = arguments[2];
            this.user = arguments[3];
            this.destination = arguments[4];
            this.arguments = arguments[5];

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            /*for (String str : */
            Shell.SH.run("rsync -a -P " + arguments + " --delete-after -e \"ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -y -p " + port + "\" " + source + " " + user + "@" + server + ":" + destination);/*) {
                publishProgress(str);
            }*/

            publishProgress("test");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            display.append("\n " + values[0]);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);
        }
    }
}