package com.example.cob155cw;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class SessionListAdapter extends ArrayAdapter<SessionData> {     // Override adapter to custom
    private final Activity myContext;
    private final ArrayList<SessionData> data;
    private final ArrayList<SessionData> arraylist;

    public SessionListAdapter(Context context, int textViewResourceId,
                              ArrayList<SessionData> objects) {
        super(context, textViewResourceId, objects);
        myContext = (Activity) context;
        data = objects;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        if(convertView == null) {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item, null);
        }

        rowView = convertView;
        ImageView typeImageView = rowView
                .findViewById(R.id.postThumb);
        if (data.get(position).typeImage.equals("Gym")) {
            typeImageView.setImageResource(R.drawable.gym);
        }                                                               // Add different images based on type of session
        if (data.get(position).typeImage.equals("Track")) {
            typeImageView.setImageResource(R.drawable.track);
        }
        if (data.get(position).typeImage.equals("Long run")) {
            typeImageView.setImageResource(R.drawable.running);
        }
        if (data.get(position).typeImage.equals("Easy run")) {
            typeImageView.setImageResource(R.drawable.easyrun);
        }

        TextView postTitleView = rowView
                .findViewById(R.id.postTitleLabel);
        postTitleView.setText(data.get(position).sessionTitle);         // Add title to top row

        TextView postDateView = rowView
                .findViewById(R.id.postDateLabel);                      // Add date to second row
        postDateView.setText(data.get(position).sessionDate);

        return rowView;
    }

    public void filter(String charText) {                           // Custom filter method
        charText = charText.toLowerCase(Locale.getDefault());
        data.clear();                                           // Filtered list
        if (charText.length() == 0) {
            data.addAll(arraylist);                                 // Return all items back into list if nothing has been searched for
        }
        else
        {
            for (SessionData wp : arraylist) {
                if (wp.sessionTitle.toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);                                   // Add to filtered list if input is in any of the items
                }
            }
        }
        notifyDataSetChanged();
    }
}