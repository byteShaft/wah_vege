package com.byteshaft.wahwege;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.adapters.OrderHistoryAdapter;
import com.byteshaft.wahwege.gettersetter.OrderHistoryItems;
import com.byteshaft.wahwege.gettersetter.OrderHistoryMain;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by husnain on 10/31/17.
 */

public class PurchaseHistory extends Fragment implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener {

    private View mBaseView;
    private ListView mHistoryListView;
    private ArrayList<OrderHistoryMain> orderHistoryMainArrayList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private int currentArraySize = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.history_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Order History");
        setHasOptionsMenu(true);
        mHistoryListView = mBaseView.findViewById(R.id.history_list_view);
        orderHistoryMainArrayList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), orderHistoryMainArrayList);
        mHistoryListView.setAdapter(orderHistoryAdapter);
        getOrderHistory();
        return mBaseView;
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
                        Log.i("TAG", "response " + request.getResponseText());
                        try {
                            JSONArray orderHistoryJSONArray = new JSONArray(request.getResponseText());
                            for (int j = 0; j < orderHistoryJSONArray.length(); j++) {
                                JSONObject jsonObject;
                                OrderHistoryMain orderHistoryMain;
                                jsonObject = orderHistoryJSONArray.getJSONObject(j);
                                orderHistoryMain = new OrderHistoryMain();
                                orderHistoryMain.setDeliveryDate(jsonObject.getString("created_at"));
                                if (jsonObject.getBoolean("delivered")) {
                                    orderHistoryMain.setDeliveryStatus("Delivered");
                                } else {
                                    orderHistoryMain.setDeliveryStatus("Pending");
                                }
                                ArrayList<OrderHistoryItems> arrayList = new ArrayList<>();
                                JSONArray orderItemsJsonArry = jsonObject.getJSONArray("items");
                                for (int i = 0; i < orderItemsJsonArry.length(); i++) {
                                    OrderHistoryItems orderHistoryItems = new OrderHistoryItems();
                                    JSONObject orderItemsObject = orderItemsJsonArry.getJSONObject(i);
                                    JSONObject jsonObjectDetails = orderItemsObject.getJSONObject("item");
                                    orderHistoryItems.setProductId(jsonObjectDetails.getInt("id"));
                                    orderHistoryItems.setProductName(jsonObjectDetails.getString("name"));
                                    orderHistoryItems.setProductUnit(jsonObjectDetails.getString("unit"));
                                    orderHistoryItems.setProductPrice(jsonObjectDetails.getInt("wah_vege_price"));
                                    orderHistoryItems.setProductQuantity(orderItemsObject.getInt("quantity"));
                                    arrayList.add(orderHistoryItems);
                                }
                                orderHistoryMain.setArrayList(arrayList);
//                                Log.i("TAG", "total items "+ arrayList.size());
                                if (arrayList.size() > 0) {
                                    orderHistoryMainArrayList.add(orderHistoryMain);
                                    orderHistoryAdapter.notifyDataSetChanged();
                                }
                            }
                            currentArraySize = orderHistoryMainArrayList.size();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

        private void getOrderHistory() {
            HttpRequest request = new HttpRequest(AppGlobals.getContext());
            request.setOnReadyStateChangeListener(this);
            request.setOnErrorListener(this);
            request.open("GET", String.format("%sorders/", AppGlobals.BASE_URL));
            request.setRequestHeader("Authorization", "Token " +
                    AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
            request.send();
            Helpers.showProgressDialog(getActivity(), getString(R.string.order_history));
        }
}
