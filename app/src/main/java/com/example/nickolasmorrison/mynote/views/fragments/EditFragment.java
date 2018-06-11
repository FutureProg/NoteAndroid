package com.example.nickolasmorrison.mynote.views.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nickolasmorrison.mynote.Constants;
import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.data.NoteViewModel;
import com.example.nickolasmorrison.mynote.storage.ImageManager;
import com.example.nickolasmorrison.mynote.storage.Note;
import com.example.nickolasmorrison.mynote.views.ImageListAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    private static final String ARG_NOTE_ID = "NoteID", ARG_TRANSITION_ID = "TransitionID";

    private Note note;
    boolean newNote = false;
    private NoteViewModel noteVM;

    private EditText titleView;
    private ImageButton imageShowButton;
    private ImageButton addImagesButton;
    private RecyclerView imageListView;
    private LinearLayout imageHintView;
    private ScrollView textContainerView;
    private EditText contentView;
    private Button doneButton;
    private ProgressBar progressWheel;

    private ImageListAdapter imageListAdapter;


    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditFragment.
     */
    public static EditFragment newInstance(Note note, String transitionName) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        if(note != null)args.putInt(ARG_NOTE_ID, note.getId());
        if(transitionName != null) {
            args.putString(ARG_TRANSITION_ID, transitionName);
        }
        fragment.setArguments(args);
        fragment.note = note;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);
        if( note == null) {
            note = new Note();
            newNote = true;
        }
        if (getArguments() != null) {
            Bundle args = getArguments();
            if(args.containsKey(ARG_NOTE_ID) && note == null){
                int noteid = getArguments().getInt(ARG_NOTE_ID);
                note = noteVM.getNoteByid(noteid).getValue();
            }
        }

        if(savedInstanceState != null) {
            Log.v("InstanceState",savedInstanceState.getInt(ARG_NOTE_ID)+"");
            noteVM.getNoteByid(savedInstanceState.getInt(ARG_NOTE_ID)).observe(
                    this,
                    (Note n) -> {this.note = n; this.initViewComponents(this.getView());}
            );
        }

        Transition fadeTransform = TransitionInflater.from(this.getContext())
                .inflateTransition(android.R.transition.fade);
        setEnterTransition(fadeTransform);

        Transition moveTransform = TransitionInflater.from(this.getContext())
                .inflateTransition(R.transition.open_note_transform);
        setSharedElementEnterTransition(moveTransform);
        setSharedElementReturnTransition(moveTransform);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("InstanceState","Saved instance state: "+note.getId()+"");
        outState.putInt(ARG_NOTE_ID,note.getId());
    }

    @Override
    public void onPause() {
        super.onPause();
        saveChanges();
    }

    public void saveChanges() {
        if(newNote) noteVM.insert(note);
        else noteVM.update(note);
    }

    public void loadImage(View view){
        if(!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            requestGallery();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image_dialog_prompt)
                .setItems(R.array.add_image_dialog_prompt_choices,
                        (DialogInterface dialog, int which) -> {
                            switch (which) {
                                case 0:
                                    requestGallery();
                                    break;
                                case 1:
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if(intent.resolveActivity(getContext().getPackageManager()) != null){
                                        File photoFile = ImageManager.createTempImageFile(getContext(), note);
                                        if(photoFile != null){
                                            Uri photoUri = FileProvider.getUriForFile(getContext(),
                                                    "com.example.nickolasmorrison.mynote.fileprovider",
                                                    photoFile);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                            startActivityForResult(intent,Constants.ActivityResultTakePhoto);
                                        }
                                    }
                                    break;
                            }
                        }
                ).create().show();
    }

    private void requestGallery() {
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, Constants.ActivityResultLoadImage);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            final Uri imageUri = data.getData();
            new ProcessImageAddition(getContext())
                    .fromPhoto(requestCode == Constants.ActivityResultTakePhoto)
                    .execute(imageUri);
        }
    }

    class ProcessImageAddition extends AsyncTask<Uri, Void, String> {
        Context context;
        boolean camera;

        public ProcessImageAddition(Context context) {
            this.context = context;
            this.camera = false;
        }

        public ProcessImageAddition fromPhoto(boolean b){
            this.camera = b;
            return this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressWheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Uri... uris) {
            if(camera){
                return ImageManager.saveTempImageFile(context,note);
            }
            try {
                Uri imageUri = uris[0];
                final InputStream inputStream = context.getContentResolver()
                        .openInputStream(imageUri);
                final Bitmap image = BitmapFactory.decodeStream(inputStream);
                final String imagePath = ImageManager.saveImage(context,note,image);
                return imagePath;
            }catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String imagePath) {
            super.onPostExecute(imagePath);
            progressWheel.setVisibility(View.GONE);
            if(imagePath != null) {
                if(note.images == null) {
                    note.images = new ArrayList<>();
                }
                note.images.add(imagePath);
                noteVM.update(note);
                refreshImages();
            }else {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void refreshImages() {
        if (note.images == null || note.images.isEmpty()) {
            imageShowButton.setVisibility(View.GONE);
            imageListView.setVisibility(View.GONE);
            imageHintView.setVisibility(View.VISIBLE);
        } else {
            imageShowButton.setVisibility(View.VISIBLE);
            imageListView.setVisibility(View.VISIBLE);
            imageHintView.setVisibility(View.GONE);
        }
        imageListAdapter.setImagePaths(note.images);
        imageListAdapter.notifyDataSetChanged();
    }

    private void initViewComponents(View myview) {
        if (note.title != null && !note.title.isEmpty()) {
            titleView.setText(note.title);
            ((TextView)myview.findViewById(R.id.note_title)).setText(note.title);
        }
        if (note.text != null && !note.text.isEmpty()) {
            contentView.setText(note.text);
        }
        if (note.images == null || note.images.isEmpty()) {
            imageShowButton.setVisibility(View.GONE);
            imageListView.setVisibility(View.GONE);
            imageHintView.setVisibility(View.VISIBLE);
        } else {
            imageShowButton.setVisibility(View.VISIBLE);
            imageListView.setVisibility(View.VISIBLE);
            imageHintView.setVisibility(View.GONE);
        }
        textContainerView.setOnClickListener( (View view) -> {
            contentView.setFocusableInTouchMode(true);
            contentView.requestFocus();
            contentView.moveCursorToVisibleOffset();
        });
        doneButton.setOnClickListener( (View view) -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.title = s.toString();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        contentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.text = s.toString();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        addImagesButton.setOnClickListener((View v) -> {this.loadImage(v);});
        imageHintView.setOnClickListener((View v)->{this.loadImage(v);});

        imageListAdapter = new ImageListAdapter(getContext());
        imageListAdapter.setImagePaths(note.images);
        imageListView.setAdapter(imageListAdapter);
        imageListView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false));
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
        doneButton = view.findViewById(R.id.done_button);
        progressWheel = view.findViewById(R.id.progress_circle);

        if (getArguments() != null) {
            Bundle args = getArguments();
            if(args.containsKey(ARG_TRANSITION_ID)) {
                String trName = args.getString(ARG_TRANSITION_ID);
                titleView.setTransitionName(trName);
            }
        }

        if(savedInstanceState == null){
            this.initViewComponents(view);
            startPostponedEnterTransition();
        }
        return view;
    }

}
