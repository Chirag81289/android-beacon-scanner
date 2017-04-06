package com.bridou_n.beaconscanner.features.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bridou_n.beaconscanner.AppSingleton;
import com.bridou_n.beaconscanner.BuildConfig;
import com.bridou_n.beaconscanner.R;
import com.bridou_n.beaconscanner.utils.PreferencesHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @Inject PreferencesHelper prefs;
    @Inject FirebaseAnalytics tracker;

    @BindView(R.id.content) ScrollView content;
    @BindView(R.id.scan_open) SwitchCompat scanOpen;
    @BindView(R.id.version) TextView version;

    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        AppSingleton.activityComponent().inject(this);

        if ((ab = getSupportActionBar()) != null) {
            ab.setTitle(getString(R.string.settings));
            ab.setDisplayHomeAsUpEnabled(true);
        }

        scanOpen.setChecked(prefs.isScanOnOpen());
        version.setText(BuildConfig.VERSION_NAME);
    }

    @OnCheckedChanged(R.id.scan_open)
    public void onScanOpenChanged(boolean status) {
        Bundle b = new Bundle();

        b.putBoolean("status", status);
        tracker.logEvent("scan_open_changed", b);
        prefs.setScanOnOpen(status);
    }

    @OnClick(R.id.rate)
    public void onRateClicked() {
        tracker.logEvent("rate_clicked", null);
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @OnClick(R.id.feature_request)
    public void onFeatureRequestClicked() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", BuildConfig.CONTACT_EMAIL, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feature request for BeaconScanner");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    @OnClick(R.id.tutorial)
    public void onTutorialClicked() {
        tracker.logEvent("tutorial_reset_clicked", null);
        prefs.setHasSeenTutorial(false);
        Snackbar.make(content, getString(R.string.the_tutorial_has_been_reset), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
