package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        View taskSubmitted = findViewById(R.id.addtasksubmitbtn);
        taskSubmitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(AddTask.this,
                        "The task is submitted!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
