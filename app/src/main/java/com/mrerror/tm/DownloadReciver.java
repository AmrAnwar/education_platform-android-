package com.mrerror.tm;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.mrerror.tm.dataBases.Contract;
import com.mrerror.tm.dataBases.ModelAnswerDbHelper;
import com.mrerror.tm.fcm.ServiceForDownLoad;
import com.mrerror.tm.fragments.ModelAnswerFragment;
import com.mrerror.tm.models.ModelAnswer;

/**
 * Created by kareem on 8/2/2017.
 */

public class DownloadReciver extends BroadcastReceiver {
    ModelAnswerDbHelper dbHelper;
    Context mContext;
    static ModelAnswer mModelAnswer;
    static long reference;
    static DownloadManager downloadManager;
    static  DownloadReciver mDownloadReciver;

    public static void setReciver(ModelAnswer modelAnswer,long ref,DownloadManager down){
        mModelAnswer=modelAnswer;
        reference=ref;
        downloadManager=down;
    }
    public static Boolean isNotNull(){
        if(mModelAnswer==null&&reference==0&&downloadManager==null){
            return false;

        }else {return true;}
    }

   private DownloadReciver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper= new ModelAnswerDbHelper(context);
        mContext=context;
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
                    System.out.println(filelocation);
                    String extision= getMimeType(filelocation);
                    mModelAnswer.setFilePath(uriString);
                    mModelAnswer.setFileLocal(filelocation);
                    mModelAnswer.setFileExtention(extision);

                    saveToDataBase(uriString,mModelAnswer);
                    Intent mIntentService=new Intent(mContext, ServiceForDownLoad.class);
                    context.stopService(mIntentService);

                  Intent mIntentActivity = new Intent (mContext,ReadPDFactivity.class);
                    mIntentActivity.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                    mIntentActivity.putExtra("obj",mModelAnswer);
                    ModelAnswerFragment.shouldResume=true;
                    context.startActivity(mIntentActivity);

                }
            }

        }
        dbHelper.close(); }

    public  void saveToDataBase(String uri,ModelAnswer modelAnswer){

//        Toast.makeText(this, uri+ " "+ modelAnswer.getId(), Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(Contract.TableForModelAnswer._ID, modelAnswer.getId());
        values.put(Contract.TableForModelAnswer.COLUMN_FILE_PATH,uri.toString());
        values.put(Contract.TableForModelAnswer.COLUMN_EXTENSION,modelAnswer.getFileExtention());
        values.put(Contract.TableForModelAnswer.COLUMN_NOTE,modelAnswer.getNote());
        values.put(Contract.TableForModelAnswer.COLUMN_TITLE,modelAnswer.getTitle());
        values.put(Contract.TableForModelAnswer.COLUMN_TYPE,modelAnswer.getType());
        values.put(Contract.TableForModelAnswer.COLUMN_FILE_LOCATION,modelAnswer.getFileLocal());


        long check=  db.insert(Contract.TableForModelAnswer.TABLE_NAME,null,values);
//        db.delete(Contract.TableForModelAnswer.TABLE_NAME,null,null);

        if(check>=0)
            Toast.makeText(mContext, "Done add to your device ", Toast.LENGTH_SHORT).show();
        else
           Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

    }
    public  String getMimeType(String url) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        return extension;
    }
    public static DownloadReciver getInctance(){
        if(mDownloadReciver==null){
            mDownloadReciver=new DownloadReciver();
            return mDownloadReciver;
        }
        else {return mDownloadReciver;}
    }

    }

