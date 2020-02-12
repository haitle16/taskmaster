package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "haitle16.main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        View addtaskbtn = findViewById(R.id.addtaskbtn);
        addtaskbtn.setOnClickListener((v) -> {
            Intent toaddtaskpage = new Intent(this, AddTask.class);
            startActivity(toaddtaskpage);
        });

        View alltaskbtn = findViewById(R.id.alltasksbtn);
        alltaskbtn.setOnClickListener((v) -> {
            Intent toalltaskpage = new Intent(this, AllTasks.class);
            startActivity(toalltaskpage);
        });

        View settingbtn = findViewById(R.id.settingbtn);
        settingbtn.setOnClickListener((v) -> {
            Intent tosettingpage = new Intent(this, Settings.class);
            startActivity(tosettingpage);
        });

//        View taskbtn1 = findViewById(R.id.taskbtn1);
//        taskbtn1.setOnClickListener((v) -> {
//            Intent toviewdetail = new Intent(this, TaskDetail.class);
//            Button btn1 = (Button)findViewById(R.id.taskbtn1);
//            toviewdetail.putExtra("taskName", btn1.getText().toString());
//            startActivity(toviewdetail);
//        });

        ViewGroup btnLayout = (ViewGroup) findViewById(R.id.task_container_buttons);
        for(int i = 0; i < btnLayout.getChildCount(); i++) {
            View child = btnLayout.getChildAt(i);
            if(child instanceof Button) {
                Button button = (Button) child;
                button.setOnClickListener((v) -> {
                    Intent toviewdetail = new Intent(this, TaskDetail.class);
                    toviewdetail.putExtra("taskName", button.getText().toString());
                    startActivity(toviewdetail);
                });
            }
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "resumed");

        TextView usertasks = findViewById(R.id.mytaskhpage);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nameinput = sharedPreferences.getString("username", "My Tasks");
        if(nameinput != "My tasks") {
            usertasks.setText(nameinput+"'s Tasks");
        }


        // make api call to get fresh data
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "paused");
        // stop inprogress calls to api, dont care about those results nomore
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "stopped");
    }
}
