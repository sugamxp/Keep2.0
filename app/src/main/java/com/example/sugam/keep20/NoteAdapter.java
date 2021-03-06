package com.example.sugam.keep20;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maes.tech.intentanim.CustomIntent;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    boolean selected = false;
    private Context mContext;
    private Cursor mCursor;

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        public NoteViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);

        }

    }

    public NoteAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        final String title = mCursor.getString(mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TITLE));
        final String content = mCursor.getString(mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_CONTENT));
        final long id = mCursor.getLong(mCursor.getColumnIndex(NoteContract.NoteEntry._ID));

        holder.title.setText(title);
        holder.content.setText(content);
        holder.itemView.setTag(id);
        final Context c = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, EditNote.class);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("flag", 1);
                intent.putExtra("id", id);

                c.startActivity(intent);
                CustomIntent.customType(c,"fadein-to-fadeout");
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }
}