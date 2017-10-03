package com.mrerror.tm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrerror.tm.models.Question;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class ReplyActivity extends AppCompatActivity {

    ImageView stfImg;
    ImageButton playBtn;
    private Question question1;
    private MediaPlayer mediaPlayer2;
    private boolean playing = false;
    ImageView replyImg;
    LinearLayout ImgLayout;
    ProgressBar loadingImg;
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
        ImgLayout = (LinearLayout) findViewById(R.id.image_layout);
        loadingImg = (ProgressBar) findViewById(R.id.loading_img);

        setUpdPlay();
        if(question1.getLinkForImg().length()>4){
            ImgLayout.setVisibility(View.VISIBLE);
        }
        Picasso.with(this).load(question1.getLinkForImg()).into(replyImg, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                loadingImg.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });

        replyImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try
                {
                    Toast.makeText(ReplyActivity.this, "downloading ...", Toast.LENGTH_SHORT).show();
                    Log.e("long click","clicked");
                    Bitmap bmp = null;
                    URL url = new URL(question1.getLinkForImg());
                    URLConnection conn = url.openConnection();
                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    File f = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() + ".jpg");
                    if(f.exists())
                        f.delete();
                    f.createNewFile();
                    Bitmap bitmap = bmp;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    Toast.makeText(ReplyActivity.this, "downloaded!", Toast.LENGTH_SHORT).show();
                    Log.e("long click","downloaded");

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void setUpdPlay() {

        playBtn = (ImageButton) findViewById(R.id.play_btn);
        if (question1.getLinkForRec().length() > 5) {
            playBtn.setVisibility(View.VISIBLE);
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
                mediaPlayer2 = new MediaPlayer();
                mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playBtn.setImageResource(R.drawable.ic_play);
                        playing = false;
                    }
                });
                mediaPlayer2.setDataSource(getString(R.string.domain)+question1.getLinkForRec());
                mediaPlayer2.prepare();
                mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        mediaPlayer2.start();

                        playBtn.setImageResource(R.drawable.ic_stop);
                        playing = true;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            playBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer2.stop();
            playing = false;
        }
    }

    @Override
    protected void onPause() {
        try{
            mediaPlayer2.stop();
        }catch (Exception e){

        }
        try{
        }catch (Exception e){

        }
        super.onPause();
    }
}
