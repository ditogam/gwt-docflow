package com.spatialite.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.spatialite.R;
import com.spatialite.beans.Balance;
import com.spatialite.beans.Meter;
import com.spatialite.db.DBLoader;
import com.spatialite.utilities.ActivityHelper;

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
				DBLoader.initInstance(getString(R.string.test_db));
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
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ActivityHelper.showAlert(this, sw.toString());
		}

		lvCustomersDetail.setAdapter(adapter);
	}

}
