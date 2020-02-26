package com.haitle16.taskmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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

import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.haitle16.taskmaster.dummy.DummyContent.DummyItem;

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
    List<ListTasksQuery.Item> teamTaskList = new LinkedList<>();
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        String teamID = teamNameID.get(teamName);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        mAWSAppSyncClient.query(ListTeamsQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(new GraphQLCall.Callback<ListTeamsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
                        Log.i("haitle16.TaskFragment", "we got team Data name id"+ response.data().listTeams().items());

                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });


        String teamID = sharedPreferences.getString("teamSelectedID", "bf5c6069-babd-4ae8-9ba0-444689581d4d"); // default team silver

        mAWSAppSyncClient.query(GetTeamQuery.builder()
        .id(teamID)
        .build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(new GraphQLCall.Callback<GetTeamQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<GetTeamQuery.Data> response) {
                        List<GetTeamQuery.Item> specificTeamTask = response.data().getTeam().tasks().items();
//                        LinkedList<Task> appTasks = new LinkedList<>();
                        //  TODO: if the task gotten from DB is not null DO
                        for(GetTeamQuery.Item i : specificTeamTask) {
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

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
                                String userPreferredTeamName = sharedPreferences.getString("teamSelected", "Silver");

                                String teamID = sharedPreferences.getString("teamSelectedID", ""); // default team silver
                                Log.i("haitle16.TaskFragment", "what kind of response: "+response.data().listTasks().items());

                                for(ListTasksQuery.Item task : response.data().listTasks().items()) {
                                    if(task.teamID().equals(teamID) && !teamTaskList.contains(task)) {
                                        teamTaskList.add(task);
                                    }
                                }
                                Log.i("haitle16.TaskFragment", "teamTaskList data:  "+teamTaskList);

                                // Setting the adapter with the team's task list. ( Maybe find a way to check if its home page then set team's task, and set all tasks on all task page.
//                                adapter.setItems(response.data().listTasks().items());
                                adapter.setItems(teamTaskList);
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
