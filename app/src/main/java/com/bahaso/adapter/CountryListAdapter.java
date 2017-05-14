package com.bahaso.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.model.Country;
import com.bahaso.util.CountryHelper;

import java.util.List;

public class CountryListAdapter extends BaseAdapter {

	private Context context;
	LayoutInflater inflater;

	public CountryListAdapter(Context context) {
		super();
		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return CountryHelper.getCountryList(context).size();
	}

	@Override
	public Object getItem(int arg0) {
		return CountryHelper.getCountryList(context).get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * Return row for each country
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cellView = convertView;
		Cell cell;
		List<Country> countryList = CountryHelper.getCountryList(context);
		Country country = countryList.get(position);

		if (convertView == null) {
			cell = new Cell();
			cellView = inflater.inflate(R.layout.item_country, null);
			cell.textView = (TextView) cellView.findViewById(R.id.tv_country_name);
			cell.imageView = (ImageView) cellView.findViewById(R.id.iv_icon);
			cellView.setTag(cell);
		} else {
			cell = (Cell) cellView.getTag();
		}
		cell.textView.setText(country.getName());
		cell.imageView.setImageResource(countryList.get(position).getFlagResource(context));
		return cellView;
	}

	/**
	 * Holder for the cell
	 * 
	 */
	static class Cell {
		public TextView textView;
		public ImageView imageView;
	}

}