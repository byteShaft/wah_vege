package com.byteshaft.wahwege;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.account.ResetPassword;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Calendar;

/**
 * Created by husnain on 10/31/17.
 */

public class SuggestionsComplains extends Fragment implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener{

    private View mBaseView;
    private EditText mComplainsEditText;
    private EditText mSummaryComplainsEditText;

    private String mComplainsEditTextString;
    private String mSummaryComplainsEditTextString;
    private Button mSendButton;

    private HttpRequest request;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.suggestions, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Suggestions");
        setHasOptionsMenu(true);
        mComplainsEditText = mBaseView.findViewById(R.id.complains_edit_text);
        mSummaryComplainsEditText = mBaseView.findViewById(R.id.summary_complains_edit_text);
        mSendButton = mBaseView.findViewById(R.id.send_button);
        mComplainsEditText.setOnClickListener(this);
        mSendButton.setOnClickListener(this);

        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_button:
                mComplainsEditTextString =  mComplainsEditText.getText().toString();
                mSummaryComplainsEditTextString =  mSummaryComplainsEditText.getText().toString();
                submitComplain(mSummaryComplainsEditTextString, mComplainsEditTextString);

                break;
        }
    }

    private void submitComplain(String summary, String text) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%ssuggestions/", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getComplainData(summary, text));
        Helpers.showProgressDialog(getActivity(), "Submitting Suggestions...");
    }


    private String getComplainData(String summary, String text) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("summary", summary);
            jsonObject.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(getView(), getString(R.string.connection_time_out));
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                break;
        }

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Suggestions Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseText() + "working ");
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        Toast.makeText(getActivity(), "Request submitted successfully", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
