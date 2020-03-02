package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AWSAppSyncClient mAWSAppSyncClient;
    List<ListTeamsQuery.Item> allTeams = new LinkedList<>();
    private Hashtable<String, String> teamNameID = new Hashtable<>();



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
                        allTeams.addAll(response.data().listTeams().items());

                        LinkedList<String> teamList = new LinkedList<>();
                        for(ListTeamsQuery.Item team : allTeams) {
                            teamList.add(team.name());
                            teamNameID.put(team.name(), team.id());
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
                        Log.i("haitle16.Settings", "failed setting team spinner" + e.toString());

                    }
                });







        View savebtn = findViewById(R.id.saveSettingbtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting the spinner inputs.
                Spinner teamspinner = findViewById(R.id.setting_team_spinner);
                TextView usernameinput = findViewById(R.id.usernameinput);

                SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = storage.edit();
                editor.putString("username",usernameinput.getText().toString());
                editor.putString("teamSelected", teamspinner.getSelectedItem().toString());
                editor.putString("teamSelectedID", teamNameID.get(teamspinner.getSelectedItem()));
                editor.apply();
                Log.i("haitle16.Settings", "This is the team selected "+teamspinner.getSelectedItem().toString());


                Toast toast = Toast.makeText(Settings.this,
                        "User information saved!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        View signoutbtn = findViewById(R.id.signoutbtn);
        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AWSMobileClient.getInstance().signOut();
                finish();

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String teamName = allTeams.get(position).id();
        Log.i("haitle16.SelectedItem", teamName);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
