package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import type.CreateTeamInput;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTeamMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "haitle16.main";
    private AWSAppSyncClient mAWSAppSyncClient;
//    private MyTaskRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        // Team Spinner
//        String[] testArray = new String[]{};
//        Spinner teamspinner = (Spinner) findViewById(R.id.team_spinner);
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testArray );
//        Log.i("haitle16.AddTask", spinnerArrayAdapter.toString());
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        if(spinnerArrayAdapter == null) {
//            spinnerArrayAdapter.add("Red");
//            spinnerArrayAdapter.add("Amber");
//            spinnerArrayAdapter.add("Silver");
//            teamspinner.setAdapter(spinnerArrayAdapter);
//        }






//        mAWSAppSyncClient.query(ListTeamsQuery.builder().build())
//                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
//                .enqueue(new GraphQLCall.Callback<ListTeamsQuery.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
//
//
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//
//                    }
//                });


//        Log.i("haitle16.AddTask", adapter.toString());




//        CreateTeamInput teamRedinput = CreateTeamInput.builder()
//                .name("Red")
//                .build();
//
//        CreateTeamInput teamAmberinput = CreateTeamInput.builder()
//                .name("Amber")
//                .build();
//
//        CreateTeamInput teamSilverinput = CreateTeamInput.builder()
//                .name("Silver")
//                .build();
//        mAWSAppSyncClient.mutate(CreateTeamMutation.builder().input(teamRedinput).build())
//                .enqueue(new GraphQLCall.Callback<CreateTeamMutation.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<CreateTeamMutation.Data> response) {
//                        Log.i("haitle16.main", response.data().toString());
//
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//                        Log.e("Error", e.toString());
//
//                    }
//                });
//        mAWSAppSyncClient.mutate(CreateTeamMutation.builder().input(teamAmberinput).build())
//                .enqueue(new GraphQLCall.Callback<CreateTeamMutation.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<CreateTeamMutation.Data> response) {
//                        Log.i("haitle16.main", response.data().toString());
//
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//                        Log.e("Error", e.toString());
//
//                    }
//                });
//        mAWSAppSyncClient.mutate(CreateTeamMutation.builder().input(teamSilverinput).build())
//                .enqueue(new GraphQLCall.Callback<CreateTeamMutation.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<CreateTeamMutation.Data> response) {
//                        Log.i("haitle16.main", response.data().toString());
//
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//                        Log.e("Error", e.toString());
//
//                    }
//                });



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

//        ViewGroup btnLayout = (ViewGroup) findViewById(R.id.task_container_buttons);
//        for(int i = 0; i < btnLayout.getChildCount(); i++) {
//            View child = btnLayout.getChildAt(i);
//            if(child instanceof Button) {
//                Button button = (Button) child;
//                button.setOnClickListener((v) -> {
//                    Intent toviewdetail = new Intent(this, TaskDetail.class);
//                    toviewdetail.putExtra("taskName", button.getText().toString());
//                    startActivity(toviewdetail);
//                });
//            }
//        }



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
