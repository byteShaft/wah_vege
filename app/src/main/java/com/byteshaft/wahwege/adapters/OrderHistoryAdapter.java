package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.Demands;
import com.byteshaft.wahwege.gettersetter.OrderHistoryItmes;
import com.byteshaft.wahwege.gettersetter.OrderHistoryMain;
import com.byteshaft.wahwege.utils.AppGlobals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by husnain on 11/20/17.
 */

public class OrderHistoryAdapter extends ArrayAdapter {

    private ViewHolder viewHolder;
    private ArrayList<OrderHistoryMain> arrayList;
    private Activity activity;

    public OrderHistoryAdapter(Activity activity, ArrayList<OrderHistoryMain> arrayList) {
        super(activity.getApplicationContext(), R.layout.delegate_order_history);
        this.arrayList = arrayList;
        this.activity = activity;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_order_history, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.orderDate = convertView.findViewById(R.id.order_date);
            viewHolder.totalOrderPrice = convertView.findViewById(R.id.total_order_price);
            viewHolder.orderStatus = convertView.findViewById(R.id.order_status);
            viewHolder.relativeLayout = convertView.findViewById(R.id.order_items_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final OrderHistoryMain orderHistoryMain = arrayList.get(position);

        viewHolder.orderStatus.setText(orderHistoryMain.getDeliveryStatus());
        ArrayList<OrderHistoryItmes> orderHistoryItmesArrayList = orderHistoryMain.getArrayList();
        double grandTotal = 0;
        for (int i = 0; i < orderHistoryItmesArrayList.size(); i++) {
            OrderHistoryItmes orderHistoryItmes = orderHistoryItmesArrayList.get(i);
            View view = activity.getLayoutInflater().inflate(R.layout.order_item_details_delegate, viewHolder.relativeLayout, false);
            TextView prductName = view.findViewById(R.id.product_name);
            TextView prductQuantity = view.findViewById(R.id.product_quantity);
            TextView productPrice = view.findViewById(R.id.product_Price);
            prductName.setText(orderHistoryItmes.getProductName());
            prductQuantity.setText(orderHistoryItmes.getProductQuantity() + "KG");
            float quantity = orderHistoryItmes.getProductQuantity();
            double totalPrice = quantity * Integer.valueOf(orderHistoryItmes.getProductPrice());
            productPrice.setText("RS: " + String.valueOf(totalPrice));
            grandTotal = grandTotal + totalPrice;
            viewHolder.totalOrderPrice.setText("RS: " + String.valueOf(grandTotal));
            String input = orderHistoryMain.getDeliveryDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try {
                viewHolder.orderDate.setText(df.format(format.parse(input)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            viewHolder.relativeLayout.addView(view);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {
        TextView orderDate;
        TextView totalOrderPrice;
        TextView orderStatus;
        LinearLayout relativeLayout;
        
    }

    class SubItemHolder{

        TextView prductName;
        TextView prductQuantity;
        TextView productPrice;
    }
}

