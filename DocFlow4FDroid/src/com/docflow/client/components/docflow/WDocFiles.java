package com.docflow.client.components.docflow;

import java.util.ArrayList;

import com.docflow.client.DocFlow;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.common.DocumentFile;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

public class WDocFiles extends Window {

	public static void showWindow(ArrayList<DocumentFile> docFiles,
			boolean readonly) {
		new WDocFiles(docFiles, readonly).show();
	}

	private UploadItem fiFile;
	private ButtonItem bAdd;
	private ButtonItem bRemove;
	private ButtonItem bShow;
	private ListGrid lgFiles;
	private DataSource dsImageUpload;
	private GPopUpUpload upUpload;

	private DynamicForm dfAdd;
	public static final String TARGET = "FileUpload.jsp";

	private ArrayList<DocumentFile> docFiles;

	public WDocFiles(ArrayList<DocumentFile> docFiles, boolean readonly) {
		upUpload = new GPopUpUpload();
		ListGridField lgSerial = new ListGridField("serial");
		lgSerial.setHidden(true);
		ListGridField lgimageId = new ListGridField("lgimageId");
		lgimageId.setHidden(true);
		lgimageId.setType(ListGridFieldType.INTEGER);
		ListGridField lgFileName = new ListGridField("fileName", "File Name",
				200);

		this.docFiles = docFiles;
		dfAdd = new DynamicForm();
		dfAdd.setNumCols(7);
		dsImageUpload = DocFlow.getDataSource("ImageDBDS");
		fiFile = new UploadItem();

		fiFile.setTitle("File");
		fiFile.setName("filedata111");

		bAdd = new ButtonItem("bAdd", "Add");
		bRemove = new ButtonItem("bRemove", "Remove");
		bShow = new ButtonItem("bShow", "Show");
		bAdd.setEndRow(false);
		bAdd.setStartRow(false);
		bRemove.setEndRow(false);
		bRemove.setStartRow(false);
		bShow.setEndRow(false);
		bShow.setStartRow(false);
		dfAdd.setDataSource(dsImageUpload);
		dfAdd.setTitleOrientation(TitleOrientation.TOP);
		VLayout hl = new VLayout();
		dfAdd.setHeight("15%");
		fiFile.setVisible(!readonly);
		fiFile.setVisible(false);
		bAdd.setVisible(!readonly);
		bRemove.setVisible(!readonly);

		dfAdd.setAction(TARGET);
		dfAdd.setEncoding(Encoding.MULTIPART);
		dfAdd.setMethod(FormMethod.POST);
		// dfAdd.setTarget("hidden_frame");
		dfAdd.setFields(fiFile, bAdd, bRemove, bShow);
		dfAdd.setVisible(!readonly);
		upUpload.setDocFiles(this);
		hl.addMember(upUpload.getUploadForm());
		hl.addMember(dfAdd);
		lgFiles = new ListGrid();
		lgFiles.setFields(lgSerial, lgFileName);
		lgFiles.setHeight("70%");
		hl.addMember(lgFiles);
		lgFiles.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				showDocument();

			}
		});
		this.addItem(hl);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.setHeight(400);
		this.setWidth(600);
		this.centerInPage();
		bRemove.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				removeData();
			}
		});
		bShow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showDocument();
			}
		});
		bAdd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// if (fiFile.getFieldName() == null
				// || fiFile.getFieldName().trim().length() == 0) {
				// SC.say("Set File!!!", new BooleanCallback() {
				//
				// @Override
				// public void execute(Boolean value) {
				// fiFile.focusInItem();
				//
				// }
				// });
				// return;
				// }
				SplashDialog.showSplash();
				upUpload.uploadFile();
				try {

					// final FileUploader dialog = FileUploader.popup(
					// "File Uploader", "en");
					// dialog.setUrl(TARGET);
					// dialog.setPostVarName("file");
					// dialog.setWidth(602);
					// dialog.setHeight(300);
					// dialog.addUploadCompletedHandler(new
					// UploadCompletedHandler() {
					// public void onCompleted(UploadCompletedEvent event) {
					// SC.say(FileUploaderUtils.getDictionary("en").get(
					// FileUploader.MSG_UPLOADING_FINISHED));
					// }
					// });
					// dialog.addBeforeFileAddHandler(new BeforeFileAddHandler()
					// {
					// public void onAdd(BeforeFileAddEvent event) {
					// // SC.say("Adding file: " + event.getFilename());
					// // event.cancel();
					// }
					// });
					// dialog.addCloseClickHandler(new
					// com.smartgwt.client.widgets.events.CloseClickHandler() {
					//
					// @Override
					// public void onCloseClick(CloseClickEvent event) {
					// dialog.hide();
					//
					// }
					// });

					// dialog.addFileNameFilter(new ExtensionFilter(".bmp",
					// ".gif",
					// ".jpeg", ".jpg", ".pdf", ".png"));

					// DSRequest req = new DSRequest();
					// req.setOperationType(DSOperationType.ADD);
					// // dfAdd.setValue("imgname",
					// fiFile.getValue().toString());
					// dfAdd.saveData(new DSCallback() {
					// @Override
					// public void execute(DSResponse response,
					// Object rawData, DSRequest request) {
					// SplashDialog.hideSplash();
					//
					// Record records[] = response.getData();
					// if (records == null || records.length <= 0) {
					// SC.say("NULL RESULT");
					// return;
					// }
					// Record record = records[0];
					// Map mp = record.toMap();
					// String id = record.getAttribute("id");
					//
					// System.out.println(fiFile.getValue());
					// try {
					// addFile(Integer.parseInt(id),
					// record.getAttribute("imagename"));
					// } catch (Exception e) {
					// SC.say("ERROR SAVINGLOCAL="
					// + getExceptionFull(e));
					// }
					// dfAdd.editNewRecord();
					// }
					// }, req);

				} catch (Exception e) {
					SplashDialog.hideSplash();
					SC.say("ERROR SAVINGDMI=" + getExceptionFull(e));
				}
			}
		});

		for (DocumentFile documentFile : docFiles) {
			if (documentFile.getId() >= 0)
				addData(documentFile);
		}

		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
	}

	private String getExceptionFull(Exception e) {
		// StringWriter sw = new StringWriter();
		// ex.printStackTrace(new PrintWriter(sw));
		// return sw.toString();
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private void addData(DocumentFile df) {
		df.setuId(HTMLPanel.createUniqueId());
		ListGridRecord lgRecord = new ListGridRecord();
		lgRecord.setAttribute("serial", df.getuId());
		lgRecord.setAttribute("fileName", df.getFilename());
		lgRecord.setAttribute("lgimageId", df.getImage_id());
		lgFiles.addData(lgRecord);
	}

	public void addFile(int id, String filename) {
		DocumentFile df = new DocumentFile();
		df.setFilename(filename);
		df.setImage_id(id);
		docFiles.add(df);
		addData(df);
	}

	private void removeData() {
		final ListGridRecord record = lgFiles.getSelectedRecord();
		if (record == null) {
			SC.say("Please select record!!!");
			return;
		}

		final String serial = record.getAttribute("serial");
		if (serial == null) {
			SC.say("Please select record!!!");
			return;
		}

		SC.ask("Do you want to delete file?", new BooleanCallback() {

			@Override
			public void execute(Boolean value) {
				if (!value) {
					return;
				}
				DocumentFile documentFile = null;
				for (DocumentFile df : docFiles) {
					if (df.getuId().equals(serial)) {
						documentFile = df;
						break;
					}
				}
				if (documentFile == null) {
					SC.say("Please select record!!!");
					return;
				}
				if (documentFile.getId() == 0)
					docFiles.remove(documentFile);
				else
					documentFile.setId(-1 * Math.abs(documentFile.getId()));
				lgFiles.removeData(record);
			}
		});

	}

	private void showDocument() {
		ListGridRecord record = lgFiles.getSelectedRecord();
		if (record == null) {
			SC.say("Please select record!!!");
			return;
		}

		Integer lgimageId = record.getAttributeAsInt("lgimageId");
		if (lgimageId == null) {
			SC.say("Please select record!!!");
			return;
		}
		
		DocFlow.showFile(lgimageId.toString());
	}
}
