package com.byteshaft.wahwege.account;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.SectorAdapter;
import com.byteshaft.wahwege.gettersetter.Sector;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener, OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private Button mRegisterButton;
    private EditText mFullNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private EditText mLandlineEditText;
    private EditText mAddressEditText;
    private String mSectorSpinnerValueString;
    private String mLandlineEditTextString;
    private String mUserNameEditTextString;
    private String mAddressEditTextString;
    private String mFullNameString;
    private String mEmailAddressString;
    public String mPhoneNumberString;
    private String mVerifyPasswordString;
    private String mPasswordString;
    private String mLocationString;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
    private static final int LOCATION_PERMISSION = 4;
    private HttpRequest request;
    private static RegisterActivity sInstance;
    private GoogleMap mMap;
    private int zoomCounter = 0;
    private SupportMapFragment mapFragment;

    public static RegisterActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        sInstance = this;
        mFullNameEditText = findViewById(R.id.user_name_edit_text);
        mEmailAddressEditText = findViewById(R.id.email_edit_text);
        mPhoneNumberEditText = findViewById(R.id.mobile_number_edit_text);
        mLandlineEditText = findViewById(R.id.land_line_edit_text);

        mAddressEditText = findViewById(R.id.address_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mVerifyPasswordEditText = findViewById(R.id.verify_password_edit_text);
        mRegisterButton = findViewById(R.id.sign_up_button);


        mFullNameEditText.setTypeface(AppGlobals.typefaceNormal);
        mEmailAddressEditText.setTypeface(AppGlobals.typefaceNormal);
        mPasswordEditText.setTypeface(AppGlobals.typefaceNormal);
        mVerifyPasswordEditText.setTypeface(AppGlobals.typefaceNormal);

        mRegisterButton.setTypeface(AppGlobals.typefaceNormal);
        mRegisterButton.setOnClickListener(this);
        mSectorSpinnerValueString = getIntent().getStringExtra("sectorvalue");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermission();
        } else {
            if (!Helpers.locationEnabled()) {
                Helpers.dialogForLocationEnableManually(this);
            }
        }

    }

    private void locationPermission() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.permission_dialog_title));
        alertDialogBuilder.setMessage(getResources().getString(R.string.permission_dialog_message))
                .setCancelable(false).setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppGlobals.LOCATION_ENABLE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            mMap.setMyLocationEnabled(true);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_button:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                    alertDialog.setTitle("Permission request");
                    alertDialog.setMessage("To verify your phone number " + getString(R.string.app_name)
                            + " app can easily check your verification code if you allow Sms permission.");
                    alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ActivityCompat.requestPermissions(RegisterActivity.this,
                                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                                    MY_PERMISSIONS_REQUEST_READ_SMS);
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                } else {
                    mLandlineEditTextString = mLandlineEditText.getText().toString();
                    if (validateEditText()) {
                        registerUser(mFullNameString,
                                mEmailAddressString,
                                mPhoneNumberString,
                                mAddressEditTextString,
                                mLandlineEditTextString,
                                mLocationString,
                                mSectorSpinnerValueString,
                                mUserNameEditTextString,
                                mPasswordString);
                    }

                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted!", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this, "permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!Helpers.locationEnabled()) {
                        Helpers.dialogForLocationEnableManually(this);
                    }
                } else {
                    Helpers.showSnackBar(findViewById(android.R.id.content), R.string.permission_denied);
                    locationPermission();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (location != null) {
                Log.i("TAG", "location " + location.getLatitude());
                if (zoomCounter < 1) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(14).build();
                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
                zoomCounter++;
            }

        }
    };

    private boolean validateEditText() {
        boolean valid = true;
        mPasswordString = mPasswordEditText.getText().toString();
        mVerifyPasswordString = mVerifyPasswordEditText.getText().toString();
        mEmailAddressString = mEmailAddressEditText.getText().toString();
        mPhoneNumberString = mPhoneNumberEditText.getText().toString();
        mFullNameString = mFullNameEditText.getText().toString();
        mAddressEditTextString = mAddressEditText.getText().toString();

        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 4) {
            mPasswordEditText.setError("enter at least 4 characters");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 4 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPasswordEditText.setError("password does not match");
            valid = false;
        } else {
            mVerifyPasswordEditText.setError(null);
        }
        if (mPhoneNumberString.trim().isEmpty()) {
            mPhoneNumberEditText.setError("required");
            valid = false;
        } else {
            mPhoneNumberEditText.setError(null);
        }

        if (mAddressEditTextString.trim().isEmpty()) {
            mAddressEditText.setError("required");
            valid = false;
        } else {
            mAddressEditText.setError(null);
        }

        if (mFullNameString.trim().isEmpty()) {
            mFullNameEditText.setError("required");
            valid = false;
        } else {
            mFullNameEditText.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmailAddressEditText.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddressEditText.setError(null);
        }
        return valid;
    }

    private void registerUser(String full_name, String email, String phoneNumber,
                              String address, String landline, String location, String sector,
                              String username, String password) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister", AppGlobals.BASE_URL));
        request.send(getRegisterData(full_name, email, phoneNumber, address, landline, location,
                sector, username, password));
        Helpers.showProgressDialog(RegisterActivity.this, "Registering User ");
    }


    private String getRegisterData(String full_name, String email, String phoneNumber,
                                   String address, String landline, String location, String sector,
                                   String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", full_name);
            jsonObject.put("email", email);
            jsonObject.put("mobile_number", phoneNumber);
            jsonObject.put("address", address);
            jsonObject.put("landline", landline);
            jsonObject.put("location", location);
            jsonObject.put("sector", sector);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            System.out.println(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(RegisterActivity.this, "Registration Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(RegisterActivity.this, "Registration Failed!", "Email already in use");
                        System.out.println(request.getResponseText() + "working ");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(getApplicationContext(), "Activation code has been sent to you! Please check your Email", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            System.out.println(jsonObject + "working ");
                            JSONObject sectorObject = jsonObject.getJSONObject("sector");

                            String fulName = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);

                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_MOBILE_NUMBER);
                            String landlineNumber = jsonObject.getString(AppGlobals.KEY_LANDLINE_NUMBER);
                            String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                            String location = jsonObject.getString(AppGlobals.KEY_LOCATION);
                            String sectorValue = sectorObject.getString(AppGlobals.KEY_SECTOR_ID);


                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, fulName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER, phoneNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LANDLINE_NUMBER, landlineNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, location);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SECTOR_ID, sectorValue);
                            LoginActivity.getInstance().finish();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), CodeConfirmationActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    @Override
    public void onError(HttpRequest request, int readyS, short error, Exception exception) {
        System.out.println(request.getStatus());
        switch (request.getStatus()) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }


    private void drawMarkerWithCircle(LatLng position, String name) {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(name);
        Marker mMarker = mMap.addMarker(markerOptions);
        mMarker.setPosition(position);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        drawMarkerWithCircle(latLng, "WahVege Location");
        mLocationString = latLng.toString();

    }
}
