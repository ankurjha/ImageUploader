package com.gaadi.imageuploader;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by ankurkumarjha on 18/12/15.
 */
public class ImageUploadServiceSyncronus extends IntentService{

    private  final int MAX_ATTEMPTS = 3;
    private  final int BACKOFF_MILLI_SECONDS = 1000;
    private  final int MULTIPLIER = 2;

    int successCount = 0 ;
    int imageCount = 0;

    int callingPoint = 0;

    ArrayList<ImageUploadRequestData> imageUploadRequestDataList;

    //public static String END_POINT_URL = "http://filmestigation.com";
    //public static String END_POINT_URL = "http://beta.usedcarsin.in/wm_v2/webapis/evaluation";

    String endPoint = null;
    RestAdapter restAdapter;
    ImageUploadRetrofitInterface uploader;


    public ImageUploadServiceSyncronus() {
        super("ImageUploadService");
    }

    @Override
    public void onCreate() {
        Log.d("ankur", "Intent Service onCreate");
        super.onCreate();
        //imageUploadeDB = new ImageUploadeDatabaseHelper(this);//move this to application controller
        ImageUploadUtils.setBooleanSharedPreference(getApplicationContext(), ImageUploadUtils.IMAGE_UPLOAD_SERVICE_RUNNING, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ankur", "Intent Service onDestroy");
        ImageUploadUtils.setBooleanSharedPreference(getApplicationContext(), ImageUploadUtils.IMAGE_UPLOAD_SERVICE_RUNNING, false);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("ankur", "Intent Service onHandleIntent");

        callingPoint = intent.getIntExtra(ImageUploadUtils.IMAGE_UPLOAD_SERVICE_CALLED_FROM, ImageUploadUtils.FROM_PENDING_IMAGE_RECEIVER);

        endPoint = intent.getStringExtra(ImageUploadUtils.END_POINT_URL);

        if(callingPoint == ImageUploadUtils.FROM_IMAGE_UPLOAD_ACTIVITY) {
            imageUploadRequestDataList = (ArrayList<ImageUploadRequestData>) intent.getSerializableExtra(ImageUploadUtils.IMAGE_UPLOAD_REQUEST_DATA_LIST);
            Log.d("ankur", "Intent Service onHandleIntent imageUploadRequestDataList = " + imageUploadRequestDataList.size());

            if (imageUploadRequestDataList != null && imageUploadRequestDataList.size() > 0 && endPoint != null) {

                for (int i = 0; i < imageUploadRequestDataList.size(); i++) {
                    ImageUploaderApplicationController.getImageUploadeDB().insertImage(new ImageModel(i + 1, imageUploadRequestDataList.get(i).ucc_id, imageUploadRequestDataList.get(i).getImagePath(), 0, imageUploadRequestDataList.get(i)));
                }
            }
        }

        ArrayList<ImageModel> imageModelList = ImageUploaderApplicationController.getImageUploadeDB().getAllImages();
        imageCount = imageModelList.size();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        uploader = restAdapter.create(ImageUploadRetrofitInterface.class);

        if (imageModelList.size() > 0) {
            upload(imageModelList, 0);
        }



        Log.d("ankur", "Intent Service ends");

    }

    /*public void printImageDB(){
        Log.d("ankur", "printImageDB method");
        ArrayList<ImageModel> imageList = imageUploadeDB.getAllImages();

        for(ImageModel cn : imageList) {
            String log = "Id: " + cn.get_id() + " ,Ref Id: " + cn.getRef_id() + " ,Path: " + cn.getPath()+" ,try count : "+cn.getTry_count();
            String name = cn.getPath();
            int index = name.lastIndexOf('/');
            name = name.substring(index+1);
            Log.d("ankur", "name : " + name);
            // Writing Contacts to log
            Log.d("ankur", log);

        }

    }*/

    public void upload(final ArrayList<ImageModel> imageModelList, final int index) {
        Log.d("ankur", "upload method imageModelList.size() = " + imageModelList.size());
        Log.d("ankur", "uploading image PK = " + imageModelList.get(index).get_id());
        if (!ImageUploadUtils.checkInternetConnectivity(this)) {
            Log.d("ankur", "Internet naikhe ba...");
            //return;
        }

        String filePath = imageModelList.get(index).getPath();
        final int LastIndex = filePath.lastIndexOf('/');
        final String fileName = filePath.substring(LastIndex + 1);

        Log.d("ankur", "filePath : " + filePath);
        Log.d("ankur", "fileName : " + fileName);

        TypedFile image = new TypedFile("image/*", new File(filePath));

        //ImageUploadRequestData imageUploadRequestData = new ImageUploadRequestData("U3KqyrewdMuCotTS","json","qprw4MbH387iq8zazt3ck8bh0g",123456,12345,"10","2","abc.jpg","app");

        ImageUploadRequestData imageUploadRequestData = imageModelList.get(index).getImageUploadRequestData();
        try {
            ImageUploadResponse imageUploadResponse = uploader.upload(image, imageUploadRequestData);

            Log.e("Upload", "success = " + imageUploadResponse.toString());



            if (imageUploadResponse.getStatus().equalsIgnoreCase("T")) {

                successCount++;
                ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));

                ImageUploadUtils.createImageUploadNotification(ImageUploadServiceSyncronus.this,
                        "1009",
                        "Uploading " + successCount + " of " + imageCount + " Images",
                        true, null, null);

                imageModelList.remove(index);

                if (imageModelList.size() > 0)
                    upload(imageModelList, index);
            } else {
                ImageModel imageModel = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                int failureCount = imageModel.getTry_count();
                Log.d("ankur", "failureCount in else: " + failureCount);
                ++failureCount;
                imageModel.setTry_count(failureCount);
                ImageUploaderApplicationController.getImageUploadeDB().updateImage(imageModel);
                if (failureCount <= MAX_ATTEMPTS) {
                    Log.d("ankur", "failureCount <= MAX_ATTEMPTS in else: ");
                    /*Handler handler = new Handler(getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ankur", "failureCount <= MAX_ATTEMPTS in else:run ");
                            upload(imageModelList, index);
                        }
                    }, (long) (BACKOFF_MILLI_SECONDS * Math.pow(MULTIPLIER, failureCount)));*/
                    try {
                        Log.d("ankur", "failureCount <= MAX_ATTEMPTS in else: seelping for = "+(long) (BACKOFF_MILLI_SECONDS * Math.pow(MULTIPLIER, failureCount)));

                        // thread to sleep for 1000 milliseconds
                        Thread.sleep((long) (BACKOFF_MILLI_SECONDS * Math.pow(MULTIPLIER, failureCount)));
                        upload(imageModelList, index);
                    } catch (Exception e) {
                        Log.d("ankur", "error in sleep = "+e.toString());
                    }
                } else {
                    Log.d("ankur", "failureCount <= MAX_ATTEMPTS in else:else ");
                    ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                    imageModelList.remove(index);
                    if (imageModelList.size() > 0)
                        upload(imageModelList, index);
                }
            }
        } catch (RetrofitError error) {
            //error.printStackTrace();
            Log.e("Upload", "error in success = " + error.toString());
            Log.i("ankur", "name of delete error = " + imageModelList.get(index).getPath());
            ImageModel imageModel = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
            int failureCount = imageModel.getTry_count();
            Log.d("ankur", "failureCount in error: " + failureCount);
            ++failureCount;
            imageModel.setTry_count(failureCount);
            ImageUploaderApplicationController.getImageUploadeDB().updateImage(imageModel);
            if (failureCount <= MAX_ATTEMPTS) {
                Log.d("ankur", "failureCount <= MAX_ATTEMPTS in error");
                /*Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ankur", "failureCount <= MAX_ATTEMPTS in error run");
                        upload(imageModelList, index);
                    }
                }, (long) (BACKOFF_MILLI_SECONDS * Math.pow(MULTIPLIER, failureCount)));*/
                try {
                    Log.d("ankur", "failureCount <= MAX_ATTEMPTS in error seelping for = "+(long) (BACKOFF_MILLI_SECONDS * Math.pow(MULTIPLIER, failureCount)));
                    // thread to sleep for 1000 milliseconds
                    Thread.sleep((long) (BACKOFF_MILLI_SECONDS * Math.pow(MULTIPLIER, failureCount)));
                    upload(imageModelList, index);
                } catch (Exception e) {
                    Log.d("ankur", "error in sleep = "+e.toString());
                }
            } else {
                Log.d("ankur", "failureCount <= MAX_ATTEMPTS in error:else");
                ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                imageModelList.remove(index);
                if (imageModelList.size() > 0)
                    upload(imageModelList, index);
            }
        }
    }

}
