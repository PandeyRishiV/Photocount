package com.application.photocount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //Global Variables
    RelativeLayout progress,splash;
    ImageButton capture;
    SharedPreferences sp;
    LinearLayout main;
    TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();

        //Init Widgets
        final CameraView camera = findViewById(R.id.camera);
        progress = findViewById(R.id.progress);
        splash = findViewById(R.id.splash);
        capture = findViewById(R.id.capture);
        count = findViewById(R.id.count);
        main = findViewById(R.id.main);

        // Set/GET SharedPreference
        sp = getSharedPreferences("CountPreference",MODE_PRIVATE);
        if(!sp.contains("Counter"))
        {
            SharedPreferences.Editor e = sp.edit();
            e.putInt("Counter",0);
            e.commit();
        }
        else
        {
            int currentnumber=sp.getInt("Counter",-1);
            count.setText(currentnumber+"");
        }


        //initialize camera
        camera.setLifecycleOwner(this);
        camera.setMode(Mode.PICTURE);

        //camera listener
        camera.addCameraListener(new CameraListener()
        {
            @Override
            public void onPictureTaken(PictureResult result)
            {
                // Set File Location
                File location = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Photo Count");
                if(!location.exists()) { location.mkdir(); }

                //create file image and add picture into directory
                Date d = new Date();
                long time= d.getTime();
                String photoname = "PC"+time+".jpg";
                File newpic = new File(location.getPath(),photoname);
                byte[] data = result.getData();
                try
                {
                    FileOutputStream fos = new FileOutputStream(newpic.getPath());
                    fos.write(data);
                    fos.close();

                    //Refresh and add picture to gallery
                    MediaScannerConnection.scanFile(MainActivity.this,
                            new String[] { newpic.toString() }, null,
                            new MediaScannerConnection.OnScanCompletedListener()
                            {
                                public void onScanCompleted(String path, Uri uri)
                                {}
                            });
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
                //stops progress bar
                progress.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });

        //capture button
        capture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //loads progress bar and disable background
                progress.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                //Update SharedPreference
                sp = getSharedPreferences("CountPreference",MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                int currentnum= sp.getInt("Counter",-1);
                e.putInt("Counter",currentnum+1);
                e.apply();

                //Update text
                count.setText(String.valueOf(currentnum+1));
                camera.takePicture();
            }
        });

    }

    //Ask for storage permission
    public void isStoragePermissionGranted()
    {

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {}
            else
                {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
