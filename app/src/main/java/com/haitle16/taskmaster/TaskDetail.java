package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);



    }

    @Override
    protected void onResume() {
        super.onResume();



        TextView taskDetailTitle = findViewById(R.id.taskdetail);
        String task1View = getIntent().getStringExtra("taskName");
        taskDetailTitle.setText(task1View);

    }
}
