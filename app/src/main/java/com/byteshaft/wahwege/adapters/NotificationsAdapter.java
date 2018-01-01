package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by husnain on 12/6/17.
 */

public class NotificationsAdapter extends ArrayAdapter<String> {

     private ViewHolder viewHolder;
     private ArrayList<Notifications> arrayList;
     private Activity activity;


    public NotificationsAdapter(Activity activity, ArrayList<Notifications> arrayList) {
        super(activity.getApplicationContext(), R.layout.delegate_notifications);
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView =   activity.getLayoutInflater().inflate(R.layout.delegate_notifications,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.notificationsTextView = convertView.findViewById(R.id.notifications_text_view);
            viewHolder.notificationsTime = convertView.findViewById(R.id.notification_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Notifications notifications = arrayList.get(position);
        viewHolder.notificationsTextView.setText(notifications.getNotificationsMessageBody());
        String input = notifications.getNotificationsTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            viewHolder.notificationsTime.setText(df.format(format.parse(input)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {
        TextView notificationsTextView;
        TextView notificationsTime;
    }
}
