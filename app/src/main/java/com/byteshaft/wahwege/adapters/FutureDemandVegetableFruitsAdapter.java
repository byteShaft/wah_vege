package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;

import com.byteshaft.wahwege.FutureDemand.FutureDemandFruits;
import com.byteshaft.wahwege.FutureDemand.FutureDemandVegetables;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.Demands;
import com.byteshaft.wahwege.gettersetter.FutureDemandVegetablesFruits;
import com.byteshaft.wahwege.utils.AppGlobals;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by husnain on 11/6/17.
 */

public class FutureDemandVegetableFruitsAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<FutureDemandVegetablesFruits> arrayList;
    private Activity activity;
    private String mQuantityValue;


    public FutureDemandVegetableFruitsAdapter(Activity activity, ArrayList<FutureDemandVegetablesFruits> arrayList) {
        super(activity.getApplicationContext(), R.layout.delegate_future_demand);
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_future_demand, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.productName = convertView.findViewById(R.id.product_name);
            viewHolder.addDemand = convertView.findViewById(R.id.add_demand);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        FutureDemandVegetablesFruits futureDemandVegetablesFruits = arrayList.get(position);
        viewHolder.productName.setText(futureDemandVegetablesFruits.getProductName());
        viewHolder.addDemand.setOnClickListener(new View.OnClickListener() {
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
                if (charSequence.toString().equals("0")) {
                    submitButton.setEnabled(false);
                    Toast.makeText(AppGlobals.getContext(), "please enter quantity in positive number", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    submitButton.setEnabled(true);
                }
                if(charSequence.toString().trim().length() < 0){
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
                FutureDemandVegetablesFruits futureDemandVegetablesFruits = arrayList.get(position);
                Demands demands = new Demands();
                demands.setProductId(futureDemandVegetablesFruits.getProductId());
                demands.setProductQuantity(Float.parseFloat(mQuantityValue));
                demands.setProductName(futureDemandVegetablesFruits.getProductName());
                if (AppGlobals.demandHashMap == null) {
                    AppGlobals.demandHashMap = new HashMap<>();
                }
                AppGlobals.demandHashMap.put(futureDemandVegetablesFruits.getProductId(), demands);
                FutureDemandFruits.updateFutureFruitOrder();
                FutureDemandVegetables.updateFutureVegetablesOrder();
                dialog.dismiss();
            }
        });
    }

    class ViewHolder {
        TextView productName;
        TextView addDemand;
    }
}
