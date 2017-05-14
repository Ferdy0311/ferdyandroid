package com.bahaso.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.bahaso.R;
import com.bahaso.adapter.CountryListAdapter;
import com.bahaso.model.Country;


public class CountryPickerDialog extends AlertDialog {
    private static final String COUNTRY_CODE = "ctry";

    String mCountryCode;
    ListView mListViewCountry;
    CountryListAdapter mCountryListAdapter;

    OnCountrySetListener mOnCountrySetListener;
    public interface OnCountrySetListener {
        void onCountrySet(Country country);
    }

    public CountryPickerDialog(Context context, int theme, OnCountrySetListener listener,
                               int selectedIndex) {
        super(context, theme);

        mOnCountrySetListener = listener;

        // final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.country_picker_dialog, null);
        setView(view);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        if (mOnCountrySetListener != null &&
                                mListViewCountry.getCheckedItemPosition() >= 0) {
                            mOnCountrySetListener.onCountrySet(
                                    (Country) mCountryListAdapter.getItem(
                                            mListViewCountry.getCheckedItemPosition()));
                        }
                        break;
                    case BUTTON_NEGATIVE:
                        cancel();
                        break;
                }
            }
        };
        setButton(BUTTON_POSITIVE, context.getString(R.string.ok), onClickListener);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), onClickListener);

        mCountryListAdapter = new CountryListAdapter(context);
        mListViewCountry = (ListView) view.findViewById(R.id.listview_countrypicker);
        mListViewCountry.setAdapter(mCountryListAdapter);

        if (selectedIndex > -1) {
            mListViewCountry.setItemChecked(selectedIndex, true);
            mListViewCountry.setSelection(selectedIndex);
        }
//        mListViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                view.setSelected(true);
//            }
//        });

//        String[] data = { "1", "2","3", "4", "5","6", "7","8" };
//        mListViewCountry = (ListView) view.findViewById(R.id.listview_countrypicker);
//        mListViewCountry.setAdapter(new ArrayAdapter<>(getContext(),
//                android.R.layout.simple_list_item_single_choice, data));
    }

    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putString(COUNTRY_CODE, mCountryCode);
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCountryCode = savedInstanceState.getString(COUNTRY_CODE);
    }
}
