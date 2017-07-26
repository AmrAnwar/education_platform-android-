package com.mrerror.tm;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.mrerror.tm.dataBases.Contract;
import com.mrerror.tm.dataBases.ModelAnswerDbHelper;
import com.mrerror.tm.fragments.GeneralNews;
import com.mrerror.tm.fragments.GeneralWords;
import com.mrerror.tm.fragments.ModelAnswerFragment;
import com.mrerror.tm.fragments.PartsFragment;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.fragments.WordsFragment;
import com.mrerror.tm.models.ModelAnswer;
import com.mrerror.tm.models.Unit;
import com.mrerror.tm.models.Word;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements UnitFragment.OnListFragmentInteractionListener ,
PartsFragment.OnListFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener ,ModelAnswerFragment.OnItemClick
    {

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    DownloadManager downloadManager;
    long reference;
    ModelAnswerDbHelper dpHelper;
    ModelAnswer mModelAnswer;
    ModelAnswerFragment modelAnswerFragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,new GeneralNews()).commit();
                    return true;
                case R.id.navigation_words:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,new GeneralWords()).commit();
                    return true;
//                case R.id.inbox:
//                    mTextMessage.setText(R.string.title_inbox);
//                    return true;
                case R.id.model_answer:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,modelAnswerFragment).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        modelAnswerFragment=new ModelAnswerFragment();

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

        //download section

        dpHelper=new ModelAnswerDbHelper(this);
        BroadcastReceiver receiver =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
                {
                    DownloadManager.Query req_query= new DownloadManager.Query();
                    req_query.setFilterById(reference);
                    Cursor c= downloadManager.query(req_query);

                    if(c.moveToFirst()){

                        int coulmIndex=c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL==c.getInt(coulmIndex)){
                            String uriString= c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            String filelocation=c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            String extision= getMimeType(filelocation);

                            mModelAnswer.setFileExtention(extision);

                            saveToDataBase(uriString,mModelAnswer);

                            getSupportFragmentManager().beginTransaction().replace(R.id.content,new ModelAnswerFragment()).commit();
                        }
                    }

                }
            }
        };
        registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//SQLiteDatabase db=dpHelper.getWritableDatabase();
//        db.delete(Contract.TableForModelAnswer.TABLE_NAME,null,null);

    }


    public  void downLoad(ModelAnswer modelAnswer ){

        downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri= Uri.parse(modelAnswer.getFileUrl());
        DownloadManager.Request request= new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
         reference= downloadManager.enqueue(request);


    }
    public  void saveToDataBase(String uri,ModelAnswer modelAnswer){

//        Toast.makeText(this, uri+ " "+ modelAnswer.getId(), Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = dpHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(Contract.TableForModelAnswer._ID, modelAnswer.getId());
        values.put(Contract.TableForModelAnswer.COLUMN_FILE_PATH,uri.toString());
        values.put(Contract.TableForModelAnswer.COLUMN_EXTENSION,modelAnswer.getFileExtention());
        values.put(Contract.TableForModelAnswer.COLUMN_NOTE,modelAnswer.getNote());
        values.put(Contract.TableForModelAnswer.COLUMN_TITLE,modelAnswer.getTitle());
        values.put(Contract.TableForModelAnswer.COLUMN_TYPE,modelAnswer.getType());


      long check=  db.insert(Contract.TableForModelAnswer.TABLE_NAME,null,values);
//        db.delete(Contract.TableForModelAnswer.TABLE_NAME,null,null);

        if(check>=0)
            Toast.makeText(this, "Done add to dataBase", Toast.LENGTH_SHORT).show();
               else
            Toast.makeText(this, "errorHappend", Toast.LENGTH_SHORT).show();

    }


    // For unit list
    @Override

    public void onListFragmentInteraction(Unit item) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content,PartsFragment.newInstance(item.getParts())).addToBackStack(null).commit();
    }

    // For parts list
    @Override
    public void onListFragmentInteraction(ArrayList<Word> item) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, WordsFragment.newInstance(item)).addToBackStack(null).commit();
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
        Toast.makeText(this, item.getNote(), Toast.LENGTH_SHORT).show();
        mModelAnswer=item;

     if(!mModelAnswer.getDwonload())
     {
         downLoad(item);}
     else {

         Intent i=new Intent(MainActivity.this,ReadPDFactivity.class);
         i.putExtra("file_path",mModelAnswer.getFilePath());
         i.putExtra("ext",mModelAnswer.getFileExtention());
         startActivity(i);
     }

    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        return extension;
    }
    protected void onDestroy() {
        dpHelper.close();
        super.onDestroy();
    }
}
