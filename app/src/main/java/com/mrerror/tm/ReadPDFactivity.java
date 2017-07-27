package com.mrerror.tm;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.mrerror.tm.dataBases.Contract;
import com.mrerror.tm.dataBases.ModelAnswerDbHelper;
import com.mrerror.tm.fragments.ModelAnswerFragment;

import java.io.File;
import java.net.URI;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ReadPDFactivity extends AppCompatActivity {
    ModelAnswerDbHelper dbHelper;

    ImageView imageView;
    PDFView pdfView;
    PhotoViewAttacher ph;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdfactivity);
        dbHelper= new ModelAnswerDbHelper(this);


        imageView= (ImageView) findViewById(R.id.ImageViewWithzoom);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        Intent m=getIntent();
       String filePath= m.getStringExtra("file_path");
        String ext=m.getStringExtra("ext");
        String location=m.getStringExtra("file_loc");
        File file=new File(URI.create(location).getPath());

        if(!file.exists()){

            deleteFromDataBase(filePath);
        }
        else {
            if (ext.equals("pdf")) {
                imageView.setVisibility(View.INVISIBLE);
                pdfView.setVisibility(View.VISIBLE);
                pdfView.fromUri(Uri.parse(filePath)).load();

            } else {
                pdfView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageURI(Uri.parse(filePath));
//                if (imageView.getDrawable() != null) {
//
//                    ph = new PhotoViewAttacher(imageView);
//                } else {
//                    deleteFromDataBase(filePath);
//                }
            }

        }
        dbHelper.close();
    }
    private void deleteFromDataBase(String filepath){
      db = dbHelper.getWritableDatabase();
        Toast.makeText(this, "file not exist any more download it again ", Toast.LENGTH_SHORT).show();
        String selection = Contract.TableForModelAnswer.COLUMN_FILE_PATH + " LIKE ?";
        String[] selectionArgs = { filepath };
        db.delete(Contract.TableForModelAnswer.TABLE_NAME, selection, selectionArgs);

        db.close();
        dbHelper.close();
        ModelAnswerFragment.shouldResume=true;
        finish();
    }
}
