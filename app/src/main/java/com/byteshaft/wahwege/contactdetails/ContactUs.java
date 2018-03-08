package com.byteshaft.wahwege.contactdetails;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.utils.AppGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by husnain on 10/31/17.
 */

public class ContactUs extends Fragment {

    private TextView mContactUsText;
    private TextView mContactUs;
    private HttpRequest request;
    private View mBaseView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.activity_contact_us, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Contact Us");
        setHasOptionsMenu(true);
        mContactUsText = mBaseView.findViewById(R.id.title_contact_text);
        mContactUs = mBaseView.findViewById(R.id.title_about_us);

        mContactUsText.setMovementMethod(new ScrollingMovementMethod());
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    System.out.println(request.getResponseText());
                                    String aboutUS = jsonObject.getString("text");
                                    mContactUsText.setText(aboutUS);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }
                }
            }
        });
        request.open("GET", String.format("%scontact-us", AppGlobals.BASE_URL));
        request.send();
        return mBaseView;
    }
}
