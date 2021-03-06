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
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity {


    private AWSAppSyncClient mAWSAppSyncClient;
    private static final String TAG = "haitle16.main";
//    private MyTaskRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

//        // Connect to AWS
//        mAWSAppSyncClient = AWSAppSyncClient.builder()
//                .context(getApplicationContext())
//                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
//                .build();




        // Hard coding 3 teams.
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

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                        if(userStateDetails.getUserState().equals(UserState.SIGNED_OUT)) {
                            AWSMobileClient.getInstance().showSignIn(MainActivity.this, new Callback<UserStateDetails>() {
                                @Override
                                public void onResult(UserStateDetails result) {
                                    Log.d(TAG, "onResult: " + result.getUserState());

                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "onError: ", e);
                                }
                            });
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("INIT", "Initialization error.", e);
                    }
                }
        );


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
