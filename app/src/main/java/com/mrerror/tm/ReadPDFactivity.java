package com.mrerror.tm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ReadPDFactivity extends AppCompatActivity {
    ImageView imageView;
    PDFView pdfView;
    PhotoViewAttacher ph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdfactivity);


        imageView= (ImageView) findViewById(R.id.ImageViewWithzoom);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        Intent m=getIntent();
       String filePath= m.getStringExtra("file_path");
        String ext=m.getStringExtra("ext");
        if(ext.equals("pdf")) {
             imageView.setVisibility(View.INVISIBLE);
            pdfView.setVisibility(View.VISIBLE);
            pdfView.fromUri(Uri.parse(filePath)).load();
        }else {
            pdfView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(filePath));

             ph = new PhotoViewAttacher(imageView);

        }

    }
}
