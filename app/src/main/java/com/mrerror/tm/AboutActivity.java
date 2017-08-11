package com.mrerror.tm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView urlTV = (TextView) findViewById(R.id.team);
        ImageView teamIV = (ImageView) findViewById(R.id.team_pic);
        urlTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTeamLink();
            }
        });
        teamIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTeamLink();
            }
        });
    }

    private void openTeamLink(){
        String OurUrl = "http://amranwar00.pythonanywhere.com/posts/phontam-team-for-software-solution/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(OurUrl));
        startActivity(i);
    }

}
