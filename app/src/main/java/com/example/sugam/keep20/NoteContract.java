package com.example.sugam.keep20;

import android.provider.BaseColumns;

public class NoteContract {
    public static final class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_TIME = "time";
    }
}
