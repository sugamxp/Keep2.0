package com.example.sugam.keep20;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends AppCompatActivity {
    EditText et_title;
    EditText et_content;
    String title;
    String content;
    NoteHelper noteHelper;
    SQLiteDatabase dB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        et_title = findViewById(R.id.title);
        et_content = findViewById(R.id.content);
        noteHelper = new NoteHelper(this);
        dB = noteHelper.getWritableDatabase();
    }

    @Override
    public void onBackPressed() {
        if (et_title.getText().toString().length() != 0 && et_content.getText().toString().length() != 0) {
            super.onBackPressed();
            addNote();
            Toast.makeText(getApplicationContext(), "" +
                    "Note Saved", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    private long addNote() {
        title = et_title.getText().toString();
        content = et_content.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
        cv.put(NoteContract.NoteEntry.COLUMN_CONTENT, content);
        return dB.insert(NoteContract.NoteEntry.TABLE_NAME, null, cv);
    }
}
