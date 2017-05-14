package com.bahaso.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.ModelPaymentHistory;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by ASUS on 3/16/2017.
 */

public class PaymentHistoryAdapter extends BaseAdapter {

    private Activity activity;
    private List<ModelPaymentHistory>historyItems;
    private static LayoutInflater inflater = null;

    public PaymentHistoryAdapter(Activity act, List<ModelPaymentHistory>historyItems){
        activity = act;
        inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.historyItems = historyItems;
    }

    @Override
    public int getCount() {
        return historyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return historyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if(convertView==null)
            convertView = inflater.inflate(R.layout.payment_history_list_row, null);

        holder.paymentHistoryDate = (TextView) convertView.findViewById(R.id.payment_history_date);
        holder.paymentHistoryStatus = (TextView) convertView.findViewById(R.id.payment_history_status);
        holder.paymentHistoryAmountGold = (TextView) convertView.findViewById(R.id.payment_history_amountgold);
        holder.paymentHistoryTotalPrice = (TextView) convertView.findViewById(R.id.payment_history_price);
        holder.paymentHistoryTransactionID = (TextView)convertView.findViewById(R.id.payment_history_transaction_id);

        ModelPaymentHistory modelPaymentHistory = historyItems.get(position);

        holder.paymentHistoryDate.setText(modelPaymentHistory.getDateHistoryPayment());
        String amountGold = String.valueOf(modelPaymentHistory.getHistoryAmountGold()) + " GOLD";
        holder.paymentHistoryAmountGold.setText(amountGold);

        //*************** Price Gold ****************//
        DecimalFormat formatter = new DecimalFormat("###,###,###,###");
        long LongNum = Long.valueOf(String.valueOf(modelPaymentHistory.getHistoryTotalPrice()));
        String priceGoldIDR = "IDR " + formatter.format(LongNum);
        holder.paymentHistoryTotalPrice.setText(priceGoldIDR);
        //*************** /Price Gold ****************//

        holder.paymentHistoryTransactionID.setText(String.valueOf(modelPaymentHistory.getHistoryTransactionID()));

        GlobalVar global = (GlobalVar)activity.getApplicationContext();

        if (modelPaymentHistory.getHistoryPaymentStatus().equals("expired")){
            if (global.getAPIversion() >= 23) {
                holder.paymentHistoryStatus.setTextColor(activity.getResources().getColor(R.color.bahaso_darker_red, null));
                holder.paymentHistoryStatus.setText(activity.getString(R.string.payment_fail));
            } else {
                //noinspection deprecation
                holder.paymentHistoryStatus.setTextColor(activity.getResources().getColor(R.color.bahaso_darker_red));
                holder.paymentHistoryStatus.setText(activity.getString(R.string.payment_fail));
            }
        } else if (modelPaymentHistory.getHistoryPaymentStatus().equals("settlement") ||
                modelPaymentHistory.getHistoryPaymentStatus().equals("capture")) {
            if (global.getAPIversion() >= 23) {
                holder.paymentHistoryStatus.setTextColor(activity.getResources().getColor(R.color.bahaso_blue, null));
                holder.paymentHistoryStatus.setText(activity.getString(R.string.payment_success));
            } else {
                holder.paymentHistoryStatus.setTextColor(activity.getResources().getColor(R.color.bahaso_blue));
                holder.paymentHistoryStatus.setText(activity.getString(R.string.payment_success));
            }
        } else {
            if (global.getAPIversion() >= 23) {
                holder.paymentHistoryStatus.setTextColor(activity.getResources().getColor(R.color.bahaso_dark_gray, null));
                holder.paymentHistoryStatus.setText(activity.getString(R.string.payment_wait_confirm));
            } else {
                holder.paymentHistoryStatus.setTextColor(activity.getResources().getColor(R.color.bahaso_dark_gray));
                holder.paymentHistoryStatus.setText(activity.getString(R.string.payment_wait_confirm));
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView paymentHistoryDate;
        TextView paymentHistoryStatus;
        TextView paymentHistoryAmountGold;
        TextView paymentHistoryTotalPrice;
        TextView paymentHistoryTransactionID;
    }
}
