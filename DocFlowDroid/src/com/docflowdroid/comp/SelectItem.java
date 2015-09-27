package com.docflowdroid.comp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.common.FieldDefinition;
import com.docflowdroid.R;
import com.docflowdroid.common.comp.IComboBoxItem;
import com.docflowdroid.comp.adapter.IDValueAdapter;

public class SelectItem extends FormItem implements IComboBoxItem {
	protected Spinner spinner;
	private IDValueAdapter adapter;

	public SelectItem(Context context) {
		super(context);
		spinner = new IgnoreFirstSpinner(context);
		adapter = new IDValueAdapter(new ArrayList<ClSelectionItem>(), context);
		spinner.setAdapter(adapter);
		ViewGroup.LayoutParams params = spinner.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
		params.width = LinearLayout.LayoutParams.MATCH_PARENT;
		// params.width=200;
		spinner.setLayoutParams(params);
		spinner.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				createFilterWindow();
				return false;
			}
		});
	}

	protected void createFilterWindow() {
		View v = ((Activity) this.getContext()).getLayoutInflater().inflate(
				R.layout.combobox_filter, null);
		int count = spinner.getAdapter().getCount();
		ArrayList<ClSelectionItem> list = new ArrayList<ClSelectionItem>();
		for (int i = 0; i < count; i++) {
			list.add((ClSelectionItem) spinner.getAdapter().getItem(i));
		}
		ListView lv = (ListView) v.findViewById(R.id.lvFilterCombo);

		final FilteredArrayAdapter<ClSelectionItem> adapter = new FilteredArrayAdapter<ClSelectionItem>(
				this.getContext(), R.layout.list_item, R.id.cl_select_item,
				list);
		lv.setAdapter(adapter);

		EditText inputSearch = (EditText) v.findViewById(R.id.eFilterCombo);
		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
		final AlertDialog alertDialog = new AlertDialog.Builder(
				this.getContext()).create();
		// alertDialog.setTitle("Info");
		alertDialog.setView(v);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "close",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					setValue(adapter.getItem(position).getId());
					alertDialog.dismiss();
				} catch (Throwable e) {
					// TODO: handle exception
				}

			}
		});
		alertDialog.show();

	}

	@Override
	public void setField(FieldDefinition field) {
		// TODO Auto-generated method stub
		super.setField(field);
		adapter.setField(field);
	}

	@Override
	public void setValue(final Object value) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			setValuePrivate(value);
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValuePrivate(value);
				}
			});
		}
	}

	private void setValuePrivate(Object value) {
		try {
			System.out.println(field);
			long val = Long.parseLong(value.toString().trim());
			int pos = adapter.getPosition(val);
			spinner.setSelection(pos);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public String getDisplayValue() {
		int pos = spinner.getSelectedItemPosition();
		try {
			return ((ClSelectionItem) adapter.getItem(pos)).getValue();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getValue() {
		int pos = spinner.getSelectedItemPosition();
		try {
			return ((ClSelectionItem) adapter.getItem(pos)).getId() + "";
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public View getComponent() {
		// TODO Auto-generated method stub
		return spinner;
	}

	@Override
	public void setValueMap(final HashMap<Long, String> map) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			adapter.setValues(map);
			adapter.notifyDataSetChanged();
			try {
				setValue(getInitialValue());
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValueMap(map);
				}
			});
		}

	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		spinner.setOnItemSelectedListener(listener);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			super.setEnabled(enabled);
			spinner.setEnabled(enabled);
			spinner.setClickable(enabled);
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setEnabled(enabled);
				}
			});
		}

	}

	@Override
	public void requestFocusSelf() {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			spinner.requestFocus();
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					requestFocusSelf();
				}
			});
		}
	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		// TODO Auto-generated method stub
		return spinner.getOnFocusChangeListener();
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener newList) {
		spinner.setOnFocusChangeListener(newList);

	}
}
