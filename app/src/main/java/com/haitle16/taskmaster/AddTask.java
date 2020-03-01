package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import type.CreateTaskInput;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.CreateTaskmasterMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class AddTask extends AppCompatActivity {

    private AWSAppSyncClient mAWSAppSyncClient;
    private RecyclerView recyclerView;
//    private SpinAdapter adapter;
    private Hashtable<String, String> teamNameID = new Hashtable<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        // Team selection spinner on AddTask Page
        mAWSAppSyncClient.query(ListTeamsQuery.builder().build())
        .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
        .enqueue(new GraphQLCall.Callback<ListTeamsQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
                List<ListTeamsQuery.Item> allTeams = new LinkedList<>();
                allTeams.addAll(response.data().listTeams().items());

                LinkedList<String> teamList = new LinkedList<>();
                for(ListTeamsQuery.Item team : allTeams) {
                    teamList.add(team.name());
                    teamNameID.put(team.name(), team.id());

                }
                Log.i("haitle16.AddTask", teamList.toString());

                Spinner teamspinner = findViewById(R.id.team_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTask.this, android.R.layout.simple_spinner_item, teamList);

                // Running on another thread instead of UI.
                Handler h = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message input) {
                        teamspinner.setAdapter(adapter);
                    }
                };
                h.obtainMessage().sendToTarget();


            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.i("haitle16", "failed setting team spinner");

            }
        });


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
        Spinner teamspinner = findViewById(R.id.team_spinner);
        String teamName = teamspinner.getSelectedItem().toString();
        String teamID = teamNameID.get(teamName);

//        //Getting user preferred team in setting and saving it to teamID
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//
//        String userPreferredTeamName = sharedPreferences.getString("teamSelected", "Red");
//        String userTeamID = teamNameID.get(userPreferredTeamName);
//
//        SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = storage.edit();
//        editor.putString("userteamID", userTeamID);
//        editor.apply();



        // need to create a state
        String state = "new";
        Log.i("haitle16.addTask", "It gets in addatask");

        CreateTaskInput input = CreateTaskInput.builder()
                .title(taskTitle)
                .body(taskBody)
                .teamID(teamID)
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

    // Hide keyboard feature when clicked outside of input referenced from https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
