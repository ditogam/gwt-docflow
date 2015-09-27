package com.docflowdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.docflowdroid.common.VDocumentFiles;
import com.docflowdroid.helper.camera.CameraHelper;

public class DocumentFilesActivity extends Activity {
	private VDocumentFiles view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DocFlow.setActivityLandscape(this);

		// setContentView(R.layout.activity_document_files);
		Bundle bundle = this.getIntent().getExtras();
		final int doc_id = bundle.getInt(DocDetailActivity.DOC_ID, -1);
		if (doc_id < 0) {
			finish();
		}

		try {
			// view = new VDocumentFiles(
			// DocumentFilesActivity.this, doc_id,);
		} catch (Exception e) {
			finish();
		}

		// View gridView = view.getMy_view();
		// gridView.setLayoutParams(new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.MATCH_PARENT));
		// setContentView(gridView);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (CameraHelper.executeCameraResult(this, requestCode, resultCode,
				data, view))
			return;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return false;
	}

}
