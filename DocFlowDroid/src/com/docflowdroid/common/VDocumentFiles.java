package com.docflowdroid.common;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.common.DocumentFile;
import com.docflowdroid.ActivityHelper;
import com.docflowdroid.R;
import com.docflowdroid.comp.FormDefinitionPanel;
import com.docflowdroid.comp.adapter.ISelectButtonExecute;
import com.docflowdroid.comp.adapter.SelectArrayAdapter;
import com.docflowdroid.helper.ICameraResult;
import com.docflowdroid.helper.camera.CameraHelper;

public class VDocumentFiles implements ISelectButtonExecute, ICameraResult {

	/**
	 * 
	 */
	private ImageButton ibtnNewFile;
	private ImageButton ibtnDeleteFiles;
	private ListView lvFileList;
	private Activity context;
	private View my_view;
	private int doc_id;
	private SelectArrayAdapter adapter;
	private ArrayList<ClSelectionItem> deletedItems;
	private int currentId = 1;

	public VDocumentFiles(Activity context, int doc_id,
			ArrayList<DocumentFile> files, final FormDefinitionPanel panel) {
		my_view = context.getLayoutInflater().inflate(R.layout.ldocfiles, null);
		files = files == null ? new ArrayList<DocumentFile>() : files;
		this.context = context;
		this.doc_id = doc_id;
		ibtnNewFile = (ImageButton) my_view.findViewById(R.id.ibtnNewFile);
		ibtnDeleteFiles = (ImageButton) my_view
				.findViewById(R.id.ibtnDeleteFiles);
		lvFileList = (ListView) my_view.findViewById(R.id.lvFileList);
		deletedItems = new ArrayList<ClSelectionItem>();
		final ArrayList<ClSelectionItem> items = new ArrayList<ClSelectionItem>();
		for (DocumentFile documentFile : files) {
			ClSelectionItem item = new ClSelectionItem();
			item.setAdditionalObject(documentFile);
			item.setId(documentFile.getId());
			item.setValue(documentFile.getFilename());
			items.add(item);
		}

		adapter = new SelectArrayAdapter(context, items, this);
		lvFileList.setAdapter(adapter);
		ibtnNewFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panel.setCurrentCameraResult(VDocumentFiles.this);
				CameraHelper.startCameraCapture(VDocumentFiles.this.context);
			}
		});

		ibtnDeleteFiles.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteFiles();
			}
		});

	}

	public ArrayList<DocumentFile> saveData() {
		final ArrayList<DocumentFile> files = new ArrayList<DocumentFile>();
		ArrayList<ClSelectionItem> items = adapter.getItems();
		for (ClSelectionItem item : items) {
			DocumentFile df = (DocumentFile) item.getAdditionalObject();
			if (df.getId() == 0)
				files.add(df);
			df.setDocument_id(doc_id);
		}

		for (ClSelectionItem item : deletedItems) {
			DocumentFile df = (DocumentFile) item.getAdditionalObject();
			df.setId(-1 * Math.abs(df.getId()));
			df.setDocument_id(doc_id);
			files.add(df);
		}
		return files;

	}

	protected void deleteFiles() {
		ArrayList<ClSelectionItem> items = adapter.getItems();
		ArrayList<ClSelectionItem> deletedItems = new ArrayList<ClSelectionItem>();
		for (ClSelectionItem item : items) {
			if (item.getParentId() != 0)
				deletedItems.add(item);
		}
		if (deletedItems.isEmpty())
			return;
		ask_remove_selected_files(false, items, deletedItems);
	}

	public View getMy_view() {
		return my_view;
	}

	private void ask_remove_selected_files(final boolean empty,
			final ArrayList<ClSelectionItem> items,
			final ArrayList<ClSelectionItem> deletedItems) {

		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					remove_selected_files(empty, items, deletedItems);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					for (ClSelectionItem item : deletedItems) {
						item.setParentId(0);
					}
					changeData();

					break;
				}
			}
		};
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				builder.setMessage(
						context.getString(empty ? R.string.do_you_want_to_empty
								: R.string.do_you_want_to_delete))
						.setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();

			}
		});

	}

	protected void remove_selected_files(final boolean empty,
			final ArrayList<ClSelectionItem> items,
			final ArrayList<ClSelectionItem> deletedItems) {
		if (!empty && deletedItems.size() == items.size()) {
			ask_remove_selected_files(true, items, deletedItems);
			return;
		}
		for (ClSelectionItem item : deletedItems) {
			items.remove(item);
			DocumentFile df = (DocumentFile) item.getAdditionalObject();
			if (df.getId() > 0)
				this.deletedItems.add(item);
		}
		changeData();

	}

	public void changeData() {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();

			}
		});
	}

	@Override
	public String getButtonTitle() {
		return "Open";
	}

	@Override
	public void executeItem(final Object _item, View view) {

		executeOpen(_item);

	}

	private void executeOpen(final Object _item) {
		try {
			ClSelectionItem item = (ClSelectionItem) _item;
			final DocumentFile df = (DocumentFile) item.getAdditionalObject();
			CameraHelper.showFile(df.getImage_id(), df.getFilename(), context);
		} catch (Throwable e) {
			ActivityHelper.showAlert(context, e);
		}
	}

	@Override
	public void setResult(Map<Long, String> files) {
		ArrayList<ClSelectionItem> items = adapter.getItems();
		Set<Long> keys = files.keySet();
		for (Long key : keys) {
			String file_name = files.get(key);
			DocumentFile _file = new DocumentFile();
			_file.setImage_id(key.intValue());
			_file.setFilename(file_name);

			ClSelectionItem item = new ClSelectionItem();
			item.setAdditionalObject(_file);
			item.setId(-1 * (currentId++));
			item.setValue(_file.getFilename());
			if (items.isEmpty())
				items.add(item);
			else
				items.add(0, item);
		}
		changeData();
	}

}
