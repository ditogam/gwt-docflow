package com.docflow.client.components.docflow;

import java.util.Date;

import com.docflow.client.components.common.SplashDialog;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class GPopUpUpload {
	private FormPanel form;
	private VerticalPanel panel;
	private FileUpload uploader;
	private DynamicForm editorForm;
	private Window window;
	private WDocFiles docFiles;

	public void setDocFiles(WDocFiles docFiles) {
		this.docFiles = docFiles;
	}

	public void initGUI() {
		window = new Window();
		window.setSize("475px", "135px");
		window.setTitle("Import");
		window.centerInPage();
		window.addItem(getEditForm());
		window.show();
	}

	public VLayout getEditForm() {
		VLayout layout = new VLayout();

		{
			DynamicForm spacerForm = new DynamicForm();
			spacerForm.setHeight("35%");
			layout.addMember(spacerForm);

			editorForm = new DynamicForm();
			editorForm.setWidth("100%");
			editorForm.setHeight("10px");
			editorForm.setNumCols(2);
			editorForm.setColWidths(5, "*");
			editorForm.setMargin(3);

			StaticTextItem txtText = new StaticTextItem("Text", "");
			txtText.setValue("Please choose your import file: ");
			editorForm.setItems(txtText);

			layout.addMember(spacerForm);
			layout.addMember(editorForm);
			layout.addMember(getUploadForm());
		}
		{
			ToolStrip toolbar = new ToolStrip();
			toolbar.setWidth100();
			toolbar.setMembersMargin(5);
			toolbar.setReverseOrder(true);
			{

				ToolStripButton buttonOK = new ToolStripButton("Apply");
				buttonOK.setIcon("icons/32x32/apply.png");
				buttonOK.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						uploadFile();
						closeWindow();
					}
				});

				toolbar.addButton(buttonOK);

				ToolStripButton buttonCancel = new ToolStripButton("Cancel");
				buttonCancel.setIcon("icons/32x32/cancel.png");
				buttonCancel.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						closeWindow();
					}
				});
				toolbar.addButton(buttonCancel);

				layout.addMember(toolbar);
			}
		}
		return layout;
	} // End of method

	public FormPanel getUploadForm() {
		// Create a FormPanel and point it at a service.
		form = new FormPanel();
		form.setHeight("20px");
		form.setAction("FileUpload.jsp");

		SpacerItem spacer = new SpacerItem();
		spacer.setWidth("10px");

		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		// Create a panel to hold all of the form widgets.
		panel = new VerticalPanel();
		form.setWidget(panel);

		// Create a FileUpload widget.
		uploader = new FileUpload();

		// determines uploader width... make Firefox 1.5.0.7 or higher happy
		Element ee = uploader.getElement();
		DOM.setAttribute(ee, "size", "50");

		uploader.setName("Uploader");
		panel.add(uploader);
		panel.setSpacing(12);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is
				// fired. Assuming the service returned a response of type
				// text/html,
				// we can get the result text here (see the FormPanel
				// documentation for
				// further explanation).

				try {
					SplashDialog.hideSplash();
					form.reset();
					String json = event.getResults();
					json = json.substring(json.indexOf('['));
					json = json.substring(0, json.indexOf(']') + 1);

					JSONValue jsonValue = JSONParser.parseLenient(json);

					if (jsonValue instanceof JSONArray) {
						JSONArray arr = (JSONArray) jsonValue;

						for (int i = 0; i < arr.size(); i++) {
							JSONValue jsonItem = arr.get(i);
							JSONObject jsonObj = jsonItem.isObject();
							try {
								docFiles.addFile(Integer.parseInt(jsonObj.get(
										"id").toString()), jsonObj.get("file")
										.isString().stringValue());
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
					}

				} catch (Exception e) {
					SplashDialog.hideSplash();
					SC.say(e.toString());
				}

			}
		});

		return form;
	}

	public void uploadFile() {
		if (uploader.getFilename().equals("") == false) {
			StringBuilder url = new StringBuilder();
			url.append("FileUpload.jsp");
			form.setAction(url.toString());
			form.submit();
			closeWindow();
		} else {
			SC.say("Please choose a file before clicking the button 'Apply'");
			SplashDialog.hideSplash();
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Creates and returns a file name
	 */
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private String getFileName() {
		String fileName = null;

		Date date = new Date();
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy_MM_dd_H_m_s_");
		String formatedDate = fmt.format(date);

		fileName = formatedDate + getMimeType(uploader.getFilename());

		return fileName;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Returns the mimetype of a file
	 */
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private String getMimeType(String filePath) {

		int i = filePath.lastIndexOf(".");
		String fileType = null;

		if (i > -1 && i < filePath.length() - 1) {
			fileType = filePath.substring(i);
		}

		return fileType;

	} // Ende der Methode

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Closes the opened window
	 */
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void closeWindow() {
		if (window != null)
			window.destroy();
	} // End of class
}
