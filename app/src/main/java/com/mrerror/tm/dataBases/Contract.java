package com.mrerror.tm.dataBases;

/**
 * Created by kareem on 7/25/2017.
 */

public class Contract {
    private Contract() {
    }

    public static class TableForModelAnswer {
        public static final String TABLE_NAME = "modelAnswer";
        public static final String _ID = "id";
        public static final String COLUMN_FILE_PATH = "path";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_EXTENSION = "extension";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FILE_LOCATION = "location";
    }
    public static class TableForWrodsBank {
        public static final String TABLE_NAME = "wordsBank";
        public static final String _ID = "id";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANSLATE = "translate";
        public static final String COLUMN_STATE = "state";

    }


}
