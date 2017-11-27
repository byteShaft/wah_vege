package com.byteshaft.wahwege.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;


public class CodeConfirmationActivity extends AppCompatActivity implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener{

    private Button mVerifyButton;
    private EditText mMobileNumberEditText;
    private String email;
    private EditText mConfirmationCodeEditText;
    private HttpRequest request;
    private SmsListener smsListener;
    private TextView mResendVerificationTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.confirmation_code_activity);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        mMobileNumberEditText = (EditText) findViewById(R.id.mobile_number_edit_text);
        mConfirmationCodeEditText = (EditText) findViewById(R.id.confirmation_code_edit_text);
        mVerifyButton = (Button) findViewById(R.id.verify_button);
        mResendVerificationTextView = (TextView) findViewById(R.id.resend_verification_text_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mMobileNumberEditText.setTypeface(AppGlobals.typefaceNormal);
        mVerifyButton.setTypeface(AppGlobals.typefaceNormal);
        mVerifyButton.setOnClickListener(this);
        mResendVerificationTextView.setOnClickListener(this);

        String number = getIntent().getStringExtra("mobile_number");
        if (!AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER).equals("") ||
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL).equals("")) {
            mMobileNumberEditText.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER));
            email = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
        }

        smsListener = new SmsListener();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsListener, filter);
        progressBar.setVisibility(View.GONE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resend_verification_text_view:
                resendOtp(email);
                break;
            case R.id.verify_button:
                String smsOtp = mConfirmationCodeEditText.getText().toString();
                activateUser(email, smsOtp);
                break;
        }

    }

    private void resendOtp(String email) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_BAD_REQUEST:
                                break;
                            case HttpURLConnection.HTTP_OK:
                                if (progressBar.getVisibility() == View.VISIBLE) {
                                    progressBar.setVisibility(View.GONE);
                                }
                                Toast.makeText(getApplicationContext(), "Sms sent", Toast.LENGTH_SHORT).show();
                        }
                }

            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%srequest-activation-key", AppGlobals.BASE_URL));
        request.send(getOtpData(email));
        Helpers.showProgressDialog(CodeConfirmationActivity.this, "Resending Sms");
    }

    private String getOtpData(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsListener != null) {
            unregisterReceiver(smsListener);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        Toast.makeText(getApplicationContext(), "Please enter correct account activation key", Toast.LENGTH_LONG).show();
                        break;
                    case HttpURLConnection.HTTP_OK:
                        if (progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String username = jsonObject.getString(AppGlobals.KEY_USER_NAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_MOBILE_NUMBER);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);

                            Log.i("TAG", "Token "+ token);
                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, username);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER, phoneNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            RegisterActivity.getInstance().finish();
                            AppGlobals.loginState(true);
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }


    private void activateUser(String email, String emailOtp) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sactivate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, emailOtp));
        Helpers.showProgressDialog(CodeConfirmationActivity.this, "Activating User");
    }


    private String getUserActivationData(String email, String emailOtp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("sms_otp", emailOtp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        System.out.println(request.getStatus());
        switch (request.getStatus()) {

        }
    }

    public class SmsListener extends BroadcastReceiver {

        private SharedPreferences preferences;

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.i("TAG",  " called ");
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null) {
                    //---retrieve the SMS message received---
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            Log.i("TAG", msgBody + " From " + msg_from);
                            String[] message = msgBody.split(":");
                            Log.i("TAG", message[1].trim());
                            mConfirmationCodeEditText.setText(message[1].trim());

                        }
                    } catch (Exception e) {
                            Log.d("Exception caught",e.getMessage());
                    }
                }
            }
        }
    }
}
