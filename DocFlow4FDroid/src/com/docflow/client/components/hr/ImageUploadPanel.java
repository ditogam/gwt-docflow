package com.docflow.client.components.hr;

import com.docflow.client.DocFlow;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FileItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ImageUploadPanel extends VLayout {
	public static native JavaScriptObject setupFileItemClick(FileItem fi) /*-{
																			try {
																			var self = fi;
																			self.onclick();
																			} catch (err) {
																			alert(err);
																			}

																			}-*/;

	DataSource dsImageUpload;
	private int imageID = -1;

	private Img img;

	public ImageUploadPanel() {
		this.setHeight(100);
		dsImageUpload = DocFlow.getDataSource("ImageDS");
		HLayout hl1 = new HLayout();
		img = new Img("", 200, 200);
		img.setShowEdges(true);
		img.setEdgeSize(5);
		hl1.addMember(img);
		final FileItem imageItem = new FileItem();
		imageItem.setAttribute("id", "kkkk");
		img.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				try {
					setupFileItemClick(imageItem);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
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
						String src = "FileDownload.jsp?id=" + id;
						img.setSrc(src);
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

	public void setImageID(int imageID) {
		this.imageID = imageID;
		String src = "FileDownload.jsp?id=" + imageID;
		img.setSrc(src);
	}

}
