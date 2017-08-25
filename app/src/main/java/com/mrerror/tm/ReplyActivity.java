package com.mrerror.tm;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrerror.tm.models.Question;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ReplyActivity extends AppCompatActivity {

    ImageView stfImg;
    ImageButton playBtn;
    private Question question1;
    private MediaPlayer mediaPlayer2;
    private boolean playing = false;
    ImageView replyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView question = (TextView) findViewById(R.id.question);
        TextView answer = (TextView) findViewById(R.id.answer);
        question1 = getIntent().getParcelableExtra("question");
        question.setText(question1.getQuestion());
        answer.setText(question1.getAnswer());
        replyImg = (ImageView) findViewById(R.id.replyImg);

        setUpdPlay();
        Picasso.with(this).load(question1.getLinkForImg()).into(replyImg);

    }

    private void setUpdPlay() {

        playBtn = (ImageButton) findViewById(R.id.play_btn);
        if (question1.getLinkForRec().length() > 5) {
            playBtn.setVisibility(View.VISIBLE);
            mediaPlayer2 = new MediaPlayer();
            try {
                mediaPlayer2.setDataSource(question1.getLinkForRec());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setImageResource(R.drawable.ic_play);
                    playing = false;
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void playRec(View view) {
        if (!playing) {
            try {
                mediaPlayer2.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer2.start();
            playBtn.setImageResource(R.drawable.ic_stop);
            playing = true;
        } else {
            playBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer2.stop();
            playing = false;
        }
    }
}
