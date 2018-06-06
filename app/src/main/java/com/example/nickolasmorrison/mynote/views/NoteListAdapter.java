package com.example.nickolasmorrison.mynote.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            DateFormat df = SimpleDateFormat.getDateInstance();
            holder.dateView.setText(df.format(current.date));
            if(current.images == null){
                holder.noteImageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notes != null? notes.size() : 0;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public Note getNote(int index){
        if(this.notes == null || index >= this.notes.size() || index < 0) return null;
        return this.notes.get(index);
    }

    class NoteViewHolder extends  RecyclerView.ViewHolder{
        private final TextView titleView;
        private final TextView dateView;
        private final TextView notePreviewView;
        private final ImageView noteImageView;
        private final View card;
        private final View cardContainer;

        public NoteViewHolder(View itemView) {
            super(itemView);
            this.titleView = itemView.findViewById(R.id.note_title);
            this.dateView = itemView.findViewById(R.id.note_date);
            this.notePreviewView = itemView.findViewById(R.id.note_text_preview);
            this.card = itemView.findViewById(R.id.note_card);
            this.cardContainer = itemView.findViewById(R.id.note_card_container);
            this.noteImageView = itemView.findViewById(R.id.note_image_preview);
        }

        public View getCardContainer() {
            return cardContainer;
        }
        public View getCard(){
            return card;
        }
    }

}
