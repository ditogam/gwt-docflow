package com.docflowdroid.comp.ds;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.common.shared.ds.DsFieldType;
import com.docflowdroid.R;
import com.docflowdroid.comp.CurrentTimeItem;

public class LGFieldView {

	private ListGridField field;
	private Context context;
	private View myview;
	private LinearLayout myFilterView;
	private View myFilterEdit;
	private TextView lff_title;
	private boolean showFilterHeader;

	public LGFieldView(Context context, ListGridField field,
			boolean showFilterHeader) {
		this.context = context;
		this.showFilterHeader = showFilterHeader;
		setField(context, field);
	}

	private void setField(Context context, ListGridField field) {
		this.field = field;
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myview = inflater.inflate(R.layout.lg_header_item, null);
		myFilterView = (LinearLayout) myview.findViewById(R.id.lff_filter);
		lff_title = (TextView) myview.findViewById(R.id.lff_title);
		refreshView();
	}

	public void refreshView() {
		lff_title.setText(field.getFieldTitle());
		boolean visible = showFilterHeader
				|| !(field.getShowFilter() == null || !field.getShowFilter()
						.booleanValue());
		myFilterView.setVisibility(!visible ? View.GONE : View.VISIBLE);
		setWidthToView(myview, field.getFieldWidth());
		createFilterComponent(visible);
	}

	public static void setWidthToView(View v, Long width) {
		android.view.ViewGroup.LayoutParams p = v.getLayoutParams();
		if (p == null) {
			p = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			v.setLayoutParams(p);
		}
		p.width = width == null ? LayoutParams.MATCH_PARENT : width.intValue();
		System.out.println(p.width);
	}

	private void createFilterComponent(boolean visible) {
		if (!visible)
			return;
		if (myFilterEdit != null)
			return;
		if (field.isRowNum()) {
			myFilterEdit = new TextView(context);
		} else {
			DsFieldType type = null;
			try {
				type = DsFieldType.valueOf(field.getType());
			} catch (Throwable e) {
				type = DsFieldType.TEXT;
			}
			switch (type) {
			case TEXT:
			case INTEGER:
			case FLOAT:
				EditText text = new EditText(context);
				text.setEms(10);
				text.setSingleLine();

				myFilterEdit = text;
				if (type.equals(DsFieldType.INTEGER))
					text.setInputType(InputType.TYPE_CLASS_NUMBER
							| InputType.TYPE_NUMBER_VARIATION_NORMAL);
				if (type.equals(DsFieldType.FLOAT))
					text.setInputType(InputType.TYPE_CLASS_NUMBER
							| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				break;
			case DATE:
			case DATETIME:

				myFilterEdit = new CurrentTimeItem(context).getComponent();
				break;
			default:
				break;
			}
		}
		if (myFilterEdit == null)
			return;

		ViewGroup.LayoutParams params = myFilterEdit.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
		myFilterEdit.setLayoutParams(params);
		myFilterView.addView(myFilterEdit, params);
	}

	public ListGridField getField() {
		return field;
	}

	public View getMyview() {
		return myview;
	}
}
