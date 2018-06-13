package com.example.nickolasmorrison.mynote.views.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.storage.ImageManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDetailFragment extends Fragment {

    public static final String ARGS_IMAGE = "ARG_IMAGE";

    String imagePath;
    String transitionName;
    @BindView(R.id.note_image) ImageView imageView;
    @BindView(R.id.close_button) Button closeButton;


    public static ImageDetailFragment createInstance(String image, @Nullable String transitionName){
        Bundle bundle = new Bundle();
        if(image != null){
            bundle.putString(ARGS_IMAGE,image);
        }
        ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.transitionName = transitionName;
        fragment.setArguments(bundle);
        return fragment;
    }

    public ImageDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        Bundle args = getArguments();
        if(args != null){
            imagePath = args.getString(ARGS_IMAGE);
        }

        Transition moveTransform = TransitionInflater.from(this.getContext())
                .inflateTransition(R.transition.open_note_transform);
        setSharedElementEnterTransition(moveTransform);
        setSharedElementReturnTransition(moveTransform);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_detail, container, false);
        ButterKnife.bind(this,view);

        closeButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        this.imageView.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));
        if(this.transitionName != null){
            this.imageView.setTransitionName(this.transitionName);
        }
        ImageManager.loadImage(getContext(),this.imagePath,this.imageView);
        startPostponedEnterTransition();
        return view;
    }

}
