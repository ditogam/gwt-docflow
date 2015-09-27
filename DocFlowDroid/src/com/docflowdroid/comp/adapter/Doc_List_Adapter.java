package com.docflowdroid.comp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.docflowdroid.R;

public class Doc_List_Adapter extends ArrayAdapter<Doc_List_item_Helper> {
	private ArrayList<Doc_List_item_Helper> items;

	public Doc_List_Adapter(Context context,
			ArrayList<Doc_List_item_Helper> items) {
		super(context, R.layout.document_list_row, items);
		this.items = items;
	}

	public ArrayList<Doc_List_item_Helper> getItems() {
		return items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Doc_List_item_Helper item = getItem(position);
		if (item == null)
			return super.getView(position, convertView, parent);
		convertView = item.getMy_view(convertView == null);
		return convertView;
	}
}
