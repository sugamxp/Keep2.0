package com.example.sugam.keep20;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class EditNote extends AppCompatActivity {
    private static final String TAG = "EditNote";
    private EditText et_title;
    private EditText et_content;
    private String title;
    private String content;
    private NoteHelper noteHelper;
    private SQLiteDatabase dB;
    private int i;
    private long id;
    private String rTitle;
    private String rContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        et_title = findViewById(R.id.title);
        et_content = findViewById(R.id.content);
        noteHelper = new NoteHelper(this);
        dB = noteHelper.getWritableDatabase();

        Intent recieved_intent = getIntent();
        i = recieved_intent.getIntExtra("flag", -1);
        id = recieved_intent.getLongExtra("id", -1);
        if (i == 1) {
            rTitle = recieved_intent.getStringExtra("title");
            rContent = recieved_intent.getStringExtra("content");
            et_title.setText(rTitle);
            et_content.setText(rContent);
        }

    }

    @Override
    public void onBackPressed() {
        if (i == 0) {
            if (et_title.getText().toString().length() != 0 && et_content.getText().toString().length() != 0) {
                super.onBackPressed();
                addNote();
            } else {
                super.onBackPressed();
                CustomIntent.customType(this,"fadein-to-fadeout");
            }

        } else {
            super.onBackPressed();
            title = et_title.getText().toString();
            content = et_content.getText().toString();
            ContentValues cv = new ContentValues();
            cv.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
            cv.put(NoteContract.NoteEntry.COLUMN_CONTENT, content);
            dB.update(NoteContract.NoteEntry.TABLE_NAME, cv, NoteContract.NoteEntry._ID + " = " + id, null);
            Toasty.warning(this, "Note Updated", Toast.LENGTH_LONG, false).show();
            CustomIntent.customType(this,"fadein-to-fadeout");

        }
    }

    private long addNote() {
        title = et_title.getText().toString();
        content = et_content.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
        cv.put(NoteContract.NoteEntry.COLUMN_CONTENT, content);
        Toasty.success(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT, false).show();
        CustomIntent.customType(this,"fadein-to-fadeout");
        return dB.insert(NoteContract.NoteEntry.TABLE_NAME, null, cv);

    }
}
