package com.socarmap;

import org.oscim.view.Compass;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddDegree extends Activity {
	EditText etAngle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_degree);
		etAngle = (EditText) findViewById(R.id.etAngle);
		etAngle.setText(Compass.add_degree + "");
		((Button) findViewById(R.id.btnSetAngle))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							String txt = etAngle.getText().toString();
							Compass.add_degree = Float.parseFloat(txt);
							try {
								MainActivity.instance
										.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												try {
													MainActivity.instance.mapView
															.invalidate();
												} catch (Throwable e) {
													// TODO: handle exception
												}

											}
										});
							} catch (Throwable e) {
								// TODO: handle exception
							}
							finish();
						} catch (Throwable e) {
							// TODO: handle exception
						}

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_degree, menu);
		return true;
	}

}
