package com.example.nickolasmorrison.mynote.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.example.nickolasmorrison.mynote.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageManager {

    private static String basePath;

    static class LoadAsync extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public LoadAsync(ImageView imgView){
            this.imageView = imgView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imagepath = basePath + strings[0];
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
    
    public static void getExternalDir(Context context){
        if(basePath == null){
            basePath = context.getFilesDir().getAbsolutePath();
        }
    }

    public static void loadImage(Context context, String path, ImageView imageView) {
        getExternalDir(context);
        Log.v("ImageManager","Loading image at path: " + path);
        new LoadAsync(imageView).execute(path);
//        Target trgt = Glide.with(context)
//                .load(new File(path.startsWith(basePath)? path : basePath + "/" + path))
//                .
//                .into(imageView);
//        trgt.onLoadStarted(null);
//        trgt.onLoadCleared(null);
//        trgt.onLoadFailed(null);
    }

    public static String saveImage(Context context, Note note, Bitmap bitmap) {
        getExternalDir(context);
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssZ").format(new Date());
        int noteId = note.getId();
        String filename = "Note_" + noteId + "_" + timestamp + ".jpg";
        // Make the folder if DNE //
        File folder = new File(basePath + "/" + Constants.ImageFolderName);
        if(!folder.exists()){
            folder.mkdir();
        }
        // ----------------------//
        String path = "/" + Constants.ImageFolderName  + "/" + filename;

        try {
            File file = new File(basePath + path);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return  path;
    }

    public static void deleteImage(Context context, String path) {
        getExternalDir(context);
    }

}
