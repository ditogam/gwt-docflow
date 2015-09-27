package com.docflowdroid.common;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.common.DocTypeMapping;
import com.docflowdroid.ActivityHelper;
import com.docflowdroid.DocDetailActivity;
import com.docflowdroid.DocFlow;
import com.docflowdroid.MainActivity;
import com.docflowdroid.R;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.comp.adapter.UnsortedIDValueAdapter;

public class VNewDocument implements OnItemSelectedListener {
	private View view;
	private Activity context;
	private Spinner spDocType;
	private EditText neCustomer;
	private EditText et_cancelary;
	private LinearLayout llCustomer;
	private ImageButton ibNew;
	private AlertDialog alertDialog;

	public VNewDocument(Activity context, int doc_type_id) {
		view = context.getLayoutInflater().inflate(R.layout.new_document_panel,
				null);
		this.context = context;
		spDocType = (Spinner) view.findViewById(R.id.sp_doc_type);
		neCustomer = (EditText) view.findViewById(R.id.ne_cus_id);
		et_cancelary = (EditText) view.findViewById(R.id.et_cancelary);
		ibNew = (ImageButton) view.findViewById(R.id.ib_new_document);
		llCustomer = (LinearLayout) view.findViewById(R.id.l_customer);
		ArrayList<ClSelectionItem> doc_types = DocFlow.doc_types;
		int indx = -1;
		for (int i = 0; i < doc_types.size(); i++) {
			if (doc_types.get(i).getId() == (long) doc_type_id) {
				indx = i;
				break;
			}
		}
		spDocType.setAdapter(new UnsortedIDValueAdapter(DocFlow.doc_types,
				context));
		spDocType.setSelection(indx);
		llCustomer.setVisibility(View.GONE);
		spDocType.setOnItemSelectedListener(this);
		ibNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createNewDocument();

			}

		});

		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(spDocType.getContext().getString(
				R.string.new_document));
		alertDialog.setView(view);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		alertDialog.show();

	}

	private void createNewDocument() {
		final long docType = spDocType.getSelectedItemId();
		if (docType <= 0) {
			ActivityHelper.showAlert(spDocType.getContext(), spDocType
					.getContext().getString(R.string.please_select_doc_type));
		}

		Object obj = et_cancelary.getText();
		if (obj == null || obj.toString().trim().isEmpty()) {
			ActivityHelper.showAlert(spDocType.getContext(), spDocType
					.getContext().getString(R.string.please_select_cancelary));
			et_cancelary.requestFocus();
			return;
		}

		final String cancelary_nom = obj.toString().trim();
		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				try {
					int cus_id = llCustomer.getVisibility() == View.VISIBLE ? Integer
							.parseInt(neCustomer.getText().toString()) : 0;

					DocTypeMapping dtm = DocFlow.docFlowService.getValueMap(
							cus_id, (int) docType, DocFlow.language_id,
							DocFlow.user_obj.getUser().getUser_id());
					dtm.getDocument().setCancelary_nom(cancelary_nom);
					MainActivity.instance.docTypeMapping = dtm;

					MainActivity.instance.cancelary_no = cancelary_nom;

					Intent myIntent = new Intent(context,
							DocDetailActivity.class);
					myIntent.putExtra(DocDetailActivity.DOC_TYPE_ID,
							(long) docType);
					context.startActivity(myIntent);
					alertDialog.dismiss();

				} catch (final Throwable e) {
					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ActivityHelper.showAlert(spDocType.getContext(), e);
						}
					});
				}

			}
		}, context);

	}

	@Override
	public void onItemSelected(final AdapterView<?> parent, final View view,
			int position, long id) {
		final long docType = spDocType.getSelectedItemId();
		if (docType <= 0) {
			llCustomer.setVisibility(View.GONE);
			return;
		}
		ClSelectionItem o = (ClSelectionItem) spDocType.getSelectedItem();
		llCustomer.setVisibility(o.getAdditional_value() > 0 ? View.VISIBLE
				: View.GONE);
		// ProcessExecutor.execute(new IProcess() {
		//
		// @Override
		// public void execute() throws Exception {
		// try {
		// final DocType dt = DocFlow.docFlowService.getDocType(
		// (int) docType, DocFlow.language_id);
		// context.runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// llCustomer.setVisibility(dt.isApplied_customer() ? View.VISIBLE
		// : View.GONE);
		// }
		// });
		//
		// } catch (final Throwable e) {
		// context.runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// ActivityHelper.showAlert(spDocType.getContext(), e);
		// }
		// });
		// }
		//
		// }
		// }, context);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
}
