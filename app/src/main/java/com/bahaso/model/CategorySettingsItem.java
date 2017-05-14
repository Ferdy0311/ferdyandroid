package com.bahaso.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bahaso.R;

/**
 * Created by hendrysetiadi on 24/03/2016.
 */
public class CategorySettingsItem extends BaseSettingsItem {
    private String title;

    public CategorySettingsItem(int id, boolean enabled, String title) {
        super(id, enabled);
        this.title = title;
    }

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_settings_cat, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Object tag = convertView.getTag();
            if (null != tag && tag instanceof CategorySettingsItem.ViewHolder) {
                viewHolder = (CategorySettingsItem.ViewHolder) tag;
            }
            else {
                convertView = inflater.inflate(R.layout.item_listview_settings_cat, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
        }

        viewHolder.tvTitle.setText(title);

        viewHolder.view.setEnabled(enabled);

        return convertView;
    }

    public String getTitle() {
        return title;
    }


    private static class ViewHolder {
        private View view;
        private TextView tvTitle;
        private ViewHolder(View view) {
            this.view = view;
            this.tvTitle = (TextView) view.findViewById(android.R.id.title);
        }
    }

}
