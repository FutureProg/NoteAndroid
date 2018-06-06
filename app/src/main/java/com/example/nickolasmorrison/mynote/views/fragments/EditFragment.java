package com.example.nickolasmorrison.mynote.views.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private int noteid;
    private Note note;
    private NoteViewModel noteVM;


    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteid The id of the note to edit
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(int noteid) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NOTE_ID, noteid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);
            noteid = getArguments().getInt(ARG_NOTE_ID);
            
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

}
