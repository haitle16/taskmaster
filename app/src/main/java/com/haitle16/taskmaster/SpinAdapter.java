package com.haitle16.taskmaster;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinAdapter<T> extends ArrayAdapter<T> {

    private Context context;
    private List<T> values;

    public SpinAdapter(Context context, int textViewResourceId, List<T> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }


    public int getCount() {
        return values.size();
    }

    public T getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(values.toArray(new Object[values.size()])[position]
                .toString());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(values.toArray(new Object[values.size()])[position]
                .toString());

        return label;
    }




}

// Refferenced from https://stackoverflow.com/questions/1625249/android-how-to-bind-spinner-to-custom-object-list
