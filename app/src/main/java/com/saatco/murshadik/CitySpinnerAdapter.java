package com.saatco.murshadik;

import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
 import android.content.Context;
import android.view.View;
        import android.view.ViewGroup;
import android.widget.TextView;

public class CitySpinnerAdapter  extends BaseAdapter implements SpinnerAdapter {

    String[] company;
    Context context;

    public CitySpinnerAdapter(Context context, String[] company) {
        this.company = company;
        this.context = context;
    }

    @Override
    public int getCount() {
        return company.length;
    }

    @Override
    public Object getItem(int position) {
        return company[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  View.inflate(context, R.layout.city_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.dropdown);
        textView.setText(company[position]);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view;
        view =  View.inflate(context, R.layout.city_layout, null);
        final TextView textView = (TextView) view.findViewById(R.id.dropdown);
        textView.setText(company[position]);


        return view;
    }
}