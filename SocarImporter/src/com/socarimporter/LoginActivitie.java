package com.socarimporter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivitie extends Activity implements OnClickListener {
	public static Settings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (settings == null)
			try {
				settings = Settings.load(this);
				Utils.init(settings.getServer_url());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		setContentView(R.layout.login_request);
		Button bLogin = (Button) findViewById(R.id.btnLogin);
		Button bCancel = (Button) findViewById(R.id.btnCancel);
		bLogin.setOnClickListener(this);
		bCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnCancel) {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
			return;
		}
		EditText tiuser_name = (EditText) findViewById(R.id.txtUname);
		EditText ttpassword = (EditText) findViewById(R.id.txtPwd);
		String user_name = tiuser_name.getText().toString().trim();
		String password = ttpassword.getText().toString().trim();

		if (user_name.isEmpty() || password.isEmpty()) {
			ActivityHelper.showAlert(this, "Please enter username&password!!!");
			return;
		}

		try {
			byte[] bytesOfMessage = password.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);

			BigInteger bigInt = new BigInteger(1, thedigest);
			String hashtext = bigInt.toString(16);
			password = URLDecoder.decode(hashtext);
			thedigest = Utils.downlodStream("loginuser.jsp?user_name="
					+ user_name + "&pwd=" + password);
			password = new String(thedigest);
			password = password.trim();
			Integer.parseInt(password);
			ttpassword.setText("");
			Intent main = new Intent(this, MainActivity.class);
			startActivity(main);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ActivityHelper.showAlert(this, sw.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_connection_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_settings:
			Intent myIntent = new Intent(this, ConnectionSettingsActivity.class);
			startActivity(myIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
