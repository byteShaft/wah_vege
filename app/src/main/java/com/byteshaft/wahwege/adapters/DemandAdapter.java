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
import com.byteshaft.wahwege.gettersetter.Demands;
import com.byteshaft.wahwege.gettersetter.Orders;
import com.byteshaft.wahwege.shopnow.CheckOut;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by husnain on 11/13/17.
 */

public class DemandAdapter extends ArrayAdapter<String> {


    private ViewHolder viewHolder;
    private ArrayList<Demands> arrayList;
    private Activity activity;

    public DemandAdapter(Activity activity, ArrayList<Demands> arrayList) {
        super(activity.getApplicationContext(), R.layout.order_delegate_demand);
        this.arrayList = arrayList;
        this.activity = activity;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.order_delegate_demand, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.productName = convertView.findViewById(R.id.product_name);
            viewHolder.removeTextView = convertView.findViewById(R.id.remove);
            viewHolder.quantity = convertView.findViewById(R.id.total_quantity);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Demands demands = arrayList.get(position);
        viewHolder.productName.setText(demands.getProductName());
        viewHolder.quantity.setText("Demand Quantity: "+String.valueOf(demands.getProductQuantity()));
        viewHolder.removeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.remove(position);
                AppGlobals.demandHashMap.remove(demands.getProductId());
                Log.i("TAG", String.valueOf(arrayList.size()));
                Log.i("TAG", String.valueOf(AppGlobals.demandHashMap));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {
        TextView productName;
        TextView removeTextView;
        TextView quantity;
    }
}
