package com.byteshaft.wahwege.account;

import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.SectorAdapter;
import com.byteshaft.wahwege.gettersetter.Sector;
import com.byteshaft.wahwege.utils.AppGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 10/27/17.
 */

public class SelectSector extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

        private String mSectorSpinnerValueString;
        private ArrayList<Sector> sectorArrayList;
        private SectorAdapter sectorAdapter;
        private Spinner mSectorSpinner;

        private Button mNextButton;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.select_sector);
            mSectorSpinner = findViewById(R.id.sector_spinner);
            mNextButton = findViewById(R.id.next_button);

            mSectorSpinner.setOnItemSelectedListener(this);
            mNextButton.setOnClickListener(this);

            sectorArrayList = new ArrayList<>();
            getSectors();
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("sectorvalue", mSectorSpinnerValueString);

            if (mSectorSpinnerValueString.isEmpty()) {
                mNextButton.setEnabled(false);
            } else {
                mNextButton.setEnabled(true);
                startActivity(intent);
            }


        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Sector sector = sectorArrayList.get(i);
            mSectorSpinnerValueString =  String.valueOf(sector.getSetcorId());
            System.out.println(mSectorSpinnerValueString + "shahid chae lay aa jaldi");

        }

        @Override


        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    private void getSectors() {
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
                                    }
                                    sectorAdapter = new SectorAdapter(SelectSector.this, sectorArrayList);
                                    mSectorSpinner.setAdapter(sectorAdapter);

                                    mSectorSpinner.setSelection(0);
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
}
