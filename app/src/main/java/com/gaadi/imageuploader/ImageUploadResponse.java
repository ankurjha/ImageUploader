package com.gaadi.imageuploader;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankurkumarjha on 18/12/15.
 */
public class ImageUploadResponse implements Serializable{

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ImageUploadResponse{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
