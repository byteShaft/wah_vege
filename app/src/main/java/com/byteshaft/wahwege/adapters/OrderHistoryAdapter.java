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
    private SubItemHolder subItemHolder;

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
            subItemHolder = new SubItemHolder();
            viewHolder.orderDate = convertView.findViewById(R.id.order_date);
            viewHolder.totalOrderPrice = convertView.findViewById(R.id.total_order_price);
            viewHolder.orderStatus = convertView.findViewById(R.id.order_status);
            viewHolder.relativeLayout = convertView.findViewById(R.id.order_items_layout);
            for (int i = 0; i < arrayList.get(position).getArrayList().size(); i++) {
                viewHolder.subItemHolder  = activity.getLayoutInflater().inflate(R.layout.order_item_details_delegate, viewHolder.relativeLayout, false);
                subItemHolder.prductName = viewHolder.subItemHolder.findViewById(R.id.product_name);
                subItemHolder.prductQuantity = viewHolder.subItemHolder.findViewById(R.id.product_quantity);
                subItemHolder.productPrice = viewHolder.subItemHolder.findViewById(R.id.product_Price);
                viewHolder.subItemHolder.setTag(subItemHolder);
                viewHolder.relativeLayout.addView(viewHolder.subItemHolder);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            subItemHolder =(SubItemHolder) viewHolder.subItemHolder.getTag();
        }
        final OrderHistoryMain orderHistoryMain = arrayList.get(position);

        viewHolder.orderStatus.setText(orderHistoryMain.getDeliveryStatus());
        ArrayList<OrderHistoryItmes> orderHistoryItmesArrayList = orderHistoryMain.getArrayList();
        double grandTotal = 0;
        for (int i = 0; i < orderHistoryItmesArrayList.size(); i++) {
            OrderHistoryItmes orderHistoryItmes = orderHistoryItmesArrayList.get(i);
            subItemHolder.prductName.setText(orderHistoryItmes.getProductName());
            subItemHolder.prductQuantity.setText(orderHistoryItmes.getProductQuantity() + "KG");
            float quantity = orderHistoryItmes.getProductQuantity();
            double totalPrice = quantity * Integer.valueOf(orderHistoryItmes.getProductPrice());
            subItemHolder.productPrice.setText("RS: " + String.valueOf(totalPrice));
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
        View subItemHolder;
    }

    class SubItemHolder{
        TextView prductName;
        TextView prductQuantity;
        TextView productPrice;
    }
}

