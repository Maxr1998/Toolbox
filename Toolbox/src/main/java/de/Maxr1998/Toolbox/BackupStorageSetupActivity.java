package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

public class BackupStorageSetupActivity extends Activity {

    public static int REQUEST_INTERNAL_STORAGE_LOC = 7;
    public static int REQUEST_EXTERNAL_STORAGE_LOC = 42;
    static Activity ACTIVITY;
    SharedPreferences prefs;
    EditText internalStorageLocET;
    EditText externalStorageLocET;
    String internalStorageLoc;
    String externalStorageLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdsetup);

        ACTIVITY = this;

        internalStorageLocET = (EditText) findViewById(R.id.internal_storage_loc);
        externalStorageLocET = (EditText) findViewById(R.id.external_storage_loc);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        internalStorageLoc = prefs.getString("internal_storage_loc", null);
        externalStorageLoc = prefs.getString("external_storage_loc", null);

        if (internalStorageLoc != null) internalStorageLocET.setText(internalStorageLoc);
        if (externalStorageLoc != null) externalStorageLocET.setText(externalStorageLoc);

        internalStorageLocET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent chooserIntent = new Intent(ACTIVITY, DirectoryChooserActivity.class);
                startActivityForResult(chooserIntent, REQUEST_INTERNAL_STORAGE_LOC);
            }
        });

        externalStorageLocET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent chooserIntent = new Intent(ACTIVITY, DirectoryChooserActivity.class);
                startActivityForResult(chooserIntent, REQUEST_EXTERNAL_STORAGE_LOC);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INTERNAL_STORAGE_LOC) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                internalStorageLoc = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                internalStorageLocET.setText(internalStorageLoc);
            }
        } else if (requestCode == REQUEST_EXTERNAL_STORAGE_LOC) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                externalStorageLoc = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                externalStorageLocET.setText(externalStorageLoc);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_apply) {
            if (internalStorageLoc != null)
                prefs.edit().putString("internal_storage_loc", internalStorageLoc).commit();
            if (externalStorageLoc != null)
                prefs.edit().putString("external_storage_loc", externalStorageLoc).commit();
            ACTIVITY.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sdsetup, menu);
        return true;
    }
}
