package com.byteshaft.wahwege.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.SectorAdapter;
import com.byteshaft.wahwege.gettersetter.Sector;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;


public class ResetPassword extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private Button mResetButton;
    private EditText mEmail;
    private EditText mEmailOtp;
    private EditText mNewPassword;


    private String mEmailAddressString;
    private String mEmailOtpString;
    private String mNewPasswordString;

    private HttpRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reset_password);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        mEmail = findViewById(R.id.email_edit_text);
        mEmailOtp = findViewById(R.id.email_otp);
        mNewPassword = findViewById(R.id.new_password);
        mResetButton = findViewById(R.id.reset_button);

        mEmail.setTypeface(AppGlobals.typefaceNormal);
        mEmailOtp.setTypeface(AppGlobals.typefaceNormal);
        mNewPassword.setTypeface(AppGlobals.typefaceNormal);

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEditText()) {
                    System.out.println("working");
                    changePassword(mEmailAddressString, mEmailOtpString, mNewPasswordString);
                }
            }
        });
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailAddressString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
    }

    private boolean validateEditText() {

        boolean valid = true;
        mNewPasswordString = mNewPassword.getText().toString();
        mEmailOtpString = mEmailOtp.getText().toString();
        mEmailAddressString = mEmail.getText().toString();

        if (mNewPasswordString.trim().isEmpty() || mNewPasswordString.length() < 3) {
            mNewPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mNewPassword.setError(null);
        }

        if (mEmailOtpString.trim().isEmpty() || mEmailOtpString.length() < 4) {
            mEmailOtp.setError("enter at least 4 characters");
            valid = false;
        } else {
            mEmailOtp.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        return valid;
    }

    private void changePassword(String email, String emailotp, String newpassword) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%schange-password", AppGlobals.BASE_URL));
        request.send(getUserChangePassword(email, emailotp, newpassword));
        Helpers.showProgressDialog(ResetPassword.this, "Resetting your password");
    }


    private String getUserChangePassword(String email, String emailotp, String newpassword) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", emailotp);
            jsonObject.put("new_password", newpassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onError(HttpRequest request, int readyStat, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(ResetPassword.this, "Resetting Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(ResetPassword.this, "Resetting Failed!", "old Password is wrong");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(ResetPassword.this, "Your password successfully changed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

}
