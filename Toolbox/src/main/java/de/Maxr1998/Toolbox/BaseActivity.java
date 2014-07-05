package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import de.Maxr1998.Toolbox.adapter.NavDrawerListAdapter;
import de.Maxr1998.Toolbox.model.NavDrawerItem;


public class BaseActivity extends Activity {

    public static File manifestFile;
    private static Activity ACTIVITY;
    public int updateInterval;
    SharedPreferences prefs;
    URL manifestUrl;
    TypedArray navMenuIcons;
    ArrayList<NavDrawerItem> navDrawerItems;
    NavDrawerListAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;

    public static void downloadManifestHelper(Activity activity, URL manifestUrl, File manifestFile, String... args) {
        new fileDownloader(activity, manifestUrl, manifestFile, false).execute(args);
    }

    public static Boolean isNetAvailable(Context con) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo =
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void downloadManifest() {

        // Check for already existing manifest
        if (manifestFile.exists()) {
            // Auto-updating allowed?
            if (updateInterval != 0) {
                long age = (new Date().getTime() - manifestFile.lastModified()) / (3600 * 1000);
                if (age < updateInterval) {
                    Utils.showToast(ACTIVITY, R.string.manifest_local_copy);
                } else {
                    if (BaseActivity.isNetAvailable(this)) {
                        manifestFile.delete();
                        downloadManifestHelper(ACTIVITY, manifestUrl, manifestFile);
                        String manifest_updated = getString(R.string.manifest_updated, updateInterval);
                        Toast.makeText(ACTIVITY, manifest_updated, Toast.LENGTH_LONG).show();
                    } else {
                        String manifest_too_old_nn = getString(R.string.manifest_too_old_nn, updateInterval);
                        Toast.makeText(ACTIVITY, manifest_too_old_nn, Toast.LENGTH_LONG).show();
                    }
                }
            } else Utils.showToast(ACTIVITY, R.string.auto_update_disabled);
        } else {
            if (BaseActivity.isNetAvailable(this)) {
                manifestFile.delete();
                downloadManifestHelper(ACTIVITY, manifestUrl, manifestFile);
            } else new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_network_manifest)
                    .setIcon(R.drawable.ic_action_error)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            downloadManifest();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ACTIVITY.finish();
                        }
                    })
                    .show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Variables
        ACTIVITY = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateInterval = Integer.parseInt(prefs.getString("update_interval", "48"));
        File dataDir = new File(Utils.getDataDir(ACTIVITY));
        manifestFile = new File(dataDir + File.separator + ACTIVITY.getString(R.string.manifest_filename));
        try {
            manifestUrl = new URL(ACTIVITY.getString(R.string.manifest_link));
        } catch (MalformedURLException e) {
            Log.d("Error", e.toString());
            e.printStackTrace();
        }
        // Actions
        downloadManifest();


        // Navigation Drawer
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Info
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Clean
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Internal Storage Backup
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Post Flash
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (prefs.getBoolean("dev_unlocked", false)) {
            getMenuInflater().inflate(R.menu.global, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.menu_dev:
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle(R.string.menu_dev_section)
                        .setMessage(R.string.dev_text)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent dev_section = new Intent(ACTIVITY, DeveloperSectionActivity.class);
                                startActivity(dev_section);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                AlertDialog alert = b.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if (prefs.getBoolean("dev_unlocked", false)) {
            menu.findItem(R.id.menu_dev).setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new InfoFragment();
                break;
            case 1:
                fragment = new CleanFragment();
                break;
            case 2:
                fragment = new BackupFragment();
                break;
            case 4:
                fragment = new SettingsFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("Fragment Manager", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
}
