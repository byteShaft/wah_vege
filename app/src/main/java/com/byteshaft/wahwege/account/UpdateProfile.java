package com.byteshaft.wahwege.account;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.SectorAdapter;
import com.byteshaft.wahwege.gettersetter.Sector;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;
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

import java.net.HttpURLConnection;
import java.util.ArrayList;


public class UpdateProfile extends AppCompatActivity implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener, OnItemSelectedListener,OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private Button mUpdateButton;
    private EditText mUsername;
    private EditText mFullname;
    private EditText mEmailAddress;
    private EditText mPhoneNumber;
    private EditText mLandlineEditText;
    private EditText mAddressEditText;
    private Spinner mSectorSpinner;

    private String mSectorSpinnerValueString;
    private String mLandlineEditTextString;
    private String mUserNameEditTextString;
    private String mAddressEditTextString;
    private String mFullNameString;
    public  String mPhoneNumberString;
    private String mLocationString;

    private HttpRequest request;
    private int selection = 0;


    private ArrayList<Sector> sectorArrayList;
    private SectorAdapter sectorAdapter;

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
    private static final int LOCATION_PERMISSION = 4;

    private GoogleMap mMap;
    private int zoomCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.update_profile);
        mFullname =  findViewById(R.id.full_name_edit_text);
        mEmailAddress = findViewById(R.id.email_edit_text);
        mPhoneNumber =  findViewById(R.id.mobile_number_edit_text);
        mLandlineEditText = findViewById(R.id.land_line_edit_text);
        mAddressEditText = findViewById(R.id.address_edit_text);
        mSectorSpinner = findViewById(R.id.sector_spinner);
        mUpdateButton = findViewById(R.id.update_button);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (AppGlobals.isLogin()) {
            mFullname.setTypeface(AppGlobals.typefaceNormal);
            mEmailAddress.setTypeface(AppGlobals.typefaceNormal);
            mPhoneNumber.setTypeface(AppGlobals.typefaceNormal);
            mLandlineEditText.setTypeface(AppGlobals.typefaceNormal);
            mAddressEditText.setTypeface(AppGlobals.typefaceNormal);
            mUpdateButton.setTypeface(AppGlobals.typefaceNormal);

            //getting user saved data from sharedPreference
            mFullname.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULL_NAME));
            mEmailAddress.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
            mPhoneNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER));
            mLandlineEditText.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LANDLINE_NUMBER));
            mAddressEditText.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ADDRESS));
        }
        mSectorSpinner.setOnItemSelectedListener(this);
        sectorArrayList = new ArrayList<>();
        getSectors();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermission();


        } else {
            if (!Helpers.locationEnabled()) {
                Helpers.dialogForLocationEnableManually(this);
            }
        }

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFullNameString = mFullname.getText().toString();
                mLandlineEditTextString = mLandlineEditText.getText().toString();
                mPhoneNumberString = mPhoneNumber.getText().toString();
                mFullNameString = mFullname.getText().toString();
                mAddressEditTextString = mAddressEditText.getText().toString();
                updateUser(mFullNameString, mPhoneNumberString, mAddressEditTextString,
                        mLandlineEditTextString, mLocationString, mSectorSpinnerValueString,
                        mUserNameEditTextString);

            }
        });
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    private void getSectors() {
        selection = 0;
        HttpRequest getStateRequest = new HttpRequest(getApplicationContext());
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
                                        Sector sector = new Sector();
                                        sector.setSetcorId(jsonObject.getInt("id"));
                                        sector.setSetcorName(jsonObject.getString("named_id"));
                                        sectorArrayList.add(sector);
                                        if (jsonObject.getInt("id") == Integer.valueOf(AppGlobals
                                                .getStringFromSharedPreferences(AppGlobals.KEY_SECTOR_ID))) {
                                            selection = i;

                                        }
                                    }
                                    sectorAdapter = new SectorAdapter(UpdateProfile.this, sectorArrayList);
                                    mSectorSpinner.setAdapter(sectorAdapter);

                                    mSectorSpinner.setSelection(selection);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%ssectors/", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(this, "Updated Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
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
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void updateUser(String full_name, String phoneNumber,
                            String address, String landline, String location, String sector,
                            String username) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("PUT", String.format("%sme", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(updateUserData(full_name, phoneNumber, address, landline, location,
                sector, username));
        Helpers.showProgressDialog(this, "Updating User Profile");
    }

    private String updateUserData(String full_name, String phoneNumber,
                                   String address, String landline, String location, String sector,
                                   String username) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", full_name);
            jsonObject.put("mobile_number", phoneNumber);
            jsonObject.put("address", address);
            jsonObject.put("landline", landline);
            jsonObject.put("location", location);
            jsonObject.put("sector", sector);
            jsonObject.put("username", username);
            System.out.println(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Sector sector = sectorArrayList.get(i);
        mSectorSpinnerValueString =  String.valueOf(sector.getSetcorId());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                    Log.i("TAG", AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION));
                    String userLocation = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION)
                            .replace("lat/lng:", "")
                            .replace("(", "").replace(")", "").trim();
                    if (!userLocation.equals("")) {
                        Log.i("TAG", userLocation);
                        String[] splitLocation = userLocation.split(",");
                        LatLng latLng = new LatLng(Double.parseDouble(splitLocation[0]),
                                Double.parseDouble(splitLocation[1]));
                        drawMarker(latLng, "");
                    }
                }
                zoomCounter++;
            }

        }
    };

    private void drawMarker(LatLng position, String name) {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(name);
        Marker mMarker = mMap.addMarker(markerOptions);
        mMarker.setPosition(position);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        drawMarker(latLng, "WahVege Location");
        mLocationString = latLng.toString();

    }
}
