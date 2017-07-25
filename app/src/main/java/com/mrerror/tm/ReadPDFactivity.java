package com.mrerror.tm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

public class ReadPDFactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdfactivity);

        Intent m=getIntent();
       String filePath= m.getStringExtra("file_path");
        Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
       PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromUri(Uri.parse(filePath)).load();


    }
}
