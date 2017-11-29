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
import com.byteshaft.wahwege.gettersetter.DemandsItemsList;
import com.byteshaft.wahwege.gettersetter.DemandsMianList;

import java.util.ArrayList;

/**
 * Created by husnain on 11/20/17.
 */

public class DemandListAdapter extends ArrayAdapter {

    private ViewHolder viewHolder;
    private ArrayList<DemandsMianList> arrayList;
    private Activity activity;

    public DemandListAdapter(Activity activity, ArrayList<DemandsMianList> arrayList) {
        super(activity.getApplicationContext(), R.layout.delegate_demand_main_list);
        this.arrayList = arrayList;
        this.activity = activity;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_demand_main_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.orderDate = convertView.findViewById(R.id.order_date);
            viewHolder.relativeLayout = convertView.findViewById(R.id.order_items_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DemandsMianList demandsMianList = arrayList.get(position);
        ArrayList<DemandsItemsList> demandsItemsListArrayList = demandsMianList.getArrayList();
        if (demandsItemsListArrayList.size() > 0) {
            viewHolder.relativeLayout.removeAllViews();
            for (int i = 0; i < demandsItemsListArrayList.size(); i++) {
                View childView = activity.getLayoutInflater()
                        .inflate(R.layout.delegate_demand_items_list, viewHolder.relativeLayout,
                                false);
                TextView productName = childView.findViewById(R.id.product_name);
                TextView productQuantity = childView.findViewById(R.id.product_quantity);
                viewHolder.relativeLayout.addView(childView);
                DemandsItemsList demandsItemsList = demandsItemsListArrayList.get(i);
                productName.setText(demandsItemsList.getProductName());
                productQuantity.setText(demandsItemsList.getProductQuantity() + "KG");
                viewHolder.orderDate.setText(demandsMianList.getDeliveryDate());
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
        LinearLayout relativeLayout;
    }

}

