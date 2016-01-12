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
public class ImageUploadServiceNew extends IntentService{

    private  final int MAX_ATTEMPTS = 3;
    private  final int BACKOFF_MILLI_SECONDS = 1000;
    private  final int MULTIPLIER = 2;

    int successCount = 0 ;
    int imageCount = 0;

    ArrayList<ImageUploadRequestData> imageUploadRequestDataList;

    //public static String END_POINT_URL = "http://filmestigation.com";
    //public static String END_POINT_URL = "http://beta.usedcarsin.in/wm_v2/webapis/evaluation";

    String endPoint = null;
    RestAdapter restAdapter;
    ImageUploadRetrofitInterface uploader;


    public ImageUploadServiceNew() {
        super("ImageUploadService");
    }

    @Override
    public void onCreate() {
        Log.d("ankur", "Intent Service onCreate");
        super.onCreate();
        //imageUploadeDB = new ImageUploadeDatabaseHelper(this);//move this to application controller
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ankur", "Intent Service onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("ankur", "Intent Service onHandleIntent");


        imageUploadRequestDataList = (ArrayList<ImageUploadRequestData>) intent.getSerializableExtra(ImageUploadUtils.IMAGE_UPLOAD_REQUEST_DATA_LIST);
        endPoint = intent.getStringExtra(ImageUploadUtils.END_POINT_URL);

        Log.d("ankur", "Intent Service onHandleIntent imageUploadRequestDataList = " + imageUploadRequestDataList.size());

        if(imageUploadRequestDataList!=null && imageUploadRequestDataList.size()>0 && endPoint!=null) {

            for (int i = 0; i < imageUploadRequestDataList.size(); i++) {
                ImageUploaderApplicationController.getImageUploadeDB().insertImage(new ImageModel(i + 1, imageUploadRequestDataList.get(i).ucc_id, imageUploadRequestDataList.get(i).getImagePath(), 0, imageUploadRequestDataList.get(i)));
            }

            final ArrayList<ImageModel> imageModelList = ImageUploaderApplicationController.getImageUploadeDB().getAllImages();
            imageCount = imageModelList.size();

            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(endPoint)
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    .build();

            uploader = restAdapter.create(ImageUploadRetrofitInterface.class);

            if (imageModelList.size() > 0) {
                upload(imageModelList, 0);
                for(int index=0;index<imageCount;index++){

                    if (!ImageUploadUtils.checkInternetConnectivity(this))
                        break;

                    String filePath = imageModelList.get(index).getPath();
                    final int id = imageModelList.get(index).get_id();
                    final int LastIndex = filePath.lastIndexOf('/');
                    final String fileName = filePath.substring(LastIndex + 1);

                    TypedFile image = new TypedFile("image/*", new File(filePath));

                    ImageUploadRequestData imageUploadRequestData = imageModelList.get(index).getImageUploadRequestData();

                    /*uploader.upload(image,imageUploadRequestData, new Callback<ImageUploadResponse>() {
                        @Override
                        public void success(ImageUploadResponse imageUploadResponse, Response response) {

                            Log.e("Upload", "success = " + imageUploadResponse.toString());

                            try {

                                if (imageUploadResponse.getStatus().equalsIgnoreCase("T")) {

                                    successCount++;
                                    ImageUploaderApplicationController.getImageUploadeDB().deleteImageById(id);

                                    ImageUploadUtils.createImageUploadNotification(ImageUploadServiceNew.this,
                                            "1009",
                                            "Uploading " + successCount + " of " + imageCount + " Images",
                                            true, null, null);

                                    //imageModelList.remove(index);

                                    //if (imageModelList.size() > 0)
                                        //upload(imageModelList, index);
                                }else{
                                    ImageModel image = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                                    int failureCount = image.getTry_count();
                                    Log.d("ankur", "failureCount : " + failureCount);
                                    ++failureCount;
                                    image.setTry_count(failureCount);
                                    ImageUploaderApplicationController.getImageUploadeDB().updateImage(image);
                                    if(failureCount<=MAX_ATTEMPTS) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                upload(imageModelList, index);
                                            }
                                        }, (long) (BACKOFF_MILLI_SECONDS*Math.pow(MULTIPLIER,failureCount)));
                                    }else{
                                        ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                                        imageModelList.remove(index);
                                        if (imageModelList.size() > 0)
                                            upload(imageModelList, index);
                                    }
                                }
                            }catch(RetrofitError error){
                                error.printStackTrace();
                                Log.e("Upload", "error in success = " + error.toString());
                                ImageModel image = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                                int failureCount = image.getTry_count();
                                Log.d("ankur", "failureCount : " + failureCount);
                                ++failureCount;
                                image.setTry_count(failureCount);
                                ImageUploaderApplicationController.getImageUploadeDB().updateImage(image);
                                if(failureCount<=MAX_ATTEMPTS) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            upload(imageModelList, index);
                                        }
                                    }, (long) (BACKOFF_MILLI_SECONDS*Math.pow(MULTIPLIER,failureCount)));
                                }else{
                                    ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                                    imageModelList.remove(index);
                                    if (imageModelList.size() > 0)
                                        upload(imageModelList, index);
                                }
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {

                            Log.e("Upload", "error = " + error.toString());

                            ImageModel image = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                            int failureCount = image.getTry_count();
                            Log.d("ankur", "failureCount : " + failureCount);
                            ++failureCount;
                            image.setTry_count(failureCount);
                            ImageUploaderApplicationController.getImageUploadeDB().updateImage(image);
                            if(failureCount<=MAX_ATTEMPTS) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        upload(imageModelList, index);
                                    }
                                }, (long) (BACKOFF_MILLI_SECONDS*Math.pow(MULTIPLIER,failureCount)));
                            }else{
                                ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                                imageModelList.remove(index);
                                if (imageModelList.size() > 0)
                                    upload(imageModelList, index);
                            }
                        }
                    });*/


                }
            }

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

    public void upload(final ArrayList<ImageModel> imageModelList, final int index){
        Log.d("ankur", "upload method imageModelList.size() = "+imageModelList.size());
        Log.d("ankur", "uploading image PK = "+imageModelList.get(index).get_id());
        if (!ImageUploadUtils.checkInternetConnectivity(this))
            return;

        String filePath = imageModelList.get(index).getPath();
        final int LastIndex = filePath.lastIndexOf('/');
        final String fileName = filePath.substring(LastIndex+1);

        Log.d("ankur", "filePath : " + filePath);
        Log.d("ankur", "fileName : " + fileName);

        TypedFile image = new TypedFile("image/*", new File(filePath));

        //ImageUploadRequestData imageUploadRequestData = new ImageUploadRequestData("U3KqyrewdMuCotTS","json","qprw4MbH387iq8zazt3ck8bh0g",123456,12345,"10","2","abc.jpg","app");

        ImageUploadRequestData imageUploadRequestData = imageModelList.get(index).getImageUploadRequestData();

        uploader.upload(image,imageUploadRequestData, new Callback<ImageUploadResponse>() {
            @Override
            public void success(ImageUploadResponse imageUploadResponse, Response response) {

                Log.e("Upload", "success = " + imageUploadResponse.toString());

                try {

                    if (imageUploadResponse.getStatus().equalsIgnoreCase("T")) {

                        successCount++;
                        ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));

                        ImageUploadUtils.createImageUploadNotification(ImageUploadServiceNew.this,
                                "1009",
                                "Uploading " + successCount + " of " + imageCount + " Images",
                                true, null, null);

                        imageModelList.remove(index);

                        if (imageModelList.size() > 0)
                            upload(imageModelList, index);
                    }else{
                        ImageModel image = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                        int failureCount = image.getTry_count();
                        Log.d("ankur", "failureCount : " + failureCount);
                        ++failureCount;
                        image.setTry_count(failureCount);
                        ImageUploaderApplicationController.getImageUploadeDB().updateImage(image);
                        if(failureCount<=MAX_ATTEMPTS) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    upload(imageModelList, index);
                                }
                            }, (long) (BACKOFF_MILLI_SECONDS*Math.pow(MULTIPLIER,failureCount)));
                        }else{
                            ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                            imageModelList.remove(index);
                            if (imageModelList.size() > 0)
                                upload(imageModelList, index);
                        }
                    }
                }catch(RetrofitError error){
                    error.printStackTrace();
                    Log.e("Upload", "error in success = " + error.toString());
                    ImageModel image = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                    int failureCount = image.getTry_count();
                    Log.d("ankur", "failureCount : " + failureCount);
                    ++failureCount;
                    image.setTry_count(failureCount);
                    ImageUploaderApplicationController.getImageUploadeDB().updateImage(image);
                    if(failureCount<=MAX_ATTEMPTS) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                upload(imageModelList, index);
                            }
                        }, (long) (BACKOFF_MILLI_SECONDS*Math.pow(MULTIPLIER,failureCount)));
                    }else{
                        ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                        imageModelList.remove(index);
                        if (imageModelList.size() > 0)
                            upload(imageModelList, index);
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {

                Log.e("Upload", "error = " + error.toString());

                ImageModel image = ImageUploaderApplicationController.getImageUploadeDB().getImage(imageModelList.get(index));
                int failureCount = image.getTry_count();
                Log.d("ankur", "failureCount : " + failureCount);
                ++failureCount;
                image.setTry_count(failureCount);
                ImageUploaderApplicationController.getImageUploadeDB().updateImage(image);
                if(failureCount<=MAX_ATTEMPTS) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            upload(imageModelList, index);
                        }
                    }, (long) (BACKOFF_MILLI_SECONDS*Math.pow(MULTIPLIER,failureCount)));
                }else{
                    ImageUploaderApplicationController.getImageUploadeDB().deleteImage(imageModelList.get(index));
                    imageModelList.remove(index);
                    if (imageModelList.size() > 0)
                        upload(imageModelList, index);
                }
            }
        });

    }
}
