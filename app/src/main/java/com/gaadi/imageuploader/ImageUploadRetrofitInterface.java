package com.gaadi.imageuploader;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by ankurkumarjha on 15/12/15.
 */
public interface ImageUploadRetrofitInterface {
    //public static final String BASE_URL = "http://filmestigation.com";

    @Multipart
    @POST("/saveCertificationImage")
    void upload(@Part("certImg") TypedFile file, @Part("evaluationData") ImageUploadRequestData imageUploadRequestData, Callback<ImageUploadResponse> cb);

    @Multipart
    @POST("/saveCertificationImage")
    ImageUploadResponse upload(@Part("certImg") TypedFile file, @Part("evaluationData") ImageUploadRequestData imageUploadRequestData);
}
