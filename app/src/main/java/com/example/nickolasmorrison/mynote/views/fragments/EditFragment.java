package com.example.nickolasmorrison.mynote.views.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.data.NoteViewModel;
import com.example.nickolasmorrison.mynote.storage.Note;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    private static final String ARG_NOTE_ID = "NoteID";

    private Note note;
    private NoteViewModel noteVM;

    private EditText titleView;
    private ImageButton imageShowButton;
    private ImageButton addImagesButton;
    private RecyclerView imageListView;
    private LinearLayout imageHintView;
    private ScrollView textContainerView;
    private EditText contentView;


    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(Note note) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_NOTE_ID, noteid);
        fragment.setArguments(args);
        fragment.note = note;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);
        if (getArguments() != null) {
            int noteid = getArguments().getInt(ARG_NOTE_ID);
            if(getArguments().containsKey(ARG_NOTE_ID) && noteid != -1){
                noteVM.getNoteByid(noteid).observe(this, (Note data) -> {
                    this.note = data;
                    this.initViewComponents();
                });
            }
        }

        if(savedInstanceState != null) {
            this.note = noteVM.getNoteByid(savedInstanceState.getInt(ARG_NOTE_ID)).getValue();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_NOTE_ID,note.getId());
    }


    private void initViewComponents() {
        if (note.title != null || !note.title.isEmpty()) {
            titleView.setText(note.title);
        }
        if (note.text != null || !note.text.isEmpty()) {
            contentView.setText(note.text);
        }
        if (note.images == null) {
            imageShowButton.setVisibility(View.GONE);
            imageListView.setVisibility(View.GONE);
            imageHintView.setVisibility(View.VISIBLE);
        } else {
            imageShowButton.setVisibility(View.VISIBLE);
            imageListView.setVisibility(View.VISIBLE);
            imageHintView.setVisibility(View.GONE);
        }
        textContainerView.setOnClickListener( (View view) -> {
            contentView.requestFocus();
            contentView.moveCursorToVisibleOffset();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        titleView = view.findViewById(R.id.note_title);
        imageShowButton = view.findViewById(R.id.view_images_button);
        addImagesButton = view.findViewById(R.id.add_image_button);
        imageListView = view.findViewById(R.id.note_image_list);
        imageHintView = view.findViewById(R.id.image_hint_text);
        textContainerView = view.findViewById(R.id.note_text_container);
        contentView = view.findViewById(R.id.note_text);

        this.initViewComponents();
        return view;
    }

}
