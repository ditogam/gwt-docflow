package com.docflowdroid;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.docflowdroid.traffic.NetTraffic;

public class DataTraffic extends Activity {
	NetTraffic latest = null;
	NetTraffic previous = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DocFlow.setActivityLandscape(this);

		setContentView(R.layout.activity_data_traffic);
		takeSnapshot(null);
	}

	public void takeSnapshot(View v) {
		previous = latest;
		latest = DocFlow.getTraffic(true);
		setText(R.id.latest_rx, R.id.latest_tx, latest.getRecieved(),
				latest.getSent());
		setText(R.id.latest_mob_rx, R.id.latest_mob_tx,
				latest.getRecievedMobile(), latest.getSentMobile());

		setText(R.id.latest_myapp_rx, R.id.latest_myapp_tx,
				latest.getRecievedApp(), latest.getSentApp());
	}

	private void setText(int reciever_id, int sent_id, long reciever, long sent) {
		((TextView) findViewById(reciever_id)).setText(getMBValue(reciever));
		((TextView) findViewById(sent_id)).setText(getMBValue(sent));
	}

	DecimalFormat twoDForm = new DecimalFormat("#.##");

	private String getMBValue(long val) {
		String v = getString(R.string.bt);
		double value = val;
		double div = 1024;
		if (value > div) {
			v = getString(R.string.kb);
			value = value / div;
		}
		if (value > div) {
			v = getString(R.string.mb);
			value = value / div;
		}
		return (value <= 0) ? "_" : (twoDForm.format(value) + v);
	}

}
