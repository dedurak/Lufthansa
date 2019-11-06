package com.example.lufthansa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
    private Context ctx;
    private int logos[];
    private String[] carrierShortcut;
    private LayoutInflater inflater;

    public MyAdapter(Context ctx, int logos[], String[] carrierShortcut){
        this.ctx = ctx;
        this.logos = logos;
        this.carrierShortcut = carrierShortcut;
        inflater = (LayoutInflater.from(ctx));
    }

    @Override
    public int getCount() {
        return logos.length;
    }

    @Override
    public Object getItem(int i) {
        return carrierShortcut[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_item, null);
        ImageView image = view.findViewById(R.id.spinner_image);
        TextView text = view.findViewById(R.id.spinner_text);
        image.setImageResource(logos[i]);
        text.setText(carrierShortcut[i]);
        return view;
    }
}
