package com.bahaso.model;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;

/**
 * Created by hendrysetiadi on 24/03/2016.
 */
public class IconSettingsItem extends BaseSettingsItem {
    int resIconSrc = -1;
    String title;
    String summary;

    public IconSettingsItem(int id, boolean enabled, int resIconSrc, String title,
                            String summary) {
        super(id, enabled);
        if (resIconSrc > -1) {
            this.resIconSrc = resIconSrc;
        }
        this.title = title;
        this.summary = summary;
    }

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_iconsettings, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Object tag = convertView.getTag();
            if (null != tag && tag instanceof IconSettingsItem.ViewHolder) {
                viewHolder = (IconSettingsItem.ViewHolder) tag;
            }
            else {
                convertView = inflater.inflate(R.layout.item_listview_iconsettings, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
        }

        if (resIconSrc > 0) {
            viewHolder.ivIcon.setImageResource(resIconSrc);
            viewHolder.ivIcon.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.ivIcon.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(title);

        if (null == summary) {
            viewHolder.tvSummary.setVisibility(View.GONE);
        }
        else {
            viewHolder.tvSummary.setText(summary);
            viewHolder.tvSummary.setVisibility(View.VISIBLE);
        }

        viewHolder.view.setEnabled(enabled);
        if (enabled) {
            viewHolder.ivIcon.clearColorFilter();
            viewHolder.tvTitle.setTextColor(
                    ContextCompat.getColor(GlobalVar.getInstance().getApplicationContext(), R.color.bahaso_black_gray));
        }
        else {
            viewHolder.ivIcon.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            viewHolder.tvTitle.setTextColor(
                    ContextCompat.getColor(GlobalVar.getInstance().getApplicationContext(), R.color.bahaso_light_dark_gray));
        }

        return convertView;
    }

    public String getTitle() {
        return title;
    }

    public int getResIconSrc() {
        return resIconSrc;
    }

    private static class ViewHolder {
        private View view;
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvSummary;
        private ViewHolder(View view) {
            this.view = view;
            this.ivIcon = (ImageView) view.findViewById(R.id.iv_navigation_icon);
            this.tvTitle = (TextView) view.findViewById(android.R.id.title);
            this.tvSummary = (TextView) view.findViewById(android.R.id.summary);
        }
    }

}

