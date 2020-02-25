package com.haitle16.taskmaster;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListTaskmastersQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.haitle16.taskmaster.TaskFragment.OnListFragmentInteractionListener;
import com.haitle16.taskmaster.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTaskRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecyclerViewAdapter.ViewHolder> {

    static final String TAG = "haitle16.ViewAdapter";
    private List<ListTasksQuery.Item> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyTaskRecyclerViewAdapter(List<ListTasksQuery.Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title());
        holder.mBodyView.setText(mValues.get(position).body());
        holder.mStateView.setText(mValues.get(position).state());

        // trying to change the background color of the whole mView to the team's color.
//        holder.mTeamView.setText(mValues.get(position).);
//        holder.mStateView.setBackgroundColor(Color.rgb(220,20,60)); // sett'ed state to be red color.
//        holder.mView.setBackgroundColor(Color.rgb(240,128,128));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }

                // consider passing body and state into the task details page
                Intent toviewdetail = new Intent(v.getContext(), TaskDetail.class);
                toviewdetail.putExtra("taskName", holder.mItem.title());
                v.getContext().startActivity(toviewdetail);
                Log.i(TAG, "task holder was clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setItems(List<ListTasksQuery.Item> items) {
        this.mValues = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mBodyView;
        public final TextView mStateView;
        public final TextView mTeamView;

        public ListTasksQuery.Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mBodyView = (TextView) view.findViewById(R.id.body);
            mStateView = (TextView) view.findViewById(R.id.state);
            mTeamView = (TextView) view.findViewById(R.id.teamName);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBodyView.getText() + "'";
        }
    }
}
