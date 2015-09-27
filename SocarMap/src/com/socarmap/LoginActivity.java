package com.socarmap;

import java.io.File;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.socarmap.db.DBLoader;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.AssetHelper;
import com.socarmap.helper.ConnectionHelper;
import com.socarmap.helper.ConnectivityReceiver;
import com.socarmap.proxy.beans.UserData;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				Integer user_id = null;
				try {
					ConnectionHelper.getConnection();
				} catch (Throwable e) {
					return false;
				}
				initDB();
				if (ConnectionHelper.userContext != null)
					user_id = ConnectionHelper.userContext.getUser_id();
				else
					user_id = DBLoader.getInstance().checkUsernameAndPassword(
							ConnectionHelper.mUserName,
							ConnectionHelper.mPassword);
				if (ConnectionHelper.userContext == null
						|| ConnectionHelper.userContext.getUserData() == null)
					userData = DBLoader.getInstance().getUser_data(
							ConnectionHelper.mUserName, user_id);
				else if (ConnectionHelper.userContext != null
						&& ConnectionHelper.userContext.getUserData() != null)
					userData = ConnectionHelper.userContext.getUserData();
				if (ConnectionHelper.userContext != null && userData != null) {
					userData.setPcity(ConnectionHelper.userContext
							.getSubregion_id());
					userData.setPpcity(ConnectionHelper.userContext
							.getRegion_id());
					ConnectionHelper.settings
							.setUser(ConnectionHelper.mUserName);
					ConnectionHelper.settings.saveData();
				}
				return userData != null;
			} catch (Throwable e) {
				return false;
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				if (settingsData != null)
					userData = UserData.compare(userData, settingsData);

				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				if (receiver != null)
					unregisterReceiver(receiver);
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}
	}
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	public static UserData userData = null;
	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;

	private TextView mLoginStatusMessageView;
	private UserData settingsData;

	public static ConnectivityReceiver receiver = null;

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		ConnectionHelper.mUserName = mUserNameView.getText().toString();
		ConnectionHelper.mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(ConnectionHelper.mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(ConnectionHelper.mUserName)) {
			mUserNameView.setError(getString(R.string.error_field_required));
			focusView = mUserNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	private void copyDB(String dbpath) {
		File dbFPath = new File(dbpath);
		// dbFPath.delete();
		if (!dbFPath.exists()) {
			dbFPath.getParentFile().mkdirs();
			try {
				AssetHelper.CopyAsset(this, dbFPath.getParentFile(),
						dbFPath.getName());
			} catch (Throwable e) {
				ActivityHelper.showAlert(this, e);
			}
		}
	}

	public void initDB() {
		String dbPath = getString(R.string.socar_db);
		String dbSettingsPath = getString(R.string.settings_db);
		copyDB(dbSettingsPath);
		try {
			DBLoader.initInstance(dbPath);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DBSettingsLoader.initInstance(dbSettingsPath);
			settingsData = DBSettingsLoader.getInstance().loadUserData();
			mUserNameView.setText(settingsData.getUsername());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				ActivityHelper.showAlert(getApplicationContext(), ex);
			}
		});
		receiver = ConnectionHelper.init(this);

		setContentView(R.layout.activity_login);
		Drawable dr = getResources().getDrawable(R.drawable.offline);
		String text = getString(R.string.working_offline);
		try {
			if (ConnectionHelper.getConnection() != null) {
				dr = getResources().getDrawable(
						ConnectionHelper.getConnectionType());
				text = getString(R.string.working_online);
			}
		} catch (Throwable e) {
		}
		((TextView) findViewById(R.id.tv_online_offline)).setText(text);
		((ImageView) findViewById(R.id.img_online_offline))
				.setImageDrawable(dr);
		mUserNameView = (EditText) findViewById(R.id.user_name);
		mUserNameView.setText(ConnectionHelper.settings.getUser());

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(ConnectionHelper.settings.getPwd());
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	protected void onStop() {
		try {
			if (LoginActivity.receiver != null)
				unregisterReceiver(LoginActivity.receiver);
		} catch (Throwable e) {
		}
		super.onStop();
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
