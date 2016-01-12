package com.gaadi.imageuploader;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankurkumarjha on 28/12/15.
 */
public class ImageUploadRequestData implements Serializable{

    @SerializedName("apikey")
    String apikey;

    @SerializedName("output")
    String output;

    @SerializedName("userKey")
    String userKey;

    @SerializedName("ucc_id")
    int ucc_id;

    @SerializedName("uc_id")
    int uc_id;

    @SerializedName("tag_id")
    String tag_id;

    @SerializedName("img_type")
    String img_type;

    @SerializedName("file_name")
    String file_name;

    @SerializedName("source")
    String source;

    String imagePath;

    public ImageUploadRequestData(String apikey, String output, String userKey, int ucc_id, int uc_id, String tag_id, String img_type, String file_name, String source, String imagePath) {
        this.apikey = apikey;
        this.output = output;
        this.userKey = userKey;
        this.ucc_id = ucc_id;
        this.uc_id = uc_id;
        this.tag_id = tag_id;
        this.img_type = img_type;
        this.file_name = file_name;
        this.source = source;
        this.imagePath = imagePath;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public int getUcc_id() {
        return ucc_id;
    }

    public void setUcc_id(int ucc_id) {
        this.ucc_id = ucc_id;
    }

    public int getUc_id() {
        return uc_id;
    }

    public void setUc_id(int uc_id) {
        this.uc_id = uc_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getImg_type() {
        return img_type;
    }

    public void setImg_type(String img_type) {
        this.img_type = img_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
