package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AllTasks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

    }

    public void goBack(View v) {
        View goBack = findViewById(R.id.goBackBtn);
//        Log.i("haitle16.AllTasks", goBack.getText().toString());
        this.finish();
    }
}
