package com.byteshaft.wahwege.contactdetails;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.FaqsAdapter;
import com.byteshaft.wahwege.gettersetter.Faqs;
import com.byteshaft.wahwege.utils.AppGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 10/31/17.
 */

public class Faq extends Fragment {

    private View mBaseView;
    private ListView mFaqsListView;
    private ArrayList<Faqs> arrayList;
    private FaqsAdapter faqsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.activity_faq, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("FAQs");
        setHasOptionsMenu(true);
        mFaqsListView = mBaseView.findViewById(R.id.faqs_list_view);
        arrayList = new ArrayList<>();
        faqsAdapter = new FaqsAdapter(getActivity(), arrayList);
        getFaqs();
        return mBaseView;
    }


    private void getFaqs() {
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
                                        Faqs faqs = new Faqs();
                                        faqs.setFaqsAnswer(jsonObject.getString("answer"));
                                        faqs.setFaqsQestion(jsonObject.getString("question"));
                                        arrayList.add(faqs);
                                    }
                                    faqsAdapter = new FaqsAdapter(getActivity(), arrayList);
                                    mFaqsListView.setAdapter(faqsAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%sfaqs", AppGlobals.BASE_URL));
        getStateRequest.send();
    }
}
