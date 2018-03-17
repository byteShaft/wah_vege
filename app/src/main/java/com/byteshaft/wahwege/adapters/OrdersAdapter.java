package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.Orders;
import com.byteshaft.wahwege.shopnow.CheckOut;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by husnain on 11/13/17.
 */

public class OrdersAdapter extends ArrayAdapter<String> {


    private ViewHolder viewHolder;
    private ArrayList<Orders> arrayList;
    private Activity activity;

    public OrdersAdapter(Activity activity, ArrayList<Orders> arrayList) {
        super(activity.getApplicationContext(), R.layout.orders_delegate);
        this.arrayList = arrayList;
        this.activity = activity;
    }


    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.orders_delegate, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.productName = convertView.findViewById(R.id.product_name);
            viewHolder.productPrice = convertView.findViewById(R.id.product_price);
            viewHolder.productWahVegePrice = convertView.findViewById(R.id.wahvege_price);
            viewHolder.productImage = convertView.findViewById(R.id.product_image);
            viewHolder.removeTextView = convertView.findViewById(R.id.remove);
            viewHolder.totalPrice = convertView.findViewById(R.id.total_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Orders orders = arrayList.get(position);
        viewHolder.productName.setText(orders.getProductName());
        viewHolder.productPrice.setText(String.valueOf(orders.getProductPrice()));
        viewHolder.productWahVegePrice.setText(String.valueOf(orders.getWahvegePrice()));
        Picasso.with(AppGlobals.getContext()).load(orders.getProductImage()).into(viewHolder.productImage);
        viewHolder.removeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.remove(position);
                AppGlobals.ordersHashMap.remove(orders.getProductId());
                Log.i("TAG", String.valueOf(arrayList.size()));
                Log.i("TAG", String.valueOf(AppGlobals.ordersHashMap));
                notifyDataSetChanged();
                CheckOut.getInstance().setGrandTotal();
            }
        });
        float quantity = orders.getProductQuantity();
        double totalPrice = quantity * Float.valueOf(orders.getWahvegePrice());
        viewHolder.totalPrice.setText("WahVege Order Price: " + String.valueOf(totalPrice));
        Log.i("TAG", "total price "+totalPrice);
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {
        TextView productName;
        CircleImageView productImage;
        TextView productPrice;
        TextView productWahVegePrice;
        TextView removeTextView;
        TextView totalPrice;
    }
}
