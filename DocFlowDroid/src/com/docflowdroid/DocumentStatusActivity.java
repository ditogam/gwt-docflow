package com.docflowdroid;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.common.shared.ClSelectionItem;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.comp.adapter.Statis_List_Adapter;

public class DocumentStatusActivity extends Activity {
	private Spinner spStatuses;
	private EditText etaComment;
	private int doc_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_status);
		DocFlow.setActivityLandscape(this);

		ArrayList<ClSelectionItem> newstatuses = new ArrayList<ClSelectionItem>();
		try {
			ArrayList<ClSelectionItem> statuses = DocFlow.user_obj
					.getStatusTree().get(DocFlow.system);

			for (ClSelectionItem item : statuses) {
				newstatuses.add(item);
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}
		spStatuses = (Spinner) findViewById(R.id.spDocStateChange);
		etaComment = (EditText) findViewById(R.id.etaComment);
		spStatuses.setAdapter(new Statis_List_Adapter(this, newstatuses));

		Bundle bundle = this.getIntent().getExtras();
		doc_id = bundle.getInt(DocDetailActivity.DOC_ID, -1);
		if (doc_id < 0) {
			finish();
		}

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				try {
					final ClSelectionItem item = DocFlow.docFlowService
							.getDocumentStateValue((long) doc_id);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							etaComment.setText(item.getValue());
							spStatuses.setSelection(getIndex(spStatuses,
									item.getId()));

						}
					});
				} catch (Throwable e) {
					finish();
				}

			}
		}, DocumentStatusActivity.this);

		((ImageButton) findViewById(R.id.btnSaveStatus))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						ProcessExecutor.execute(new IProcess() {

							@Override
							public void execute() throws Exception {

								try {
									long status = ((ClSelectionItem) spStatuses
											.getSelectedItem()).getId();
									DocFlow.docFlowService.documentChangeState(
											doc_id, etaComment.getText()
													.toString(), (int) status,
											DocFlow.user_obj.getUser()
													.getUser_id(),
											DocFlow.language_id, new Date()
													.getTime(), false);

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											finish();

										}
									});

								} catch (final Throwable e) {

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											ActivityHelper
													.showAlert(
															DocumentStatusActivity.this,
															e);

										}
									});

								}

							}
						}, DocumentStatusActivity.this);

					}
				});
	}

	private int getIndex(Spinner spinner, long id) {
		int index = 0;

		for (int i = 0; i < spinner.getCount(); i++) {
			if (((ClSelectionItem) spinner.getItemAtPosition(i)).getId() == id) {
				index = i;
				i = spinner.getCount();// will stop the loop, kind of break, by
										// making condition false
			}
		}
		return index;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
