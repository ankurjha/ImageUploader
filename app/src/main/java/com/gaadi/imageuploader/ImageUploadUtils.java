package com.gaadi.imageuploader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by ankurkumarjha on 28/12/15.
 */
public class ImageUploadUtils {

    public static String IMAGE_UPLOAD_REQUEST_DATA_LIST = "imageUploadRequestDataList";

    public static String END_POINT_URL = "endPointURL"; //"http://beta.usedcarsin.in/wm_v2/webapis/evaluation";

    public static int FROM_IMAGE_UPLOAD_ACTIVITY = 1;

    public static int FROM_PENDING_IMAGE_RECEIVER = 2;

    public static String IMAGE_UPLOAD_SERVICE_CALLED_FROM = "Image_Upload_Service_Called_From";

    public static String IMAGE_UPLOAD_PREF = "Image_Upload_Service";

    public static String IMAGE_UPLOAD_SERVICE_RUNNING = "Image_Upload_Service_Running";

    public static void createImageUploadNotification(Context context,
                                                     String applicationId,
                                                     String message,
                                                     boolean progress,
                                                     PendingIntent pendingIntent,
                                                     String subText) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        Log.i("ankur", "applicationId = " + applicationId);

        mBuilder.setContentTitle("Loan Application ID: " + applicationId)
                .setContentText(message)
                //.setProgress(0, 0, progress)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                //.setOngoing(progress)
                .setTicker(message);

        if (pendingIntent != null)
            mBuilder.setContentIntent(pendingIntent);

        if (!progress && subText != null)
            mBuilder.setSubText(subText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.ic_notification);
                    //.setColor(context.getResources().getColor(R.color.gaadi_blue));
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.valueOf(applicationId), mBuilder.build());
    }

    public static boolean checkInternetConnectivity(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(byte[] mydata) {
        ByteArrayInputStream in = new ByteArrayInputStream(mydata);
        ObjectInputStream is;
        try {
            is = new ObjectInputStream(in);
            return is.readObject();
        } catch (StreamCorruptedException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    public static void setBooleanSharedPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        IMAGE_UPLOAD_PREF,
                        Context.MODE_PRIVATE
                );
        SharedPreferences.Editor editor = preferences.edit();
        if ((key != null) && !key.isEmpty()) {
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public static boolean getBooleanSharedPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        IMAGE_UPLOAD_PREF,
                        Context.MODE_PRIVATE
                );

        if (preferences.contains(key)) {
            return preferences.getBoolean(key, defaultValue);
        } else {
            return defaultValue;
        }
    }
}
