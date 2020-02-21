package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import type.CreateTaskInput;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.CreateTaskmasterMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.lang.reflect.Array;

import javax.annotation.Nonnull;

public class AddTask extends AppCompatActivity {

    private AWSAppSyncClient mAWSAppSyncClient;
    private RecyclerView recyclerView;
    private MyTaskRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();



        // Team Spinner
//        String[] testArray = new String[]{"Red", "Amber", "Silver"};
//        Spinner teamspinner = (Spinner) findViewById(R.id.team_spinner);
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, testArray );
//        Log.i("haitle16.AddTask", spinnerArrayAdapter.toString());


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

        CreateTaskInput input = CreateTaskInput.builder()
                .title(taskTitle)
                .body(taskBody)
                .state(state)
                .build();
        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(input).build())
                .enqueue(new GraphQLCall.Callback<CreateTaskMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response) {
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
