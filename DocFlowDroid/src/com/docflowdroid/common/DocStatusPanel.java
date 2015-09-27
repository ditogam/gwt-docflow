package com.docflowdroid.common;

import java.util.ArrayList;
import java.util.Date;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflowdroid.ActivityHelper;
import com.docflowdroid.DocDetailActivity;
import com.docflowdroid.DocFlow;
import com.docflowdroid.MainActivity;
import com.docflowdroid.R;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.comp.adapter.Statis_List_Adapter;

public class DocStatusPanel {
	private Spinner spStatuses;
	private EditText etaComment;
	private long doc_id;
	private boolean visible;

	public DocStatusPanel(final DocDetailActivity activity, View v,
			ArrayList<ClSelectionItem> statuses, final DocumentLong document,
			boolean disabled) {
		spStatuses = (Spinner) v.findViewById(R.id.spDocStateChange);
		etaComment = (EditText) v.findViewById(R.id.etaComment);
		spStatuses.setAdapter(new Statis_List_Adapter(activity, statuses));
		etaComment.setText(document.getReplic());

		setStatusId(document.getDoc_status_id());
		doc_id = document.getId();

		spStatuses.setEnabled(!disabled);
		((ImageButton) v.findViewById(R.id.btnSaveStatus))
				.setEnabled(!disabled);
		((ImageButton) v.findViewById(R.id.btnSaveStatus))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						saveStatus(activity);

					}
				});
		((ImageButton) v.findViewById(R.id.btnSaveStatus))
				.setVisibility(View.GONE);
	}

	public void setStatusId(int statusid) {
		spStatuses.setSelection(getIndex(spStatuses, statusid));
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

	public void setDoc_id(long doc_id) {
		this.doc_id = doc_id;
	}

	public void saveStatus(final DocDetailActivity activity) {
		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {

				try {
					int did = (int) doc_id;
					long status = ((ClSelectionItem) spStatuses
							.getSelectedItem()).getId();
					final DocumentShort dsh = DocFlow.docFlowService
							.documentChangeState(did, etaComment.getText()
									.toString(), (int) status, DocFlow.user_obj
									.getUser().getUser_id(),
									DocFlow.language_id, new Date().getTime(),
									false);
					if (dsh != null) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								visible = true;
								MainActivity.instance
										.documentSaved(dsh, doc_id);
								activity.doAfterSave(dsh.getDoc_status_id());
							}
						});
					} else {
						throw new Exception("Unknown error!!!");
					}

				} catch (final Throwable e) {

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ActivityHelper.showAlert(activity, e);

						}
					});

				}

			}
		}, activity);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
