package com.bahaso.model;

import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bahaso.R;

/**
 * Created by hendrysetiadi on 05/09/2016.
 */
public class SwitchSettingsItem extends BaseSettingsItem
    implements View.OnClickListener{
    String title;
    String summary;
    boolean defaultIsChecked;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public SwitchSettingsItem(int id, String title,
                              String summary,
                              boolean defaultIsChecked,
                              CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        super(id, true);
        this.title = title;
        this.summary = summary;
        this.defaultIsChecked = defaultIsChecked;
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_switchsettings, parent, false);
            viewHolder = new ViewHolder(convertView, defaultIsChecked,  onCheckedChangeListener);
            convertView.setTag(viewHolder);
        } else {
            Object tag = convertView.getTag();
            if (null != tag && tag instanceof SwitchSettingsItem.ViewHolder) {
                viewHolder = (SwitchSettingsItem.ViewHolder) tag;
            }
            else {
                convertView = inflater.inflate(R.layout.item_listview_switchsettings, parent, false);
                viewHolder = new ViewHolder(convertView, defaultIsChecked,  onCheckedChangeListener);
                convertView.setTag(viewHolder);
            }
        }

        viewHolder.tvTitle.setText(title);

        if (null == summary) {
            viewHolder.tvSummary.setVisibility(View.GONE);
        }
        else {
            viewHolder.tvSummary.setText(summary);
            viewHolder.tvSummary.setVisibility(View.VISIBLE);
        }

        viewHolder.view.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        ((SwitchCompat) view.findViewById(R.id.switch_compat)).toggle();
    }

    public String getTitle() {
        return title;
    }


    private static class ViewHolder {
        private TextView tvTitle;
        private TextView tvSummary;
        private SwitchCompat switchCompat;
        private View view;
        private ViewHolder(View view,
                           boolean defaultIsChecked,
                           CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            this.view = view;
            this.tvTitle = (TextView) view.findViewById(android.R.id.title);
            this.tvSummary = (TextView) view.findViewById(android.R.id.summary);
            this.switchCompat = (SwitchCompat) view.findViewById(R.id.switch_compat);
            switchCompat.setChecked(defaultIsChecked);
            switchCompat.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

}