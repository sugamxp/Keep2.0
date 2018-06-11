package com.example.sugam.keep20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "keep.db";
    private static final int DB_VERSION = 1;

    public NoteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + NoteContract.NoteEntry.TABLE_NAME + " ( " +
                    NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NoteContract.NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                    NoteContract.NoteEntry.COLUMN_CONTENT + " TEXT NOT NULL, "+
                    NoteContract.NoteEntry.COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "+
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
