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
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class ForgotPasswordActivity extends AppCompatActivity implements
        HttpRequest.OnErrorListener, HttpRequest.OnReadyStateChangeListener {

    private Button mRecoverButton;
    private EditText mEmail;
    private String mEmailString;
    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.forgot_password_activity);

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);

        mEmail = (EditText) findViewById(R.id.email_edit_text);
        mRecoverButton = (Button) findViewById(R.id.reset_button);

        mEmail.setTypeface(AppGlobals.typefaceNormal);
        mRecoverButton.setTypeface(AppGlobals.typefaceNormal);

        mRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    recoverUserPassword(mEmailString);
                }

            }
        });
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.
                matcher(mEmailString).matches()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        }
        return valid;
    }

    private void recoverUserPassword(String email) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sforgot-password", AppGlobals.BASE_URL));
        request.send(getUserPassword(email));
        Helpers.showProgressDialog(ForgotPasswordActivity.this, "Sending Recovery Mail");
    }


    private String getUserPassword(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        System.out.println(request.getStatus());
        switch (request.getStatus()) {

        }
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(ForgotPasswordActivity.this, "Recovery Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        AppGlobals.alertDialog(ForgotPasswordActivity.this, "Recovery Failed!", "provide a valid EmailAddress");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(getApplicationContext(), "Please check your Email for new password", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), ResetPassword.class));
                }
        }

    }
}
