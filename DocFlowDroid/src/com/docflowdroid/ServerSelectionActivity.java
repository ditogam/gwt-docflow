package com.docflowdroid;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.docflowdroid.conf.Config;
import com.docflowdroid.conf.Server;

public class ServerSelectionActivity extends Activity {
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_selection);
		radioGroup = (RadioGroup) findViewById(R.id.radioServers);
		recreateButtons();

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton checkedRadioButton = (RadioButton) radioGroup
						.findViewById(checkedId);
				Server server = (Server) checkedRadioButton.getTag();
				ArrayList<Server> servers = Config.getInstance().getServers();
				for (int i = 0; i < servers.size(); i++) {
					servers.get(i).setActive(false);
				}
				server.setActive(true);
			}
		});
		((Button) findViewById(R.id.btnaddserver))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						addEditServer(null, ServerSelectionActivity.this);

					}
				});

		((Button) findViewById(R.id.btnselectserver))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						RadioButton checkedRadioButton = (RadioButton) radioGroup
								.findViewById(radioGroup
										.getCheckedRadioButtonId());
						Server server = (Server) checkedRadioButton.getTag();
						ArrayList<Server> servers = Config.getInstance()
								.getServers();
						for (int i = 0; i < servers.size(); i++) {
							servers.get(i).setActive(false);
						}
						server.setActive(true);
						try {
							Config.save();
							finish();
							LoginActivity.instance
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											LoginActivity.instance
													.setActiveServer();

										}
									});
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});

	}

	private void recreateButtons() {
		radioGroup.removeAllViews();
		ArrayList<Server> servers = Config.getInstance().getServers();

		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
				RadioGroup.LayoutParams.WRAP_CONTENT,
				RadioGroup.LayoutParams.WRAP_CONTENT);
		OnLongClickListener l = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				try {
					Server s = (Server) v.getTag();
					addEditServer(s, ServerSelectionActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
				}
				return false;
			}
		};
		int active_id = -1;
		for (int i = 0; i < servers.size(); i++) {
			Server server = servers.get(i);
			RadioButton radioButton = new RadioButton(this);
			radioButton.setText(server.getServername());
			radioButton.setTag(server);
			radioButton.setLongClickable(true);
			radioButton.setOnLongClickListener(l);
			radioButton.setId(i);
			radioGroup.addView(radioButton, layoutParams);
			if (server.isActive())
				active_id = i;
		}
		if (active_id >= 0)
			radioGroup.check(active_id);
	}

	public static void addEditServer(Server _server, final Activity act) {
		final boolean isNew = _server == null;
		if (isNew)
			_server = new Server();
		final AlertDialog.Builder screenDialog = new AlertDialog.Builder(act);
		final Server server = _server;
		LayoutInflater inflater = LayoutInflater.from(act);
		final View convertView = inflater.inflate(R.layout.new_server, null);
		setText(convertView, R.id.eserver_name, server.getServername());
		setText(convertView, R.id.eserver_url, server.getServerurl());

		if (!isNew) {
			View isActive = ((CheckBox) convertView
					.findViewById(R.id.cbserver_active));
			isActive.setVisibility(View.GONE);
			isActive.setVisibility(View.INVISIBLE);
		}

		screenDialog.setView(convertView);
		screenDialog.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		screenDialog.setPositiveButton(R.string.save, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					String server_name = ((EditText) convertView
							.findViewById(R.id.eserver_name)).getText()
							.toString();
					String server_url = ((EditText) convertView
							.findViewById(R.id.eserver_url)).getText()
							.toString();
					if (server_name == null || server_name.trim().isEmpty())
						throw new Exception("Server name must not be empty");
					if (server_url == null || server_url.trim().isEmpty())
						throw new Exception("Server url must not be empty");
					DocFlow.init_connection(server_url.trim(), false);
					if (isNew) {
						CheckBox isActive = ((CheckBox) convertView
								.findViewById(R.id.cbserver_active));
						Server s = Config.getInstance().addServer(
								server_name.trim(), server_url.trim(),
								isActive.isChecked());
						System.out.println(s);
					} else {
						server.setServername(server_name);
						server.setServerurl(server_url);
					}
					Config.save();
					if (act instanceof ServerSelectionActivity) {
						act.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								((ServerSelectionActivity) act)
										.recreateButtons();
							}
						});
					}
					if (act instanceof LoginActivity) {
						act.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								((LoginActivity) act).setActiveServer();

							}
						});
					}
				} catch (Throwable e) {
					ActivityHelper.showAlert(act, e);

				}

			}
		});
		screenDialog.show();
	}

	private static void setText(View convertView, int id, String text) {
		EditText textView = (EditText) convertView.findViewById(id);
		if (textView != null)
			textView.setText(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
