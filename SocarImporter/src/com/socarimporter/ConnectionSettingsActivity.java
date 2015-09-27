package com.socarimporter;

import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionSettingsActivity extends Activity {
	private EditText eUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection_settings);
		eUrl = (EditText) findViewById(R.id.eUrl);
		eUrl.setText(LoginActivitie.settings.getServer_url());
		((Button) findViewById(R.id.btn_save))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							String sUrl = eUrl.getText().toString().trim();
							new URL(sUrl);
							LoginActivitie.settings.setServer_url(sUrl);
							LoginActivitie.settings
									.saveFile(ConnectionSettingsActivity.this);
							Utils.init(LoginActivitie.settings.getServer_url());
							finish();
						} catch (Exception e) {
							eUrl.setError(e.getMessage());
						}

					}
				});
	}

}
