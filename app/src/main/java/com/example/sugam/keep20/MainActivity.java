package com.example.sugam.keep20;


import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Random;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private SQLiteDatabase dB;
    private Cursor cursor;
    private NoteHelper noteHelper;
    private ImageView imageView;
    private ArrayList<String> imageUrls;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapse_function();
        Random random = new Random();
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
        imageUrls = new ArrayList<>();
        addImages();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Glide.with(this).load(imageUrls.get(random.nextInt(9))).into(imageView);
            Log.d(TAG, "onCreate: glide successfull");
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditNote();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                long id = (long) viewHolder.itemView.getTag();
                dB.delete(NoteContract.NoteEntry.TABLE_NAME, NoteContract.NoteEntry._ID + " = " + id, null);
                noteAdapter.swapCursor(getNotes());
                Toasty.error(getApplicationContext(), "Note Deleted", Toast.LENGTH_LONG, false).show();
            }
        }).attachToRecyclerView(recyclerView);

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
        } else if (id == R.id.tool_signout) {

            FirebaseAuth.getInstance().signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent i = new Intent(getApplicationContext(), SignIn.class);
                            startActivity(i);
                            finish();
                        }
                    });
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
        i.putExtra("flag", 0);
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

    private void addImages() {
        imageUrls.add("https://source.unsplash.com/CuFYW1c97w8/800x600");
        imageUrls.add("https://source.unsplash.com/xrVDYZRGdw4/800x600");
        imageUrls.add("https://source.unsplash.com/VK284NKoAVU/800x600");
        imageUrls.add("https://source.unsplash.com/OqtafYT5kTw/800x600");
        imageUrls.add("https://source.unsplash.com/vnpTRdmtQ30/800x600");
        imageUrls.add("https://source.unsplash.com/mG28olYFgHI/800x600");
        imageUrls.add("https://source.unsplash.com/oqStl2L5oxI/800x600");
        imageUrls.add("https://source.unsplash.com/fzak3_U4npE/800x600");
        imageUrls.add("https://source.unsplash.com/800x900/?black");

    }
}

