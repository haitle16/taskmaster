package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import type.CreateTaskmasterInput;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskmasterMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

public class AddTask extends AppCompatActivity {

    private AWSAppSyncClient mAWSAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        // Triggering the toast after a task is submitted
        View taskSubmitted = findViewById(R.id.addtasksubmitbtn);
        taskSubmitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(AddTask.this,
                        "The task is submitted!",
                        Toast.LENGTH_SHORT);
                toast.show();
                addatask(v);
            }
        });
    }

    public void addatask(View v) {
        EditText taskTitleText = findViewById(R.id.addATaskTitle);
        EditText taskBodyText = findViewById(R.id.addATaskDescription);
        String taskTitle = taskTitleText.getText().toString();
        String taskBody = taskBodyText.getText().toString();
        // need to create a state
        String state = "new";
        Log.i("haitle16.addTask", "It gets in addatask");

        CreateTaskmasterInput input = CreateTaskmasterInput.builder()
                .title(taskTitle)
                .body(taskBody)
                .state(state)
                .build();
        mAWSAppSyncClient.mutate(CreateTaskmasterMutation.builder().input(input).build())
                .enqueue(new GraphQLCall.Callback<CreateTaskmasterMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<CreateTaskmasterMutation.Data> response) {
                        Log.i("haitle16.addTask", response.data().toString());
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
//                        Log.e("Error", e.toString());
                        Log.w("haitle16.addTask", "failure");
                    }
                });


    }
}
