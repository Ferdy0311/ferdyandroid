package com.bahaso.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bahaso.R;

/**
 * Created by hendrysetiadi on 24/03/2016.
 */
public class DividerSettingsItem extends BaseSettingsItem {

    public DividerSettingsItem(){
        super (-1, false);
    }
    private static class ViewHolder {
        private View view;
        private ViewHolder(View view) {
            this.view = view;
        }
    }

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_settings_divider, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Object tag = convertView.getTag();
            if (null != tag && tag instanceof DividerSettingsItem.ViewHolder) {
                viewHolder = (DividerSettingsItem.ViewHolder) tag;
            }
            else {
                convertView = inflater.inflate(R.layout.item_listview_settings_divider, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
        }

        viewHolder.view.setClickable(false);
        viewHolder.view.setEnabled(false);
        viewHolder.view.setMinimumHeight(1);

        //set the color for the divider
        // viewHolder.divider.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(parent.getContext(), R.attr.material_drawer_divider,
        // R.color.material_drawer_divider));
        return convertView;
    }
}
