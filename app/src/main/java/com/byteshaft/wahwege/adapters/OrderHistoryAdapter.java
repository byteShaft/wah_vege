package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.OrderHistoryItems;
import com.byteshaft.wahwege.gettersetter.OrderHistoryMain;

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
        ArrayList<OrderHistoryItems> orderHistoryItemsArrayList = orderHistoryMain.getArrayList();
//        Log.i("TAG", "position " + position);
        double grandTotal = 0;
        if (orderHistoryItemsArrayList.size() > 0 ) {
            viewHolder.relativeLayout.removeAllViews();
            for (int i = 0; i < orderHistoryItemsArrayList.size(); i++) {
                View childView = activity.getLayoutInflater()
                        .inflate(R.layout.order_item_details_delegate,
                                viewHolder.relativeLayout, false);
                TextView prductName = childView.findViewById(R.id.product_name);
                TextView prductQuantity = childView.findViewById(R.id.product_quantity);
                TextView productUnit = childView.findViewById(R.id.product_unit);
                TextView productPrice = childView.findViewById(R.id.product_Price);
                viewHolder.relativeLayout.addView(childView);
                OrderHistoryItems orderHistoryItems = orderHistoryItemsArrayList.get(i);
                prductName.setText(orderHistoryItems.getProductName());
                productUnit.setText(orderHistoryItems.getProductUnit());
                prductQuantity.setText(orderHistoryItems.getProductQuantity()+"");
                float quantity = orderHistoryItems.getProductQuantity();
                double totalPrice = quantity * Integer.valueOf(orderHistoryItems.getProductPrice());
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
            }
        } else {
            viewHolder.relativeLayout.removeAllViews();
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
//        View subItemHolder;
    }

//    class SubItemHolder{
//        TextView prductName;
//        TextView prductQuantity;
//        TextView productPrice;
//    }
}

