package com.docflowdroid;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.common.shared.ClSelectionItem;
import com.docflow.and.impl.db.DBConnectionAnd;
import com.docflow.client.DocFlowService;
import com.docflow.shared.ClSelection;
import com.docflowdroid.common.MapObjectProcessorContainer;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.comp.adapter.UnsortedIDValueAdapter;
import com.docflowdroid.conf.Config;
import com.docflowdroid.conf.Server;
import com.docflowdroid.helper.Utils;
import com.docflowdroid.map.MapObjectProcessor;

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

		private Throwable error;

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				Config config = Config.getInstance();
				MapObjectProcessorContainer container = new MapObjectProcessorContainer(
						new MapObjectProcessor(), 0, 0);

				DocFlow.init(
						config.getActiveServer().getServerurl().toString(),
						LoginActivity.this,
						(int) ((Spinner) findViewById(R.id.spLanguage))
								.getSelectedItemId(),
						(int) ((Spinner) findViewById(R.id.spSystem))
								.getSelectedItemId(), container);
				try {
					DocFlow.createDocTypeTree();
				} catch (Exception e) {
					// TODO: handle exception
				}
				return true;
			} catch (Throwable e) {
				error = e;
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
				startActivity(new Intent(LoginActivity.this, MainActivity.class));

				finish();
			} else if (error != null) {
				ActivityHelper.showAlert(LoginActivity.this, error);
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

	// UI references.
	private CheckBox mWorkingOffline;
	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;

	private TextView mLoginStatusMessageView;

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
		DocFlow.mWorkingOffline = mWorkingOffline.isChecked();
		DocFlow.mUserName = mUserNameView.getText().toString();
		DocFlow.mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(DocFlow.mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(DocFlow.mUserName)) {
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

	static LoginActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DocFlow.setActivity(this);
		DBConnectionAnd.customFuntions = null;
		Utils.initScreenResolution(this);

		DocFlow.setActivityLandscape(this);
		instance = this;
		setContentView(R.layout.activity_login);
		try {
			Config.readConfig(this, R.string.docflow_dir,
					R.string.docflow_settings);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				ActivityHelper.showAlert(getApplicationContext(), ex);
			}
		});
		((Button) findViewById(R.id.btnselectserver))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(LoginActivity.this,
								ServerSelectionActivity.class));

					}
				});
		;
		mWorkingOffline = (CheckBox) findViewById(R.id.chworking_offline);
		mUserNameView = (EditText) findViewById(R.id.user_name);
		mUserNameView.setText(Config.getInstance().getUsername());

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(Config.getInstance().getPwd());
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
		WebView wvNews = (WebView) findViewById(R.id.wvNews);
		String news = "ახალ ვერსიაში თუ დიდხანს დააწვებით ჩამოსაშლელ ველს, გამოვა დიალოგი, რომლითაც შეგიძლიათ გაფილტროთ ჩამონათვალი!!!";
		// news = "";
		if (news.trim().isEmpty()) {
			findViewById(R.id.tvNew).setVisibility(View.GONE);
			wvNews.setVisibility(View.GONE);
		} else
			wvNews.loadData(
					"<p  style=\"color: red;font-weight: bold;font-size:small\">"
							+ news + "</p>", "text/html; charset=utf-8",
					"UTF-8");
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		mWorkingOffline
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {

							ProcessExecutor.execute(new IProcess() {

								@Override
								public void execute() throws Exception {
									try {
										final DocFlowService service = DocFlow
												.init_connection(null, true);
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												try {
													setParamData(service);
												} catch (final Throwable e) {
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															ActivityHelper
																	.showAlert(
																			LoginActivity.this,
																			e);
														}
													});
												}

											}
										});
									} catch (final Throwable e) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ActivityHelper.showAlert(
														LoginActivity.this, e);
											}
										});
									}
								}
							}, LoginActivity.this);

						} else {
							setActiveServer();
						}
					}
				});
		setActiveServer();
	}

	private void setAdapterAndPosition(
			HashMap<Integer, ArrayList<ClSelectionItem>> types, int type,
			int id, int spinner_id) {
		ArrayList<ClSelectionItem> list = types.get(type);
		UnsortedIDValueAdapter adapter = new UnsortedIDValueAdapter(list, this);
		Spinner sp = ((Spinner) findViewById(spinner_id));
		sp.setAdapter(adapter);
		int pos = -1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == (long) id) {
				pos = i;
				break;
			}
		}
		try {
			if (pos != -1)
				sp.setSelection(pos);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void setActiveServer() {

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				try {
					Server serv = Config.getInstance().getActiveServer(true);
					if (serv == null) {
						serv = new Server();
						serv.setActive(true);
						startServerChoose();
					} else {
						final DocFlowService service = DocFlow.init_connection(
								serv.getServerurl(), false);

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								try {
									((TextView) findViewById(R.id.etserver))
											.setText(Config.getInstance()
													.getActiveServer()
													.getServername());
									StrictMode
											.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
													.permitAll().build());
									setParamData(service);
								} catch (Throwable e) {
									startServerChoose();
								}

							}
						});

					}
				} catch (Throwable e) {
					startServerChoose();
				}
			}
		}, this);

	}

	private void setParamData(DocFlowService service) throws Exception {

		HashMap<Integer, ArrayList<ClSelectionItem>> types = service
				.getTopTypes(new int[] { ClSelection.T_LANGUAGE,
						ClSelection.T_SYSTEMS });

		setAdapterAndPosition(types, ClSelection.T_LANGUAGE, Config
				.getInstance().getLanguageid(), R.id.spLanguage);

		setAdapterAndPosition(types, ClSelection.T_SYSTEMS, Config
				.getInstance().getSystemid(), R.id.spSystem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
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

	private void startServerChoose() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(LoginActivity.this,
						ServerSelectionActivity.class));

			}
		});
	}
}
