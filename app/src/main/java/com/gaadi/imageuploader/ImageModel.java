package com.gaadi.imageuploader;

import java.io.Serializable;

/**
 * Created by ankurkumarjha on 21/12/15.
 */
public class ImageModel implements Serializable {

    private int _id;
    private int ref_id;
    private String path;
    private int try_count;
    private ImageUploadRequestData imageUploadRequestData;

    public ImageModel() {
    }

    public ImageModel(int _id, int ref_id, String path, int try_count, ImageUploadRequestData imageUploadRequestData) {
        this._id = _id;
        this.ref_id = ref_id;
        this.path = path;
        this.try_count = try_count;
        this.imageUploadRequestData = imageUploadRequestData;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getRef_id() {
        return ref_id;
    }

    public void setRef_id(int ref_id) {
        this.ref_id = ref_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTry_count() {
        return try_count;
    }

    public void setTry_count(int try_count) {
        this.try_count = try_count;
    }

    public ImageUploadRequestData getImageUploadRequestData() {
        return imageUploadRequestData;
    }

    public void setImageUploadRequestData(ImageUploadRequestData imageUploadRequestData) {
        this.imageUploadRequestData = imageUploadRequestData;
    }
}
