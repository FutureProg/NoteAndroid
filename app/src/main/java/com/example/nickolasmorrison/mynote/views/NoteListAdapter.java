package com.example.nickolasmorrison.mynote.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.storage.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private final LayoutInflater inflater;
    private List<Note> notes;

    public NoteListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.note_recycler_item, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if(notes != null){
            Note current = notes.get(position);
            holder.notePreviewView.setText(current.text);
            holder.titleView.setText(current.title);
            DateFormat df = new SimpleDateFormat("MMMMM dd, yyyy");
            holder.dateView.setText(df.format(current.date));
        }
    }

    @Override
    public int getItemCount() {
        return notes != null? notes.size() : 0;
    }

    void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends  RecyclerView.ViewHolder{
        private final TextView titleView;
        private final TextView dateView;
        private final TextView notePreviewView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            this.titleView = itemView.findViewById(R.id.note_title);
            this.dateView = itemView.findViewById(R.id.note_date);
            this.notePreviewView = itemView.findViewById(R.id.note_text_preview);
        }
    }

}