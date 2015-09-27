package com.docflowdroid.comp;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import com.docflowdroid.DocFlow;
import com.docflowdroid.common.comp.ICurrentTimeItem;

public class CurrentTimeItem extends StaticTextItem implements
		ICurrentTimeItem, OnDateSetListener {

	public CurrentTimeItem(Context context) {
		super(context);
		addList();
		setCurrentDate();
	}

	public CurrentTimeItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		addList();
		setCurrentDate();
	}

	public CurrentTimeItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		addList();
		setCurrentDate();
	}

	private void addList() {
		valueItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(getValueAsDate());
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH);
					int day = calendar.get(Calendar.DAY_OF_MONTH);
					DatePickerDialog dp = new DatePickerDialog(v.getContext(),
							CurrentTimeItem.this, year, month, day);
					DatePicker dpC = dp.getDatePicker();
					Calendar cal = GregorianCalendar.getInstance();
					cal.add(Calendar.YEAR, -1);
					dpC.setMinDate(cal.getTimeInMillis() - 1000);
					cal.add(Calendar.YEAR, 2);
					dpC.setMaxDate(cal.getTimeInMillis() - 1000);
					dp.show();
				} catch (Throwable e) {
					// ActivityHelper.showAlert(context, e);
				}

			}
		});
	}

	private void setCurrentDate() {
		setValue(DocFlow.getCurrentDate());
	}

	public void setValue(final Date value) {
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

	private void setValuePrivate(Date value) {
		value = value == null ? DocFlow.getCurrentDate() : value;
		setValue(value.getTime() + "");
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
		value = value == null ? "" : value.toString().trim();
		Date date = null;
		try {
			date = DocFlow.DATE_FORMAT.parse(value.toString());
		} catch (Exception e) {
			try {
				date = new Date(Long.parseLong(value.toString()));
			} catch (Exception e2) {
				setCurrentDate();
				return;
			}
		}
		valueItem.setText(DocFlow.DATE_FORMAT.format(date));
	}

	@Override
	public String getValue() {
		try {
			return DocFlow.DATE_FORMAT.parse(valueItem.getText().toString())
					.getTime() + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public Date getValueAsDate() {
		try {
			return DocFlow.DATE_FORMAT.parse(valueItem.getText().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static long trimDate(long millisecond) {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(millisecond);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.HOUR, 0);
		long result = c.getTimeInMillis();
		return result;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		setValue(calendar.getTime());

	}

}
