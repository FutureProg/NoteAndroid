package com.example.nickolasmorrison.mynote.views.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.data.NoteViewModel;
import com.example.nickolasmorrison.mynote.storage.Note;
import com.example.nickolasmorrison.mynote.storage.NoteRepository;
import com.example.nickolasmorrison.mynote.views.NoteListAdapter;
import com.example.nickolasmorrison.mynote.views.NoteListTouchHelper;

import java.util.List;

public class MainFragment extends Fragment implements NoteListTouchHelper.Listener,
    NoteListAdapter.OnClickListener{

    private InteractionListener mListener;
    private NoteListAdapter listAdapter;
    private NoteViewModel noteVM;

    private RecyclerView recyclerView;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listAdapter = new NoteListAdapter(this.getContext(),this);
        noteVM.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                listAdapter.setNotes(notes);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new NoteListTouchHelper(
                0,
                ItemTouchHelper.RIGHT,
                this,
                view.findViewById(R.id.delete_section)
        );
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        // FAB Callback
        view.findViewById(R.id.floatingActionButton).setOnClickListener((View v) -> {
                addNote(v);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void addNote(View view) {
        Note note = new Note();
        note.text = "Hello";
        note.title = "Hello World";
        noteVM.insert(note);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder != null) {
            final Note note = listAdapter.getNote(viewHolder.getAdapterPosition());
            if( note == null )return;
            final String title = note.title;
            noteVM.delete(note);

            Snackbar snackbar = Snackbar.make(
                    getView().findViewById(R.id.constraint_layout),
                    "Deleted Note " + title,
                    Snackbar.LENGTH_LONG
            );
            snackbar.setAction("Undo", (View v) -> {
                noteVM.insert(note);
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onClick(View view, Note note) {
        Log.v("MainFragment","Swap to editor with note: " + note);
        if(note == null) return;
        EditFragment fragment = EditFragment.newInstance(note);
        FragmentManager manager = this.getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container,fragment)
                .addToBackStack(null).commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface InteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
