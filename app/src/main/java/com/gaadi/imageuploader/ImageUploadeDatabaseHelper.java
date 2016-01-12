package com.gaadi.imageuploader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankurkumarjha on 21/12/15.
 */
public class ImageUploadeDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "ImageUploadDB";

    private static final String TABLE_NAME = "ImageUpload";

    private static final String KEY_ID = "id";
    private static final String KEY_REF_ID = "ref_id";
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_TRY_COUNT = "try_count";
    private static final String KEY_BLOB_DATA = "blob_data";

    String CREATE_IMAGEUPLOAD_TABLE = "CREATE TABLE " + TABLE_NAME
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_REF_ID + " INTEGER,"
            + KEY_IMAGE_PATH + " TEXT,"
            + KEY_TRY_COUNT + " INTEGER,"
            + KEY_BLOB_DATA + " BLOB"
            + ")";

    public ImageUploadeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_IMAGEUPLOAD_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);

    }

    synchronized void insertImage(ImageModel image) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_REF_ID, image.getRef_id());
            values.put(KEY_IMAGE_PATH, image.getPath());
            values.put(KEY_TRY_COUNT, image.getTry_count());
            values.put(KEY_BLOB_DATA, ImageUploadUtils.serialize(((ImageUploadRequestData) image.getImageUploadRequestData())));

            db.insert(TABLE_NAME, null, values);
        }catch (SQLiteException e){

        }/*finally {
            db.close();
        }*/
    }

    // Getting single image
    synchronized ImageModel getImage(ImageModel image) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_REF_ID, KEY_IMAGE_PATH,KEY_TRY_COUNT,KEY_BLOB_DATA }, KEY_ID + "=?",
                new String[] { String.valueOf(image.get_id()) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImageModel img = new ImageModel(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),
                cursor.getString(2), Integer.parseInt(cursor.getString(3)),(ImageUploadRequestData) ImageUploadUtils.deserialize(cursor.getBlob(4)));

        cursor.close();
        return img;
    }

    synchronized public ArrayList<ImageModel> getAllImages() {
        ArrayList<ImageModel> imageList = new ArrayList<ImageModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImageModel image = new ImageModel();
                image.set_id(Integer.parseInt(cursor.getString(0)));
                image.setRef_id(Integer.parseInt(cursor.getString(1)));
                image.setPath(cursor.getString(2));
                image.setTry_count(Integer.parseInt(cursor.getString(3)));
                image.setImageUploadRequestData((ImageUploadRequestData) ImageUploadUtils.deserialize(cursor.getBlob(4)));
                // Adding image to list
                imageList.add(image);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return imageList;
    }

    // Updating single image
    synchronized public int updateImage(ImageModel image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REF_ID, image.getRef_id());
        values.put(KEY_IMAGE_PATH, image.getPath());
        values.put(KEY_TRY_COUNT, image.getTry_count());
        values.put(KEY_BLOB_DATA, ImageUploadUtils.serialize(((ImageUploadRequestData) image.getImageUploadRequestData())));

        // updating row
        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(image.get_id()) });
    }

    // Deleting single image
    synchronized public void deleteImage(ImageModel image) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(image.get_id())});
       // db.close();
    }

    synchronized public void deleteImageById(int image_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{image_id+""});
        // db.close();
    }


    // Getting images Count
    synchronized public int getImagesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count

        return count;
    }
}
