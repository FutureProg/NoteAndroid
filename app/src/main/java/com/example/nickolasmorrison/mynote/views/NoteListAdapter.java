package com.example.nickolasmorrison.mynote.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.storage.ImageManager;
import com.example.nickolasmorrison.mynote.storage.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private final LayoutInflater inflater;
    private List<Note> notes;
    private OnClickListener clickListener;

    public NoteListAdapter(Context context, OnClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.clickListener = listener;
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
            final Note current = notes.get(position);
            ImageManager.bufferNote(inflater.getContext(),current,3);
            holder.notePreviewView.setText(current.text);
            holder.titleView.setText(current.title);
            DateFormat df = SimpleDateFormat.getDateInstance();
            holder.dateView.setText(df.format(current.date));
            if(current.images == null || current.images.isEmpty()){
                holder.noteImageView.setVisibility(View.GONE);
                holder.countView.setVisibility(View.GONE);
            } else {
                ImageManager.loadImage(
                        inflater.getContext(),
                        current.images.get(0),
                        holder.noteImageView
                        );
                if(current.images.size() > 1){
                    holder.countView.setText(current.images.size() + "");
                }else{
                    holder.countView.setVisibility(View.GONE);
                }
            }
            if(clickListener != null) {
                holder.card.setOnClickListener((View v) -> {
                    clickListener.onClick(v,current);
                });
            }
            holder.titleView.setTransitionName( "TitleViewTransition" + position);
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

    public interface OnClickListener{
        void onClick(View view, Note note);
    }

    class NoteViewHolder extends  RecyclerView.ViewHolder{
        private final TextView titleView;
        private final TextView dateView;
        private final TextView notePreviewView;
        private final ImageView noteImageView;
        private final View card;
        private final View cardContainer;
        private final TextView countView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            this.titleView = itemView.findViewById(R.id.note_title);
            this.dateView = itemView.findViewById(R.id.note_date);
            this.notePreviewView = itemView.findViewById(R.id.note_text_preview);
            this.card = itemView.findViewById(R.id.note_card);
            this.cardContainer = itemView.findViewById(R.id.note_card_container);
            this.noteImageView = itemView.findViewById(R.id.note_image_preview);
            this.countView = itemView.findViewById(R.id.note_image_count);
        }

        public View getCardContainer() {
            return cardContainer;
        }
        public View getCard(){
            return card;
        }
    }

}
