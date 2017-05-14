package com.bahaso.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.bahaso.model.BaseSettingsItem;

import java.util.List;

/*
Main Settings Fragment
-------------------
  ---------------
| Main Settings | -- CLicked
 ----------------
   ---------------
| Main Settings | -- CLicked
 ----------------
   ---------------
| Main Settings | -- CLicked
 ----------------
 -------------------
 */

public class MainSettingsBaseAdapter extends BaseAdapter {
    private List<BaseSettingsItem> baseSettingsItems;
    private LayoutInflater mInflater;

    public MainSettingsBaseAdapter(Context context, List<BaseSettingsItem> baseSettingsItems){
        this.baseSettingsItems = baseSettingsItems;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseSettingsItem item = (BaseSettingsItem) getItem(position);
        View view = item.convertView(mInflater, convertView, parent);
        return view;
    }

    @Override
    public Object getItem(int position) {
        return baseSettingsItems.get(position);
    }

    @Override
    public int getCount() {
        return baseSettingsItems.size();
    }

    @Override
    public long getItemId(int position) {
        return baseSettingsItems.get(position).getId();
    }

    @Override
    public boolean isEnabled(int position) {
        return baseSettingsItems.get(position).isEnabled();
    }

    // to make the posititon state selected
//    public void setSelected (int position) {
//        selectedPosition = position;
//        notifyDataSetChanged();
//    }
}

