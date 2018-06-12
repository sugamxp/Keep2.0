package com.example.sugam.keep20;


import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private FloatingActionButton fab;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    SQLiteDatabase dB;
    Cursor cursor;
    NoteHelper noteHelper;
    ImageView imageView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapse_function();

        imageView = findViewById(R.id.expandedImage);
        noteHelper = new NoteHelper(this);
        dB = noteHelper.getReadableDatabase();
        cursor = getNotes();
        Log.d(TAG, "onCreate: Number of notes = " + cursor.getCount());
        recyclerView = findViewById(R.id.recycler_view);
        noteAdapter = new NoteAdapter(this, cursor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
        Log.d(TAG, "onCreate: Here");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Glide.with(this).load("https://source.unsplash.com/1600x900/?black").into(imageView);
            Log.d(TAG, "onCreate: glide successfull");

        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditNote();
            }
        });


    }

    private void collapse_function() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                    showOption(R.id.tool_add);
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                    hideOption(R.id.tool_add);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tool_add) {
            openEditNote();
            return true;
        }

        return false;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    final void openEditNote() {
        Intent i = new Intent(this, EditNote.class);
        startActivity(i);
    }

    private Cursor getNotes() {
        Log.d(TAG, "getNotes: Here");
        return dB.query(NoteContract.NoteEntry.TABLE_NAME, null
                , null, null, null, null, NoteContract.NoteEntry.COLUMN_TIME);

    }

    @Override
    protected void onResume() {
        noteAdapter.swapCursor(getNotes());
        super.onResume();
    }


}

