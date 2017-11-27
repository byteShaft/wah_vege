package com.byteshaft.wahwege.FutureDemand;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.wahwege.MainActivity;
import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.shopnow.ShopNow;
import com.byteshaft.wahwege.utils.Helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.PriorityQueue;

/**
 * Created by husnain on 10/31/17.
 */

public class AddDemand extends Fragment implements View.OnClickListener{

    private View mBaseView;
    private EditText mDateEditText;
    private Button mNextButton;
    public static String mDateEditTextString;

    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.future_demands, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Add Demand");
        setHasOptionsMenu(true);
        mDateEditText = mBaseView.findViewById(R.id.date_edit_text);
        mNextButton = mBaseView.findViewById(R.id.next_button);

        mDateEditText.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mCalendar = Calendar.getInstance();
         date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        return mBaseView;
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mDateEditText.setText(sdf.format(mCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_edit_text:
                new DatePickerDialog(getActivity(), date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            break;
            case R.id.next_button:
                mDateEditTextString = mDateEditText.getText().toString();
                MainActivity.getInstance().loadFragment(new SelectProduct());
            break;
        }
    }

}
