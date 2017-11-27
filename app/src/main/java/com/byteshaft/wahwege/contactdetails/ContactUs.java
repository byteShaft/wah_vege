package com.byteshaft.wahwege.contactdetails;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

public class ContactUs extends Activity {

    private TextView mContactUsText;
    private TextView mContactUs;
    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        mContactUsText = findViewById(R.id.title_contact_text);
        mContactUs = (findViewById(R.id.title_about_us));

        mContactUsText.setTypeface(AppGlobals.typefaceNormal);
        mContactUs.setTypeface(AppGlobals.typefaceNormal);

        mContactUsText.setMovementMethod(new ScrollingMovementMethod());
        request = new HttpRequest(getApplicationContext());
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
    }
}
