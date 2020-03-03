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
                downloadWithTransferUtility();
            }

            @Override
            public void onError(Exception e) {
                Log.e("haitle16.TaskDetail", "Initialization error.", e);
            }
        });

        ImageView image = findViewById(R.id.taskdetail_image);
        Picasso.with(getApplicationContext()).load("https://www.addictiveblogs.com/wp-content/uploads/2013/08/programmer_quotes_06.jpg").into(image);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//        String teamID = sharedPreferences.getString("teamSelectedID", "bf5c6069-babd-4ae8-9ba0-444689581d4d"); // default team silver
//
//        mAWSAppSyncClient.query(GetTeamQuery.builder()
//                .id(teamID)
//                .build())
//                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
//                .enqueue(new GraphQLCall.Callback<GetTeamQuery.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<GetTeamQuery.Data> response) {
//                        List<GetTeamQuery.Item> specificTeamTask = response.data().getTeam().tasks().items();
////                        LinkedList<Task> appTasks = new LinkedList<>();
//                        //  TODO: if the task gotten from DB is not null DO
//                        for(GetTeamQuery.Item i : specificTeamTask) {
//                            Log.i("haitle16.TaskFragment", "Task Title: " + i.title() + " | Task Body: " + i.body() + " | Task State: " + i.state() + " | Task's TeamID: " +i.teamID() + " | Task's UUID: " + i.imgPath());
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//                        Log.e("haitle16.TaskFragment1",e.toString());
//
//
//                    }
//                });




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

    private void downloadWithTransferUtility() {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        TransferObserver downloadObserver =
                transferUtility.download(
                        "public/e1614b25-16ff-428c-8113-bf110c1a1115",
                        new File(getApplicationContext().getFilesDir(), "e1614b25-16ff-428c-8113-bf110c1a1115"));

//        ImageView image = findViewById(R.id.taskdetail_image);
//        Picasso.with(getApplicationContext()).load("public/e1614b25-16ff-428c-8113-bf110c1a1115").into(image);

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d("Your Activity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == downloadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("Your Activity", "Bytes Transferred: " + downloadObserver.getBytesTransferred());
        Log.d("Your Activity", "Bytes Total: " + downloadObserver.getBytesTotal());
    }
}
