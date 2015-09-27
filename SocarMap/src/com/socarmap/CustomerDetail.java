package com.socarmap;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.socarmap.db.DBLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.proxy.beans.Balance;
import com.socarmap.proxy.beans.Meter;

public class CustomerDetail extends Activity {
	private ListView lvCustomersDetail;
	private Long cusid;
	private int detail_type;
	public static int DT_BALANCE = 1;
	public static int DT_METER = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		cusid = b.getLong("cusid");
		detail_type = b.getInt("detail_type");
		setContentView(R.layout.activity_customer_detail);
		lvCustomersDetail = (ListView) findViewById(R.id.lv_customer_detail);
		ArrayAdapter<?> adapter = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_expandable_list_item_1,
				new ArrayList<String>());
		try {
			if (DBLoader.getInstance() == null) {
				DBLoader.initInstance(getString(R.string.socar_db));
			}
			if (detail_type == DT_BALANCE) {
				ArrayList<Balance> myList = DBLoader.getInstance().loadBalance(
						cusid);
				adapter = new ArrayAdapter<Balance>(getBaseContext(),
						android.R.layout.simple_expandable_list_item_1, myList);
			} else {
				ArrayList<Meter> myList = DBLoader.getInstance().loadMeters(
						cusid);
				adapter = new ArrayAdapter<Meter>(getBaseContext(),
						android.R.layout.simple_expandable_list_item_1, myList);
			}
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
		}

		lvCustomersDetail.setAdapter(adapter);
	}

}
