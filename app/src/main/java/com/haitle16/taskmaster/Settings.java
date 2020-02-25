package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class Settings extends AppCompatActivity {

    private AWSAppSyncClient mAWSAppSyncClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
            .context(getApplicationContext())
            .awsConfiguration(new AWSConfiguration(getApplicationContext()))
            .build();

        // Team Selection on setting page.
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
                        }
                        Spinner teamspinner = findViewById(R.id.setting_team_spinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_item, teamList);

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
                        Log.i("haitle16.Settings", "failed setting team spinner");

                    }
                });




        TextView usernameinput = findViewById(R.id.usernameinput);



        View savebtn = findViewById(R.id.saveSettingbtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = storage.edit();
                editor.putString("username",usernameinput.getText().toString());
                editor.apply();
                Toast toast = Toast.makeText(Settings.this,
                        "User information saved!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
