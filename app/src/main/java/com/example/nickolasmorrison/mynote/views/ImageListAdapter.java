package com.example.nickolasmorrison.mynote.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.nickolasmorrison.mynote.R;
import com.example.nickolasmorrison.mynote.storage.ImageManager;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    private List<String> imagePaths;
    private Context context;

    public ImageListAdapter(Context context){
        super();
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imgView = new ImageView(context);
        final float scale = parent.getResources().getDisplayMetrics().density;
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(Math.round(64 * scale),Math.round(64 * scale));
        params.setMargins(Math.round(8 * scale), 0, 0, Math.round(8 * scale));
        imgView.setLayoutParams(params);
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setImageDrawable(parent.getContext().getDrawable(R.drawable.ic_image_black_semi_transparent24dp));
        return new ImageViewHolder(imgView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageManager.loadImage(context,imagePaths.get(position),holder.imageView);
    }

    public void setImagePaths(List<String> imagePaths){
        this.imagePaths = imagePaths;
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(View itemView){
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}
