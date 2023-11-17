package com.can301.coursework.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.can301.coursework.R;

public class PrepareActivity extends AppCompatActivity {


    public static final String countLengthKey = "count_length";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        int length = getIntent().getIntExtra(countLengthKey,15);
        TextView textView = findViewById(R.id.prepare_count);
        Button btn = findViewById(R.id.cancel_prepare_btn);
        btn.setOnClickListener((view -> {
            finish();
        }));
        new Thread(()->{
            int prepareCount = getResources().getInteger(R.integer.prepare_count);
            int interval = 1;
            while(prepareCount>0){
                int count = prepareCount--;
                runOnUiThread(()->{
                    textView.setText(String.valueOf(count));
                });
                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            runOnUiThread(()->{
                textView.setText(String.valueOf(length));
            });

        }).start();


    }
}
