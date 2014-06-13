package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;

public class CleanFragment extends Fragment {

    private static Activity activity;
    public int tasks_nr;
    ArrayList<String> tasks;

    public static void clean(Activity activity, Integer tasks, ArrayList<String>... args) {
        if (!BaseActivity.manifestFile.exists()) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_manifest)
                    .setIcon(R.drawable.ic_action_error)
                    .create().show();
            return;
        }
        new fileCleaner(activity, tasks, args[0]).execute();
    }

    public void onCreate(Bundle savedInstanceState) {
        // Layout
        super.onCreate(savedInstanceState);
        // Settings
        tasks = new ArrayList<String>();
        tasks_nr = 0;
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clean, container, false);

        View[] cbv = new View[]{
                rootView.findViewById(R.id.clear_ads),
                rootView.findViewById(R.id.clear_thumbs),
                rootView.findViewById(R.id.clear_logs),
                rootView.findViewById(R.id.clear_temporary),
                rootView.findViewById(R.id.format_cache),
                rootView.findViewById(R.id.clear_dalvik)
        };
        int length = 6;
        CheckBox[] cb = new CheckBox[length];

        for (int i = 0; i < length; i++) {
            cb[i] = (CheckBox) cbv[i];
            cb[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((CheckBox) view).isChecked();

                    switch (view.getId()) {
                        case R.id.clear_ads:
                            if (checked) {
                                tasks_nr++;
                                tasks.add("ads");
                            } else {
                                tasks_nr--;
                                tasks.remove("ads");
                            }
                            break;
                        case R.id.clear_thumbs:
                            if (checked) {
                                tasks_nr++;
                                tasks.add("thumbs");
                            } else {
                                tasks_nr--;
                                tasks.remove("thumbs");
                            }
                            break;
                        case R.id.clear_logs:
                            if (checked) {
                                tasks_nr++;
                                tasks.add("logs");
                            } else {
                                tasks_nr--;
                                tasks.remove("logs");
                            }
                            break;
                        case R.id.clear_temporary:
                            if (checked) {
                                tasks_nr++;
                                tasks.add("tmp");
                            } else {
                                tasks_nr--;
                                tasks.remove("tmp");
                            }
                            break;
                        case R.id.format_cache:
                            if (checked) {
                                tasks_nr++;
                                tasks.add("cache");
                            } else {
                                tasks_nr--;
                                tasks.remove("cache");
                            }
                            break;
                        case R.id.clear_dalvik:
                            if (checked) {
                                final CheckBox cd = (CheckBox) view.findViewById(R.id.clear_dalvik);
                                cd.setChecked(false);
                                new AlertDialog.Builder(activity)
                                        .setTitle(R.string.warning)
                                        .setIcon(R.drawable.ic_action_warning)
                                        .setMessage(R.string.dalvik_warning)
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                tasks_nr++;
                                                tasks.add("dalvik");
                                                cd.setChecked(true);
                                            }
                                        })
                                        .create().show();
                            } else {
                                tasks_nr--;
                                tasks.remove("dalvik");
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        final Button clear = (Button) rootView.findViewById(R.id.clear_selected_button);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
        return rootView;
    }

    public void clear() {

        for (String item : tasks) {
            Log.d("Task Name", item);
        }
        String test = String.valueOf(tasks_nr);
        Log.d("Number of all tasks", test);

        clean(activity, tasks_nr, tasks);
    }
}

