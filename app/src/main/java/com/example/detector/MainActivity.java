package com.example.detector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detector.adapter.MainMessageAdapter;
import com.example.detector.model.MainMessage;
import com.example.detector.observer.ObservableObject;
import com.example.detector.request.RequestUrl;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements Observer {

    public static final String TAG = "MAIN_ACTIVITY";

    private RecyclerView recyclerView;
    private MainMessageAdapter adapter;
    private LinearLayoutManager lManager;

//    @BindView(R.id.recycler_view)
//    RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requiredPerms();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ObservableObject.getInstance().addObserver(this);

        recyclerView = findViewById(R.id.recycler_view);
        lManager = new LinearLayoutManager(this);
        lManager.setReverseLayout(true);
        lManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(lManager);
        adapter = new MainMessageAdapter(this);

        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requiredPerms() {
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permisionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if(permisionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        if(!Settings.canDrawOverlays(this)){
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(overlayIntent);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "update()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainMessage msg = (MainMessage) arg;
                adapter.setItem(msg);
            }
        });
    }

}