package com.socarmap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.socarmap.helper.ConnectionHelper;
import com.socarmap.ui.process.IProcess;
import com.socarmap.ui.process.ProcessExecutor;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		ConnectionHelper.init(this);
		// ConnectionHelper.inprogressDialog = new ProgressDialog(activity);
		((Button) findViewById(R.id.button_test))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						ping();

					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

	protected void ping() {
		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Throwable {
				ConnectionHelper.uploadFile("dito",
						"skdjflsjdflskdjflskjfd".getBytes());

			}
		}, this);

	}

}
