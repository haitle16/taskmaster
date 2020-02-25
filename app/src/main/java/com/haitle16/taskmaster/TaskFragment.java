package com.haitle16.taskmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.amazonaws.amplify.generated.graphql.GetTaskmasterQuery;
import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.amplify.generated.graphql.ListTaskmastersQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.haitle16.taskmaster.dummy.DummyContent;
import com.haitle16.taskmaster.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TaskFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private AWSAppSyncClient mAWSAppSyncClient;
    private MyTaskRecyclerViewAdapter adapter;
//    private Hashtable<String, String> teamNameID = new Hashtable<>();



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TaskFragment newInstance(int columnCount) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // Connect to AWS
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getContext())
                .awsConfiguration(new AWSConfiguration(getContext()))
                .build();

//        // Getting all the teams from to populate the hash table to get the team id.
//        mAWSAppSyncClient.query(ListTeamsQuery.builder().build())
//                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
//                .enqueue(new GraphQLCall.Callback<ListTeamsQuery.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
//                        List<ListTeamsQuery.Item> allTeams = new LinkedList<>();
//                        allTeams.addAll(response.data().listTeams().items());
//                        for(ListTeamsQuery.Item team : allTeams) {
//                            teamNameID.put(team.name(), team.id());
//                        }
//                        Log.i("haitle16.TaskFragment", "Hashed the team and team id.");
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//                        Log.e("haitle16.TaskFragment", e.toString());
//
//                    }
//                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

//        // Connect to AWS
//        mAWSAppSyncClient = AWSAppSyncClient.builder()
//                .context(view.getContext().getApplicationContext())
//                .awsConfiguration(new AWSConfiguration(view.getContext().getApplicationContext()))
//                .build();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        String teamID = teamNameID.get(teamName);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        String teamID = sharedPreferences.getString("teamID", "d89aa2df-f3f3-4aff-9ed6-8edae9f5dbb2"); // default team silver

//        GetTeamQuery.builder().
        mAWSAppSyncClient.query(GetTeamQuery.builder()
        .id(teamID)
        .build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(new GraphQLCall.Callback<GetTeamQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<GetTeamQuery.Data> response) {
                        Log.i("haitle16.TaskFragment", "we got data!"+ response.data());
                        for(GetTeamQuery.Item i : response.data().getTeam().tasks().items()) {
                            Log.i("haitle16.TaskFragment", "Task Title: " + i.title() + " | Task Body: " + i.body() + " | Task State: " + i.state() + " | Task's TeamID: " +i.teamID());
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });



        mAWSAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(new GraphQLCall.Callback<ListTasksQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {

                        //Create a new handle to run this background thread.
                        Handler h = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message inputMessage) {
                                if(adapter == null) {
                                    adapter = new MyTaskRecyclerViewAdapter(null, mListener);
                                    recyclerView.setAdapter(adapter);
                                }
                                // Create a list for the team's task based on user's team preference and iterate to populate the list with its tasks and then set items to that list.
                                // Maybe find a way to see if its home page list only user team's task and on the all task page list all the tasks by setItems
                                
                                adapter.setItems(response.data().listTasks().items());
                                adapter.notifyDataSetChanged();
//                                recyclerView.setAdapter(new MyTaskRecyclerViewAdapter(response.data().listTaskmasters().items(), mListener));

                            }
                        };
                        // package up the handler and send it to the UI thread.
                        h.obtainMessage().sendToTarget();
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
