package com.byteshaft.wahwege.FutureDemand;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.FutureDemandVegetableFruitsAdapter;
import com.byteshaft.wahwege.adapters.VegetableFruitsAdapter;
import com.byteshaft.wahwege.gettersetter.FutureDemandVegetablesFruits;
import com.byteshaft.wahwege.gettersetter.VegetablesFruits;
import com.byteshaft.wahwege.shopnow.CheckOut;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 11/3/17.
 */

public class FutureDemandVegetables extends Fragment implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener{

    private View mBaseView;
    private ListView mVegetableListView;
    private FutureDemandVegetableFruitsAdapter futureDemandVegetableFruitsAdapter;
    private ArrayList<FutureDemandVegetablesFruits> futureDemandVegetablesFruitsArrayList;

    private HttpRequest request;

    private ImageView mCartImage;
    private static FrameLayout frameLayout;
    private static TextView mCartCount;


    public static void updateFutureVegetablesOrder() {
        if (mCartCount != null) {
            if (AppGlobals.demandHashMap.size() > 0) {
                mCartCount.setText(String.valueOf(AppGlobals.demandHashMap.size()));
            } else {
                mCartCount.setText("0");
            }

            frameLayout.setVisibility((AppGlobals.demandHashMap.size() > 0) ? View.VISIBLE : View.GONE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.activity_vegetables, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Demand Vegetables");
        setHasOptionsMenu(true);
        mVegetableListView = mBaseView.findViewById(R.id.vegetables_list_view);
        futureDemandVegetablesFruitsArrayList = new ArrayList<>();
        futureDemandVegetableFruitsAdapter = new FutureDemandVegetableFruitsAdapter(getActivity(), futureDemandVegetablesFruitsArrayList);
        mVegetableListView.setAdapter(futureDemandVegetableFruitsAdapter);
        getVegetablesList();
        return mBaseView;
    }


    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                System.out.println("Test " + jsonArray.getJSONObject(i));
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                FutureDemandVegetablesFruits futureDemandVegetablesFruits = new FutureDemandVegetablesFruits();
                                futureDemandVegetablesFruits.setProductId(jsonObject.getInt("id"));
                                futureDemandVegetablesFruits.setProductName(jsonObject.getString("name"));
                                futureDemandVegetablesFruitsArrayList.add(futureDemandVegetablesFruits);
                                futureDemandVegetableFruitsAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

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

    private void getVegetablesList() {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sfuture/vegetables/", AppGlobals.BASE_URL));
        request.send();
        Helpers.showProgressDialog(getActivity(), getString(R.string.vegetables_list));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cart_item, menu);
        final MenuItem alertMenuItem = menu.findItem(R.id.cart_image);
        alertMenuItem.setActionView(R.layout.cart_count_layout);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        frameLayout = rootView.findViewById(R.id.view_circle);
        mCartImage = rootView.findViewById(R.id.cart_image_button);
        mCartCount = rootView.findViewById(R.id.view_alert_count_textview);
        mCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FutureDemandCheckOut.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_image:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateFutureVegetablesOrder();
    }

}
