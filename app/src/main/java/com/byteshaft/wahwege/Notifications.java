package com.byteshaft.wahwege;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.adapters.NotificationsAdapter;
import com.byteshaft.wahwege.utils.AppGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 10/31/17.
 */

public class Notifications extends Fragment {

    private View mBaseView;
    private ListView mNotificationsListView;
    private NotificationsAdapter notificationsAdapter;
    private ArrayList<com.byteshaft.wahwege.gettersetter.Notifications> notificationsArrayList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_notifications, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Promotions");
        setHasOptionsMenu(true);
        mNotificationsListView = mBaseView.findViewById(R.id.notifications_list_view);
        notificationsArrayList = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(getActivity(),notificationsArrayList);
        mNotificationsListView.setAdapter(notificationsAdapter);
        getPromotionsMessages();
        return mBaseView;
    }

    private void getPromotionsMessages() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        System.out.println("Test " + jsonArray.getJSONObject(i));
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        com.byteshaft.wahwege.gettersetter.Notifications notifications = new com.byteshaft.wahwege.gettersetter.Notifications();
                                        notifications.setNotificationsMessageBody(jsonObject.getString("text"));
                                        notifications.setNotificationsTime(jsonObject.getString("created_at"));
                                        notificationsArrayList.add(notifications);
                                    }
                                    notificationsAdapter = new NotificationsAdapter(getActivity(), notificationsArrayList);
                                    mNotificationsListView.setAdapter(notificationsAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%spromotions", AppGlobals.BASE_URL));
        getStateRequest.send();
    }
}
