package com.example.detector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.detector.adapter.MainMessageAdapter;
import com.example.detector.model.MainMessage;

public class SubActivity extends AppCompatActivity {

    public static final String TAG = "SUB_ACTIVITY";

    private RecyclerView recyclerView;
    private MainMessageAdapter adapter;
    private LinearLayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        MainMessage message = (MainMessage) intent.getSerializableExtra("item");

        recyclerView = (RecyclerView) findViewById(R.id.sub_rv);
        lManager = new LinearLayoutManager(this);
        lManager.setReverseLayout(true);
        lManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(lManager);
        adapter = new MainMessageAdapter(this);

        recyclerView.setAdapter(adapter);

        adapter.setItem(message);
    }
}