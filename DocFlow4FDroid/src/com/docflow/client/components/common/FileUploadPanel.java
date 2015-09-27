package com.docflow.client.components.common;

import com.docflow.client.DocFlow;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FileItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class FileUploadPanel extends VLayout {

	DataSource dsImageUpload;
	private int imageID = -1;

	public FileUploadPanel() {
		this.setHeight(100);
		dsImageUpload = DocFlow.getDataSource("ImageDS");
		HLayout hl1 = new HLayout();
		final FileItem imageItem = new FileItem();
		imageItem.setAttribute("id", "kkkk");
		final DynamicForm dnImage = new DynamicForm();
		hl1.addMember(new IButton("Upload", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dnImage.saveData(new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData,
							DSRequest request) {
						Record records[] = response.getData();
						if (records == null || records.length <= 0) {
							SC.say("áƒ¨áƒ”áƒªáƒ“áƒ�áƒ›áƒ� áƒ¡áƒ£áƒ áƒ�áƒ—áƒ˜áƒ¡ áƒ�áƒ¢áƒ•áƒ˜áƒ áƒ—áƒ•áƒ˜áƒ¡áƒ�áƒ¡!");
							return;
						}
						Record record = records[0];
						String id = record.getAttribute("id");
						try {
							imageID = Integer.parseInt(id);
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});

			}
		}));

		this.addMember(hl1);

		dnImage.setDataSource(dsImageUpload);

		imageItem.setTitle("სურათი");
		imageItem.setName("imageInputStream");
		dnImage.setFields(imageItem);
		imageItem.setTitleOrientation(TitleOrientation.TOP);
		VLayout hl = new VLayout();
		// imageItem.setVisible(false);

		imageItem
				.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

					@Override
					public void onClick(
							com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						SC.say("ss");

					}
				});
		hl.addMember(dnImage);

		this.addMember(hl);
	}

	public int getImageID() {
		return imageID;
	}
}
