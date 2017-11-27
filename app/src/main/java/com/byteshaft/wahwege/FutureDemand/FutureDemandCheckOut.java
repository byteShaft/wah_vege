package com.byteshaft.wahwege.FutureDemand;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.adapters.DemandAdapter;
import com.byteshaft.wahwege.adapters.OrdersAdapter;
import com.byteshaft.wahwege.gettersetter.Demands;
import com.byteshaft.wahwege.gettersetter.Orders;
import com.byteshaft.wahwege.shopnow.CheckOut;
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

public class FutureDemandCheckOut extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    private ListView orderList;
    private Button proceedButton;
    private ArrayList<Demands> demandsArrayList;
    private DemandAdapter demandAdapter;
    private TextView checkOutPrice;
    private double total;
    private static FutureDemandCheckOut instance;
    private String deliveryDate;

    public static FutureDemandCheckOut getInstance(){
        return  instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demand_fregment_check_out);
        instance = this;
        orderList = findViewById(R.id.order_list);
        proceedButton = findViewById(R.id.proceed_button);
        demandsArrayList = new ArrayList<>();
        for (Map.Entry<Integer, Demands> map : AppGlobals.demandHashMap.entrySet()) {
            demandsArrayList.add(map.getValue());
            Demands orders = map.getValue();
//            double calculateTotalPrice = Integer.valueOf(orders.getWahvegePrice()) * orders.getProductQuantity();
//            Log.i("TAG", " calculate total  "+ calculateTotalPrice);
//            total = total + calculateTotalPrice;
//            Log.i("TAG", " total  "+ total);

        }
        demandAdapter = new DemandAdapter(FutureDemandCheckOut.this, demandsArrayList);
        orderList.setAdapter(demandAdapter);
        proceedButton.setOnClickListener(this);
        deliveryDate = AddDemand.mDateEditTextString;
        System.out.println(deliveryDate);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.proceed_button:
                orderRequest(deliveryDate);
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
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        System.out.println(request.getResponseURL());
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        System.out.println(request.getResponseText());
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        System.out.println(request.getResponseText());
                        AppGlobals.alertDialog(FutureDemandCheckOut.this, "Request Failed!",
                                "Demand Quantity is more than Stock");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseURL());
                        Toast.makeText(getApplicationContext(), "Your request has been received", Toast.LENGTH_SHORT).show();
                        AppGlobals.demandHashMap = new HashMap<>();
                        finish();
                }
        }
    }

    private void orderRequest(String deliverydate) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sfuture/orders/ ", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(orderRequestData(deliverydate));
    }

    private String orderRequestData(String deliverydate) {
        JSONObject ordersObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, Demands> map : AppGlobals.demandHashMap.entrySet()) {
            Demands demands = map.getValue();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("item", demands.getProductId());
                jsonObject.put("quantity", demands.getProductQuantity());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        try {
            ordersObject.put("delivery_date", deliverydate);
            ordersObject.put("items", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ordersObject.toString();
    }
}
