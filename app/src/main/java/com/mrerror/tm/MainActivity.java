package com.mrerror.tm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mrerror.tm.fragments.GeneralNews;
import com.mrerror.tm.fragments.GeneralWords;
import com.mrerror.tm.fragments.ModelAnswerFragment;
import com.mrerror.tm.fragments.PartsFragment;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.models.ModelAnswer;
import com.mrerror.tm.models.Part;
import com.mrerror.tm.models.Unit;

import static com.mrerror.tm.ReadPDFactivity.checkid;

public class MainActivity extends AppCompatActivity implements UnitFragment.OnListFragmentInteractionListener ,
PartsFragment.OnListFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener ,ModelAnswerFragment.OnItemClick
   {

       SharedPreferences sp;
       SharedPreferences.Editor editor;
       ProgressBar mProgressBar;
        TextView blankText;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,new GeneralNews()).commit();
                    return true;
                case R.id.navigation_words:
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,UnitFragment.newInstance("http://educationplatform.pythonanywhere.com/api/study/units/v2/")).commit();
                    return true;
//                case R.id.inbox:
//                    mTextMessage.setText(R.string.title_inbox);
//                    return true;
                case R.id.model_answer:
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                   loadModelAnswerFragment();
                    return true;
            }
            return false;
        }

    };
public  void loadModelAnswerFragment(){
    getSupportFragmentManager().beginTransaction().replace(R.id.content,new ModelAnswerFragment()).commit();

}





       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         mProgressBar= (ProgressBar) findViewById(R.id.progressbar);
         blankText=(TextView) findViewById(R.id.no_list_net);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AskActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content,new GeneralNews()).commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


//SQ
        mProgressBar.setVisibility(View.GONE);
//


        FirebaseMessaging.getInstance().subscribeToTopic("news");
           FirebaseMessaging.getInstance().subscribeToTopic("modelanswer");
        Bundle extras=getIntent().getExtras();
        if(extras !=null ){
            if(extras.getString("where").equals("modelanswer")){
                loadModelAnswerFragment();
            }
        }
    }




    // For unit list
    @Override

    public void onListFragmentInteraction(Unit item) {
        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.content,PartsFragment.newInstance(item.getParts()))
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
            startActivity(new Intent(this,Inbox.class));
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            editor.putBoolean("logged",false);
            editor.commit();
            startActivity(new Intent(this,LoginActivity.class));
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClickLestiner(ModelAnswer item) {

        if(!checkid.keySet().contains(item.getId())) {
            Intent i = new Intent(MainActivity.this, ReadPDFactivity.class);
            i.putExtra("obj", item);
            startActivity(i);
        }else {
            Toast.makeText(this, "please..wait ", Toast.LENGTH_SHORT).show();
        }
    }




}
