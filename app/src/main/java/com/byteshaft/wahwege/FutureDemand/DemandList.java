package com.byteshaft.wahwege.FutureDemand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.DemandListAdapter;
import com.byteshaft.wahwege.gettersetter.DemandsItemsList;
import com.byteshaft.wahwege.gettersetter.DemandsMianList;
import com.byteshaft.wahwege.gettersetter.OrderHistoryItmes;
import com.byteshaft.wahwege.gettersetter.OrderHistoryMain;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 11/13/17.
 */

public class DemandList extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    private View mBaseView;
    private ListView mFutureDemandList;
    private ArrayList<DemandsMianList> demandsMianListArrayList;
    private DemandListAdapter demandListAdapter;
    private HttpRequest request;
    private int currentArraySize = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.future_demands_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Demands");
        mFutureDemandList = mBaseView.findViewById(R.id.future_demand_list);
        setHasOptionsMenu(true);
        demandsMianListArrayList = new ArrayList<>();
        demandListAdapter = new DemandListAdapter(getActivity(), demandsMianListArrayList);
        mFutureDemandList.setAdapter(demandListAdapter);
        getDemandsHistory();
        return mBaseView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_demand, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_button:
                MainActivity.getInstance().loadFragment(new AddDemand());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONArray demandsMianJSONArray = new JSONArray(request.getResponseText());
                            for (int j = 0; j < demandsMianJSONArray.length(); j++) {
                                JSONObject jsonObject;
                                DemandsMianList demandsMianList;
                                jsonObject = demandsMianJSONArray.getJSONObject(j);
                                demandsMianList = new DemandsMianList();
                                demandsMianList.setDeliveryDate(jsonObject.getString("delivery_date"));
                                ArrayList<DemandsItemsList> arrayList = new ArrayList<>();
                                JSONArray demandItemsJsonArry = jsonObject.getJSONArray("items");
                                for (int i = 0; i < demandItemsJsonArry.length(); i++) {
                                    DemandsItemsList demandsItemsList = new DemandsItemsList();
                                    JSONObject orderItemsObject = demandItemsJsonArry.getJSONObject(i);
                                    JSONObject jsonObjectDetails = orderItemsObject.getJSONObject("item");
                                    demandsItemsList.setProductId(jsonObjectDetails.getInt("id"));
                                    demandsItemsList.setProductName(jsonObjectDetails.getString("name"));
                                    demandsItemsList.setProductQuantity(orderItemsObject.getInt("quantity"));
                                    arrayList.add(demandsItemsList);
                                }
                                demandsMianList.setArrayList(arrayList);
                                Log.i("TAG", "total items "+ arrayList.size());
                                demandsMianListArrayList.add(demandsMianList);
                                demandListAdapter.notifyDataSetChanged();
                            }
                            currentArraySize = demandsMianListArrayList.size();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void getDemandsHistory() {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sfuture/orders/", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
        Helpers.showProgressDialog(getActivity(), getString(R.string.demand_history));
    }
}
