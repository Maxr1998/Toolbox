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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Arrays;
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
            throw new RuntimeException(getActivity().getClass().getSimpleName() + " must implement DemandReturnBackListener to use this fragment!", e);
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

        if (profilesInDialog != null) profilesInDialog.clear();
        Set<String> prefProfiles = pref.getStringSet(Common.PREF_PROFILES, null);
        if (prefProfiles == null) {
            prefProfiles = ImmutableSet.of(getResources().getString(R.string.dialog_item_no_profiles_available));
        }

        profilesInDialog = new LinkedList<String>(prefProfiles);
        ListAdapter profilesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, profilesInDialog);
        AlertDialog.Builder profilesDialogBuilder = new AlertDialog.Builder(getActivity());
        profilesDialogBuilder.setTitle(R.string.dialog_title_choose_profile)
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
                        showAddOrEditProfileDialog(9001);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        demandReturnBackListener.onDemandReturnBack();
                    }
                })
                .setCancelable(false);
        AlertDialog profilesDialog = profilesDialogBuilder.create();
        ListView listView = profilesDialog.getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showActionDialog(i);
                return false;
            }
        });
        profilesDialog.show();

    }

    private void showActionDialog(final int clickedProfile) {
        final List<String> actionsList = Arrays.asList("delete", "edit");
        ListAdapter actionsAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, actionsList);
        AlertDialog.Builder action = new AlertDialog.Builder(getActivity());
        action
                .setAdapter(actionsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int action) {
                        if (actionsList.get(action).equals("delete")) {
                            Utils.deleteProfile(getActivity(), profilesInDialog.get(clickedProfile));
                            showProfilesDialog();
                        } else if (actionsList.get(action).equals("edit"))
                            showAddOrEditProfileDialog(clickedProfile);
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void showAddOrEditProfileDialog(int profileToEdit) {
        boolean loadData = false;
        final String editedProfile;
        if (profileToEdit > 9000)
            editedProfile = Common.NOT_AVAILABLE;
        else {
            editedProfile = profilesInDialog.get(profileToEdit);
            loadData = true;
        }
        LayoutInflater li = LayoutInflater.from(getActivity());
        final View dialogView = li.inflate(R.layout.dialog_backup_setup, null);

        final EditText profile_name = (EditText) dialogView.findViewById(R.id.profile_name_input);
        final EditText source = (EditText) dialogView.findViewById(R.id.source_input);
        final EditText server = (EditText) dialogView.findViewById(R.id.server_name_input);
        final EditText port = (EditText) dialogView.findViewById(R.id.server_name_port_input);
        final EditText user = (EditText) dialogView.findViewById(R.id.user_name_input);
        final EditText destination = (EditText) dialogView.findViewById(R.id.destination_input);
        final EditText arguments = (EditText) dialogView.findViewById(R.id.extra_arguments_input);

        if (loadData) {
            String[] values = Utils.getValuesForProfile(getActivity(), editedProfile);
            profile_name.setText(editedProfile);
            source.setText(values[0]);
            server.setText(values[1]);
            port.setText(values[2]);
            user.setText(values[3]);
            destination.setText(values[4]);
            arguments.setText(values[5]);
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.changeProfile(getActivity(), profile_name.getText().toString(), source.getText().toString(),
                                server.getText().toString(), port.getText().toString(),
                                user.getText().toString(), destination.getText().toString(),
                                arguments.getText().toString(), editedProfile);
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

            File rsync = new File(Utils.getDataDir(getActivity()) + File.separator + "files" + File.separator + getActivity().getResources().getString(R.string.rsync_filename));

            String cmd = rsync + " -avP " + arguments + " --ignore-errors --delete-after -e \"ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -y -p " + port + "\" " + source + " " + user + "@" + server + ":" + destination;
            publishProgress(cmd);
            for (String str : Shell.SH.run(cmd)) {
                publishProgress(str);
            }
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