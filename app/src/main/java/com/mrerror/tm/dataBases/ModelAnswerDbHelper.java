package com.mrerror.tm.dataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by kareem on 7/25/2017.
 */

public class ModelAnswerDbHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ModelAnswer.db";

    public ModelAnswerDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Contract.TableForModelAnswer.TABLE_NAME + " (" +
                        Contract.TableForModelAnswer._ID + " INTEGER PRIMARY KEY," +
                        Contract.TableForModelAnswer.COLUMN_FILE_PATH + " TEXT,"+
                        Contract.TableForModelAnswer.COLUMN_EXTENSION +" TEXT,"+
                        Contract.TableForModelAnswer.COLUMN_NOTE+" TEXT,"+
                        Contract.TableForModelAnswer.COLUMN_TITLE+" TEXT,"+
                        Contract.TableForModelAnswer.COLUMN_TYPE+" TEXT)";
        db.execSQL(SQL_CREATE_ENTRIES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Contract.TableForModelAnswer.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
