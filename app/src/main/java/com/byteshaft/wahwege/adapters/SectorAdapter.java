package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.gettersetter.Sector;

import java.util.ArrayList;


public class SectorAdapter extends BaseAdapter {

    private ViewHolder viewHolder;
    private ArrayList<Sector> sector;
    private Activity activity;

    public SectorAdapter(Activity activity, ArrayList<Sector> sector) {
        this.activity = activity;
        this.sector = sector;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_spinner, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.spinnerText = convertView.findViewById(R.id.spinner_text);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Sector sectorName = sector.get(position);
        viewHolder.spinnerText.setText(sectorName.getSetcorName());
        return convertView;
    }

    @Override
    public int getCount() {
        return sector.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder {
        TextView spinnerText;
    }
}