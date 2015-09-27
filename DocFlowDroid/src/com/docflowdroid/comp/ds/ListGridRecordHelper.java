package com.docflowdroid.comp.ds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.shared.ds.DsFieldType;
import com.docflowdroid.DocFlow;
import com.docflowdroid.R;

public class ListGridRecordHelper {
	private Map<String, Object> data;
	private ListGridField[] fields;
	private int rownum;
	private boolean shownum;
	private LinearLayout view;
	private Map<String, View> mptexts = new HashMap<String, View>();

	public ListGridRecordHelper(int rownum, boolean shownum,
			Map<String, Object> data, ListGridField[] fields) {
		this.data = data;
		setParams(rownum, shownum, fields);

	}

	private void setParams(int rownum, boolean shownum, ListGridField[] fields) {
		this.fields = fields;
		this.rownum = rownum;
		this.shownum = shownum;
	}

	public ListGridRecordHelper(int rownum, boolean shownum,
			ListGridField[] fields) {
		setParams(rownum, shownum, fields);

	}

	public void clearData() {
		this.data = null;
		if (view != null) {

			view.removeAllViews();
			mptexts.clear();
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
			view = null;
		}
	}

	public Map<String, Object> getData() {
		return data;

	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public View getView(Context context) {
		if (data == null) {
			if (view == null && shownum) {
				createView(context);
				addNowNum(context);
				View v = view;
				view = null;
				return v;
			}

		}
		if (view == null) {
			createView(context);
			if (shownum)
				addNowNum(context);
			for (ListGridField f : fields) {
				DsFieldType type = DsFieldType.TEXT;
				try {
					// type = DsFieldType.valueOf(f.getType());
				} catch (Throwable e) {
					type = DsFieldType.TEXT;
					// e.printStackTrace();
				}
				View tv = type.equals(DsFieldType.BOOLEAN) ? new CheckBox(
						context) : new TextView(context);
				if (tv instanceof CheckBox)
					((CheckBox) tv).setEnabled(false);
				mptexts.put(f.getFieldName(), tv);
				LGFieldView.setWidthToView(tv, f.getFieldWidth());
				view.addView(tv);
			}
		}
		Set<String> keys = mptexts.keySet();
		for (String key : keys) {
			View v = mptexts.get(key);
			if (v instanceof CheckBox)
				((CheckBox) v).setChecked(false);
			if (v instanceof TextView)
				((TextView) v).setText(null);
		}
		for (ListGridField f : fields) {
			View v = mptexts.get(f.getFieldName());
			if (v == null)
				continue;
			if (v instanceof TextView) {
				TextView tv = (TextView) v;
				tv.setSingleLine(!f.isWrapText());
				tv.setText(getText(f));
			}
			if (v instanceof CheckBox) {
				Boolean o = getBooleanValue(data.get(f.getFieldName()));
				if (o != null)
					((CheckBox) v).setChecked(o.booleanValue());
			}
		}
		return view;
	}

	private void createView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (LinearLayout) inflater.inflate(R.layout.lg_row, null);
	}

	private void addNowNum(Context context) {
		TextView tv = new TextView(context);
		LGFieldView.setWidthToView(tv, ListGridView.ROWNUM_WIDTH);
		tv.setText("" + rownum);
		view.addView(tv);
	}

	private Boolean getBooleanValue(Object o) {
		Boolean b = null;
		if (o != null) {

			if (o instanceof Boolean)
				b = (Boolean) o;
			else {
				try {
					b = Boolean.valueOf(o.toString());
				} catch (Exception e) {
				}
				if (b == null) {
					try {
						b = Integer.parseInt(o.toString()) == 1;
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
		return b;
	}

	private String getText(ListGridField f) {
		Object o = data.get(f.getFieldName());
		String result = null;
		if (o != null) {

			DsFieldType type = null;
			try {
				type = DsFieldType.valueOf(f.getType());
			} catch (Throwable e) {
				type = DsFieldType.TEXT;
				// e.printStackTrace();
			}
			if (type.equals(DsFieldType.DATE)
					|| type.equals(DsFieldType.DATETIME)) {
				Date d = null;
				SimpleDateFormat format = type.equals(DsFieldType.DATETIME) ? DocFlow.DATE_TIME_FORMAT
						: DocFlow.DATE_FORMAT;
				if (o instanceof Date) {
					d = (Date) o;
					result = format.format(d);
				} else
					try {
						long l = Long.valueOf(o.toString().trim());
						d = new Date(l);
					} catch (Exception e) {
						try {
							d = format.parse(o.toString());
						} catch (Exception e2) {

						}
					}
				if (d != null)
					result = format.format(d);
			}
			if (result == null)
				result = o.toString();

		}
		return result;
	}
}
