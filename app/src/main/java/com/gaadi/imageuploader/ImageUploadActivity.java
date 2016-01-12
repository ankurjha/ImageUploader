package com.gaadi.imageuploader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class ImageUploadActivity extends AppCompatActivity {

    public static String Path = "/storage/emulated/0/Pictures//upload_lib/upload_lib_pic_";

    public static String END_POINT_URL = "http://beta.usedcarsin.in/wm_v2/webapis/evaluation";

    //public static String fileName = "upload_lib_pic_";

    //private static ImageUploadeDatabaseHelper imageUploadeDB;

    ArrayList<ImageUploadRequestData> imageUploadRequestDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // imageUploadeDB = new ImageUploadeDatabaseHelper(this);

        imageUploadRequestDataList = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startImageUploadService();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /*public static synchronized ImageUploadeDatabaseHelper getImageUploadeDB() {
        return imageUploadeDB;
    }*/

    public void startImageUploadService(){

        Intent intent = new Intent(ImageUploadActivity.this, ImageUploadServiceSyncronus.class);
        imageUploadRequestDataList.clear();
        for(int i=1;i<=10;i++){
            //imageUploadeDB.insertImage(new ImageModel(i,i,Path+i+".jpg",0));
            imageUploadRequestDataList.add(new ImageUploadRequestData("U3KqyrewdMuCotTS","json","qprw4MbH387iq8zazt3ck8bh0g",123456,12345,"10","2","abc"+i+".jpg","app",Path+i+".jpg"));
        }

        intent.putExtra(ImageUploadUtils.IMAGE_UPLOAD_REQUEST_DATA_LIST, imageUploadRequestDataList);
        intent.putExtra(ImageUploadUtils.END_POINT_URL,END_POINT_URL);
        intent.putExtra(ImageUploadUtils.IMAGE_UPLOAD_SERVICE_CALLED_FROM,ImageUploadUtils.FROM_IMAGE_UPLOAD_ACTIVITY);

        startService(intent);

        //int size = imageUploadeDB.getImagesCount();
        //Log.d("ankur: ", "size : "+size);

        // Reading all contacts
        //Log.d("ankur: ", "Reading all contacts..");
        //ArrayList<ImageModel> contacts = imageUploadeDB.getAllImages();

        //msgIntent.putExtra("filePath", contacts);//("filePath", contacts);
        //msgIntent.putExtra("fileName", name);


        /*for (ImageModel cn : contacts) {
            String log = "Id: " + cn.get_id() + " ,Ref Id: " + cn.getRef_id() + " ,Path: " + cn.getPath()+" ,try count : "+cn.getTry_count();
            String name = cn.getPath();
            int index = name.lastIndexOf('/');
            name = name.substring(index+1);
            Log.d("ankur", "name : " + name);
            // Writing Contacts to log
            Log.d("ankur", log);



            *//*msgIntent.putExtra("filePath",cn.getPath() );
            msgIntent.putExtra("fileName", name);
            startService(msgIntent);*//*
        }*/



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
