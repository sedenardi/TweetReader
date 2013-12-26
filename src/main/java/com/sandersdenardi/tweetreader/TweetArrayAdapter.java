package com.sandersdenardi.tweetreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TweetArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> values;

    public TweetArrayAdapter(Context context, List<String> objects) {
        super(context, R.layout.simple_item, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.simple_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.tweetText);
        textView.setText(values.get(position));
        return rowView;
    }
}