package com.byteshaft.wahwege;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.byteshaft.wahwege.FutureDemand.AddDemand;
import com.byteshaft.wahwege.FutureDemand.DemandList;
import com.byteshaft.wahwege.account.LoginActivity;
import com.byteshaft.wahwege.account.UpdateProfile;
import com.byteshaft.wahwege.contactdetails.AboutUs;
import com.byteshaft.wahwege.contactdetails.Faq;
import com.byteshaft.wahwege.contactdetails.RateUs;
import com.byteshaft.wahwege.shopnow.ShopNow;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity sInstance;

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        FirebaseMessaging.getInstance().subscribeToTopic("wahvege.promotion_created");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (AppGlobals.isLogin()) {
            if (getIntent().getBooleanExtra("promotion", false)) {
                loadFragment(new Notifications());
            } else {
                loadFragment(new ShopNow());
            }
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_future_demand) {
            loadFragment(new DemandList());
        } else if (id == R.id.nav_user_profile) {
            startActivity(new Intent(getApplicationContext(), UpdateProfile.class));

        } else if (id == R.id.nav_purchase_history) {
            loadFragment(new PurchaseHistory());

        } else if (id == R.id.nav_notifications) {
            loadFragment(new Notifications());

        } else if (id == R.id.nav_complains) {
            loadFragment(new SuggestionsComplains());

        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(getApplicationContext(), AboutUs.class));

        } else if (id == R.id.nav_faq) {
            startActivity(new Intent(getApplicationContext(), Faq.class));

        } else if (id == R.id.nav_share) {
            shareDialog();

        } else if (id == R.id.nav_rate_us) {

        } else if (id == R.id.nav_logout) {
            logOutDialog();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("Do you really want to logout?")
                .setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppGlobals.clearSettings();
                        AppGlobals.firstTimeLaunch(true);
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    private void shareDialog() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=byteshaft.com.tasbeeh&hl=en");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void loadFragmentWithBackStack(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container, fragment, backStateName);
        fragmentTransaction.addToBackStack(backStateName);
        fragmentTransaction.commit();
    }
}
