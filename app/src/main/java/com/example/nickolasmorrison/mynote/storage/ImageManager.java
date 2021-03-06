package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.Delete;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class ImageManager {

    private static String basePath;
    private static String tempImagePath;
    private static final HashMap<String,Bitmap> memoryCache = new HashMap<>();

    static class LoadAsync extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public LoadAsync(@Nullable ImageView imgView){
            this.imageView = imgView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imagepath = basePath + strings[0];
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            memoryCache.put(strings[0],bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(imageView != null) imageView.setImageBitmap(bitmap);
        }
    }

    static class DeleteAsync extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... path) {
            File file = new File(path[0]);
            if(file.delete()){
                Log.v("ImageManager","Deleted image at path: " + path[0]);
            }else{
                Log.v("ImageManager","Failed to delete image at path: " + path[0]);
            }
            return null;
        }
    }



    public static void getExternalDir(Context context){
        if(basePath == null){
            basePath = context.getFilesDir().getAbsolutePath();
        }
    }

    public static void bufferNote(Context context, Note note, int count){
        getExternalDir(context);
        if(note.images != null){
            for(int i = 0; i < note.images.size() && i < count; i++){
                if(memoryCache.containsKey(note.images.get(i))) continue;
                new LoadAsync(null).execute(note.images.get(i));
            }
        }
    }

    public static File createTempImageFile(Context context, Note note) {
        getExternalDir(context);
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssZ").format(new Date());
        int noteId = note.getId();
        String filename = "Note_" + noteId + "_" + timestamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File tempFile;
        try {
            tempFile = File.createTempFile(filename,".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        tempImagePath = tempFile.getAbsolutePath();
        return tempFile;
    }

    public static String saveTempImageFile(Context context, Note note){
        getExternalDir(context);
        if(tempImagePath == null) return null;
        Bitmap bitmap = BitmapFactory.decodeFile(tempImagePath);
        return saveImage(context,note,bitmap);
    }

    public static void loadImage(Context context, String path, ImageView imageView) {
        getExternalDir(context);
        if(memoryCache.containsKey(path)){
            imageView.setImageBitmap(memoryCache.get(path));
            return;
        }
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
        memoryCache.put(path,bitmap);
        return  path;
    }

    public static void deleteImage(Context context, String path) {
        getExternalDir(context);
        if(memoryCache.containsKey(path)) memoryCache.remove(path);
        new DeleteAsync().execute(basePath + path);
    }

}
