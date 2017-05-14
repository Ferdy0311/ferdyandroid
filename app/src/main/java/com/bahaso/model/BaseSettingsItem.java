package com.bahaso.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hendrysetiadi on 24/03/2016.
 */
public abstract class BaseSettingsItem {
    int id;
    boolean enabled;

    public BaseSettingsItem(int id, boolean enabled){
        this.id = id;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract View convertView (LayoutInflater inflater,
                                      View convertView,
                                      ViewGroup parent);

    public int getId() {
        return id;
    }


}
