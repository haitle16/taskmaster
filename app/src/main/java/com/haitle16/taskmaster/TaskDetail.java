package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

public class TaskDetail extends AppCompatActivity {

    private AWSAppSyncClient mAWSAppSyncClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        // Initialize the AWSMobileClient if not initialized
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                Log.i("haitle16.TaskDetail", "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());
            }

            @Override
            public void onError(Exception e) {
                Log.e("haitle16.TaskDetail", "Initialization error.", e);
            }
        });


        // pulling from a imgURI for now.
        ImageView image = findViewById(R.id.taskdetail_image);
        String imagePath = getIntent().getStringExtra("imgPath");

//        Picasso.with(getApplicationContext()).load("https://www.addictiveblogs.com/wp-content/uploads/2013/08/programmer_quotes_06.jpg").into(image);
        Picasso.with(getApplicationContext()).load("https://taskmasterbucket92011-taskenv.s3-us-west-2.amazonaws.com/public/"+imagePath).into(image);







    }

    @Override
    protected void onResume() {
        super.onResume();



        TextView taskDetailTitle = findViewById(R.id.taskdetail);
        String task1View = getIntent().getStringExtra("taskName");
        taskDetailTitle.setText(task1View);

        TextView taskDetailState = findViewById(R.id.taskdetail_state);
        String taskState = getIntent().getStringExtra("taskState");
        taskDetailState.setText(taskState);

        TextView taskDetailBody = findViewById(R.id.taskdetail_body);
        String taskBody = getIntent().getStringExtra("taskBody");
        taskDetailBody.setText(taskBody);

    }
}
