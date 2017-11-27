package com.byteshaft.wahwege.shopnow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;


/**
 * Created by husnain on 10/31/17.
 */

public class ShopNow extends Fragment implements OnClickListener {

    private View mBaseView;

    private Button mFruitsButton;
    private Button mVegetablesButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.activity_shop_now, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Show Now");
        setHasOptionsMenu(true);
        mFruitsButton = mBaseView.findViewById(R.id.fruits_button);
        mVegetablesButton = mBaseView.findViewById(R.id.vegetable_button);

        mFruitsButton.setOnClickListener(this);
        mVegetablesButton.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fruits_button:
                MainActivity.getInstance().loadFragment(new Fruits());
                break;
            case R.id.vegetable_button:
                MainActivity.getInstance().loadFragment(new Vegetables());
                break;
        }
    }

}
