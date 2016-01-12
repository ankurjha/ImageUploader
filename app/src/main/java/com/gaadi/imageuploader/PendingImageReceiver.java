package com.gaadi.imageuploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class PendingImageReceiver extends BroadcastReceiver {
    public PendingImageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");

        Intent startImageUploadIntent = new Intent(context, ImageUploadServiceSyncronus.class);

        startImageUploadIntent.putExtra(ImageUploadUtils.END_POINT_URL,ImageUploadActivity.END_POINT_URL);
        startImageUploadIntent.putExtra(ImageUploadUtils.IMAGE_UPLOAD_SERVICE_CALLED_FROM, ImageUploadUtils.FROM_PENDING_IMAGE_RECEIVER);

        int pendingImageCount = ImageUploaderApplicationController.getImageUploadeDB().getImagesCount();


        if (intent.getAction().toString().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Log.i("ankur", "ACTION_BOOT_COMPLETED");
            ArrayList<ImageModel> imageModelList = ImageUploaderApplicationController.getImageUploadeDB().getAllImages();
            for (ImageModel cn : imageModelList) {
                String log = "Id: " + cn.get_id() + " ,Ref Id: " + cn.getRef_id() + " ,Path: " + cn.getPath() + " ,try count : " + cn.getTry_count();
                // Writing Contacts to log
                Log.d("ankur", log);

            }

            if(!ImageUploadUtils.getBooleanSharedPreference(context, ImageUploadUtils.IMAGE_UPLOAD_SERVICE_RUNNING, false)) {
                Log.i("ankur", "ACTION_BOOT_COMPLETED IMAGE_UPLOAD_SERVICE_RUNNING not runinng , starting the service");
                if(pendingImageCount > 0)
                    context.startService(startImageUploadIntent);
            }

        } else {
            if (ImageUploadUtils.checkInternetConnectivity(context)) {

                Log.i("ankur", "checkInternetConnectivity");
                ArrayList<ImageModel> imageModelList = ImageUploaderApplicationController.getImageUploadeDB().getAllImages();
                for (ImageModel cn : imageModelList) {
                    String log = "Id: " + cn.get_id() + " ,Ref Id: " + cn.getRef_id() + " ,Path: " + cn.getPath() + " ,try count : " + cn.getTry_count();
                    // Writing Contacts to log
                    Log.d("ankur", log);

                }

                /*CommonUtils.setIntSharedPreference(ApplicationController.getInstance(), RetrofitImageUploadService.KEY_MAXRETRY, 5);
                Intent startImageUpload = new Intent(ApplicationController.getInstance(), RetrofitImageUploadService.class);
                ApplicationController.getInstance().startService(startImageUpload);*/
                if(!ImageUploadUtils.getBooleanSharedPreference(context, ImageUploadUtils.IMAGE_UPLOAD_SERVICE_RUNNING, false)) {
                    Log.i("ankur", "checkInternetConnectivity IMAGE_UPLOAD_SERVICE_RUNNING not runinng , starting the service");
                    if(pendingImageCount > 0)
                        context.startService(startImageUploadIntent);
                }

            }
        }
    }
}
