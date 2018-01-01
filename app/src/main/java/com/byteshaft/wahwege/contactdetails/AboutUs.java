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
import com.byteshaft.wahwege.adapters.FaqsAdapter;
import com.byteshaft.wahwege.utils.AppGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 10/31/17.
 */

public class AboutUs extends Fragment {

    private View mBaseView;
    private TextView aboutUsText;
    private TextView tvAboutUs;
    private HttpRequest request;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.activity_about_us, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("About Us");
        setHasOptionsMenu(true);
        aboutUsText = mBaseView.findViewById(R.id.about_us_text);
        tvAboutUs = mBaseView.findViewById(R.id.title_about_us);
        aboutUsText.setTypeface(AppGlobals.typefaceNormal);
        tvAboutUs.setTypeface(AppGlobals.typefaceNormal);

        aboutUsText.setMovementMethod(new ScrollingMovementMethod());
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
                                    aboutUsText.setText(aboutUS);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }
                }
            }
        });
        request.open("GET", String.format("%sabout-us", AppGlobals.BASE_URL));
        request.send();

        return mBaseView;
    }
}
