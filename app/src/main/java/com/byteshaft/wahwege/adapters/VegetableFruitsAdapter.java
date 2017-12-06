package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.Orders;
import com.byteshaft.wahwege.gettersetter.VegetablesFruits;
import com.byteshaft.wahwege.shopnow.Fruits;
import com.byteshaft.wahwege.shopnow.Vegetables;
import com.byteshaft.wahwege.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by husnain on 11/6/17.
 */

public class VegetableFruitsAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<VegetablesFruits> arrayList;
    private Activity activity;
    private String mQuantityValue;


    public VegetableFruitsAdapter(Activity activity, ArrayList<VegetablesFruits> arrayList) {
        super(activity.getApplicationContext(), R.layout.delegate_vegetables_fruits);
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_vegetables_fruits, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.productName = convertView.findViewById(R.id.product_name);
            viewHolder.productMarketPrice = convertView.findViewById(R.id.product_price);
            viewHolder.productWahVegePrice = convertView.findViewById(R.id.wahvege_price);
            viewHolder.productImage = convertView.findViewById(R.id.product_image);
            viewHolder.availableStock = convertView.findViewById(R.id.stock_count);
            viewHolder.orderNow = convertView.findViewById(R.id.order_now);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        VegetablesFruits vegetablesFruits = arrayList.get(position);
        viewHolder.productName.setText(vegetablesFruits.getProductName());
        viewHolder.productMarketPrice.setText(vegetablesFruits.getProductMarketPrice());
        viewHolder.productWahVegePrice.setText(vegetablesFruits.getProductWahVegePrice());
        viewHolder.availableStock.setText(vegetablesFruits.getProductStockCount());
        Picasso.with(AppGlobals.getContext()).load(vegetablesFruits.getProductImage()).into(viewHolder.productImage);
        viewHolder.orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityDialog(position);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    public void quantityDialog(final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.edit_text_dialog_layuot);
        dialog.setTitle("Quantity For Items");
        dialog.setCancelable(true);
        dialog.show();
        final EditText quantityEditText = dialog.findViewById(R.id.quantity_edit_text);
        final Button submitButton = dialog.findViewById(R.id.submit_button);
        submitButton.setEnabled(false);
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    submitButton.setEnabled(false);
                } else {
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuantityValue = quantityEditText.getText().toString();
                VegetablesFruits vegetablesFruits = arrayList.get(position);
                Orders orders = new Orders();
                orders.setProductId(vegetablesFruits.getProductId());
                orders.setProductImage(vegetablesFruits.getProductImage());
                orders.setProductPrice(Integer.parseInt(vegetablesFruits.getProductMarketPrice()));
                orders.setWahvegePrice(vegetablesFruits.getProductWahVegePrice());
                orders.setProductQuantity(Float.parseFloat(mQuantityValue));
                orders.setProductName(vegetablesFruits.getProductName());
                if (AppGlobals.ordersHashMap == null) {
                    AppGlobals.ordersHashMap = new HashMap<>();
                }
                AppGlobals.ordersHashMap.put(vegetablesFruits.getProductId(), orders);
                Fruits.updateFruitOrder();
                Vegetables.updateVegetablesOrder();
                dialog.dismiss();
            }
        });
    }


    class ViewHolder {
        TextView productName;
        CircleImageView productImage;
        TextView productMarketPrice;
        TextView productWahVegePrice;
        TextView availableStock;
        TextView orderNow;
    }
}
