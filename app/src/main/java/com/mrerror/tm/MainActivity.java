package com.mrerror.tm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mrerror.tm.fragments.GeneralNews;
import com.mrerror.tm.fragments.GeneralWords;
import com.mrerror.tm.fragments.PartsFragment;
import com.mrerror.tm.fragments.UnitFragment;
import com.mrerror.tm.fragments.WordsFragment;
import com.mrerror.tm.models.Unit;
import com.mrerror.tm.models.Word;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements UnitFragment.OnListFragmentInteractionListener ,
PartsFragment.OnListFragmentInteractionListener{

    private TextView mTextMessage;

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
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AskActivity.class));
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.content,new GeneralNews()).commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
}
