package com.byteshaft.wahwege.shopnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.account.LoginActivity;
import com.byteshaft.wahwege.account.SelectSector;
import com.byteshaft.wahwege.adapters.OrdersAdapter;
import com.byteshaft.wahwege.adapters.SectorAdapter;
import com.byteshaft.wahwege.gettersetter.Orders;
import com.byteshaft.wahwege.gettersetter.Sector;
import com.byteshaft.wahwege.utils.AppGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by husnain on 11/13/17.
 */

public class CheckOut extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    private ListView orderList;
    private Button proceedButton;
    private ArrayList<Orders> ordersArrayList;
    private OrdersAdapter ordersAdapter;
    private TextView checkOutPrice;
    private double total;
    private static CheckOut instance;
    private String mMinPrice;

    public static CheckOut getInstance(){
        return  instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fregment_check_out);
        instance = this;
        orderList = findViewById(R.id.order_list);
        checkOutPrice = findViewById(R.id.check_out_price);
        proceedButton = findViewById(R.id.proceed_button);
        ordersArrayList = new ArrayList<>();
        setGrandTotal();
        ordersAdapter = new OrdersAdapter(CheckOut.this, ordersArrayList);
        orderList.setAdapter(ordersAdapter);
        proceedButton.setOnClickListener(this);
        getMinPrice();
    }

    public void setGrandTotal() {
        ordersArrayList = new ArrayList<>();
        total = 0;
        checkOutPrice.setText("");
        for (Map.Entry<Integer, Orders> map : AppGlobals.ordersHashMap.entrySet()) {
            ordersArrayList.add(map.getValue());
            Orders orders = map.getValue();
            double calculateTotalPrice = Integer.valueOf(orders.getWahvegePrice()) * orders.getProductQuantity();
            Log.i("TAG", " calculate total  "+ calculateTotalPrice);
            total = total + calculateTotalPrice;
            Log.i("TAG", " total  "+ total);

        }
        checkOutPrice.setText("Grand total: "+String.valueOf(total));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.proceed_button:
                String grandTotalprice = String.valueOf(total);
                if (Float.valueOf(grandTotalprice) < Float.valueOf(mMinPrice)) {
                    AppGlobals.alertDialog(CheckOut.this, "Request Failed!",
                            "Minimum Order Price Should be " + "RS" + " "+ mMinPrice);
                } else {
                    orderRequest();
                }
                break;
        }

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        System.out.println(request.getResponseText());
                        AppGlobals.alertDialog(CheckOut.this, "Request Failed!",
                                "Order quantity is more than stock");
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        System.out.println(request.getResponseURL());
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseURL());
                        Toast.makeText(getApplicationContext(), "Your request has been received", Toast.LENGTH_SHORT).show();
                        AppGlobals.ordersHashMap = new HashMap<>();
                        finish();
                }
        }
    }

    private void orderRequest() {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sorders/", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(orderRequestData());
    }

    private String orderRequestData() {
        JSONObject ordersObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, Orders> map : AppGlobals.ordersHashMap.entrySet()) {
            Orders orders = map.getValue();
            double calculateTotalPrice = Integer.valueOf(orders.getWahvegePrice()) * orders.getProductQuantity();
            total = total + calculateTotalPrice;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("item", orders.getProductId());
                jsonObject.put("quantity", orders.getProductQuantity());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        try {
            ordersObject.put("items", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ordersObject.toString();
    }


    private void getMinPrice() {
        HttpRequest getStateRequest = new HttpRequest(getApplicationContext());
        getStateRequest.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                System.out.println(request.getResponseText());
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    mMinPrice = jsonObject.getString(AppGlobals.KEY_MIN_PRICE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        getStateRequest.open("GET", String.format("%smin-price", AppGlobals.BASE_URL));
        getStateRequest.send();
    }
}
