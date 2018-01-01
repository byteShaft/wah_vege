package com.byteshaft.wahwege.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byteshaft.wahwege.R;
import com.byteshaft.wahwege.contactdetails.Faq;
import com.byteshaft.wahwege.gettersetter.Faqs;

import java.util.ArrayList;

/**
 * Created by husnain on 12/12/17.
 */

public class FaqsAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<Faqs> arrayList;
    private Activity activity;

    public FaqsAdapter(Activity activity, ArrayList<Faqs> arrayList) {
        super(activity.getApplication(), R.layout.delegate_faqs);
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_faqs, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.FaqsQuestion = convertView.findViewById(R.id.question_text_view);
            viewHolder.FaqsAnswer = convertView.findViewById(R.id.answer_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Faqs faqs = arrayList.get(position);
        viewHolder.FaqsQuestion.setText(faqs.getFaqsQestion());
        viewHolder.FaqsAnswer.setText(faqs.getFaqsAnswer());
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {
        TextView FaqsQuestion;
        TextView FaqsAnswer;
    }
}
