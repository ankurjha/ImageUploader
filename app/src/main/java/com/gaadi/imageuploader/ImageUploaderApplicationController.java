package com.gaadi.imageuploader;

import android.app.Application;

/**
 * Created by ankurkumarjha on 30/12/15.
 */
public class ImageUploaderApplicationController extends Application {

    private static ImageUploadeDatabaseHelper imageUploadeDB;

    @Override
    public void onCreate() {
        super.onCreate();
        imageUploadeDB = new ImageUploadeDatabaseHelper(this);
    }

    public static synchronized ImageUploadeDatabaseHelper getImageUploadeDB() {
        return imageUploadeDB;
    }
}
