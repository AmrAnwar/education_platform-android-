package com.mrerror.tm;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mrerror.tm.connection.NetworkConnection;
import com.mrerror.tm.fragments.GeneralNews;
import com.mrerror.tm.fragments.GeneralWords;
import com.mrerror.tm.fragments.ModelAnswerFragment;
import com.mrerror.tm.fragments.PartsFragment;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.models.ModelAnswer;
import com.mrerror.tm.models.Part;
import com.mrerror.tm.models.Unit;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mrerror.tm.ReadPDFactivity.checkid;
import static com.mrerror.tm.connection.NetworkConnection.url;

public class MainActivity extends AppCompatActivity implements UnitFragment.OnListFragmentInteractionListener,
        PartsFragment.OnListFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ModelAnswerFragment.OnItemClick, NetworkConnection.OnCompleteFetchingData {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ProgressBar mProgressBar;
    TextView blankText;
    BottomNavigationView navigation;
    private static final int REQUEST_EXTERNAL_STORAGE = 12;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                        getSupportFragmentManager().popBackStack();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, new GeneralNews()).commit();
                    return true;
                case R.id.navigation_words:
                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                        getSupportFragmentManager().popBackStack();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, UnitFragment.newInstance("http://educationplatform.pythonanywhere.com/api/study/units/v2/")).commit();
                    return true;
//                case R.id.inbox:
//                    mTextMessage.setText(R.string.title_inbox);
//                    return true;
                case R.id.model_answer:
                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                        getSupportFragmentManager().popBackStack();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    loadModelAnswerFragment();
                    return true;
            }
            return false;
        }

    };
    private String count;
    private TextView actionView;

    public void loadModelAnswerFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ModelAnswerFragment()).commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        blankText = (TextView) findViewById(R.id.no_list_net);
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AskActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionView = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_inbox));

        getCount(sp.getString("group", "normal"));
        actionView.setTextColor(getResources().getColor(R.color.colorPrimary));
        actionView.setGravity(Gravity.CENTER);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new GeneralNews()).commit();

        }
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


//SQ

//

        //Notifigation Handel
        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {

            }

            @Override
            public void onError(String error) {

            }
        }).patchData(this, "http://educationplatform.pythonanywhere.com/api/users/"
                        + sp.getString("username", "") + "/profile",
                        new String[]{"token"}, new String[]{sp.getString("token", "")});

        String group = sp.getString("group", "normal");
        if (group.equals("normal")) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("new_question");

        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("new_question");

        }

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseMessaging.getInstance().subscribeToTopic("answers");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("where").equals("answers")) {
                loadModelAnswerFragment();
            } else if (extras.getString("where").equals("news")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new GeneralNews()).commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCount(sp.getString("group", "normal"));
    }

    private void getCount(String group) {

        if (group.equals("normal")) {

            actionView.setText(String.valueOf(sp.getInt("answersCount", 0) + 1));

        } else {
            url = "http://educationplatform.pythonanywhere.com/api/asks/";

            new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
                @Override
                public void onCompleted(String result) throws JSONException {

                    JSONObject jsonObject = new JSONObject(result);
                    count = String.valueOf(jsonObject.getInt("count"));
                    actionView.setText(count);
                }

                @Override
                public void onError(String error) {

                }
            }).getDataAsJsonObject(this);
        }


    }


    // For unit list
    @Override

    public void onListFragmentInteraction(Unit item) {
        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.content, PartsFragment.newInstance(item.getParts()))
                .addToBackStack(null).commit();
    }

    // For parts list
    @Override
    public void onListFragmentInteraction(Part item) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, GeneralWords.newInstance(item))
                .addToBackStack(null).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_inbox) {
            startActivity(new Intent(this, Inbox.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));

        } else if (id == R.id.nav_logout) {
            url = "http://educationplatform.pythonanywhere.com/api/users/" + sp.getInt("id", 0) + "/logout/";
            new NetworkConnection(this).getDataAsJsonObject(this);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClickLestiner(ModelAnswer item) {


        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            Toast.makeText(this, "YOU MUST ALLOW AND PRESS AGAIN", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {


            if (!checkid.keySet().contains(item.getId())) {
                Intent i = new Intent(MainActivity.this, ReadPDFactivity.class);
                i.putExtra("obj", item);
                startActivity(i);
            } else {
                Toast.makeText(this, "please..wait ", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onCompleted(String result) throws JSONException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("new_question");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("answers");

        new NetworkConnection(new NetworkConnection.OnCompleteFetchingData() {
            @Override
            public void onCompleted(String result) throws JSONException {

            }

            @Override
            public void onError(String error) {

            }
        }).patchData(this, "http://educationplatform.pythonanywhere.com/api/users/"
                        + sp.getString("username", "") + "/profile",
                new String[]{"token"}, new String[]{"له"});


        editor.putBoolean("logged2", false);
        editor.commit();
        startActivity(new Intent(this, LoginActivity2.class));
        this.finish();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "You cannot do this !", Toast.LENGTH_SHORT).show();
    }
}
