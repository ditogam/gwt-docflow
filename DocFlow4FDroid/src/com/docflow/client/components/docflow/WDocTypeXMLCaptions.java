package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import com.docflow.client.DocFlow;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.FormGroup;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

public class WDocTypeXMLCaptions extends Window {

	private class GroupSelection extends SectionStackSection {
		private FormGroup _formGroup;

		public GroupSelection(FormGroup formGroup, int index) {
			super("Group #" + index);
			this._formGroup = formGroup;
			DynamicForm df = new DynamicForm();
			TextItem tiText = new TextItem("tiText", "GroupTitle");
			tiText.setValue(_formGroup.getGroupTitle());
			tiText.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					_formGroup.setGroupTitle(event.getValue().toString());
				}
			});
			LinkedHashMap<String, String> _map = new LinkedHashMap<String, String>();
			_map.put(FormGroup.FTORIENTATION_TOP + "", "FTORIENTATION_TOP");
			_map.put(FormGroup.FTORIENTATION_LEFT + "", "FTORIENTATION_LEFT");
			_map.put(FormGroup.FTORIENTATION_RIGHT + "", "FTORIENTATION_RIGHT");
			SelectItem siOrientation = new SelectItem("siOrientation",
					"Title Orientation");
			siOrientation.setValueMap(_map);
			siOrientation.setValue(_formGroup.getLabelOrientation());
			siOrientation.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					_formGroup.setLabelOrientation(Integer.parseInt(event
							.getValue().toString()));

				}
			});
			SelectItem siCaptions = createCaptionCombo(
					_formGroup.getFieldCaptionId(), "Caption");
			siCaptions.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					_formGroup.setFieldCaptionId(Integer.parseInt(event
							.getValue().toString()));

				}
			});

			df.setShowEdges(true);
			df.setNumCols(3);
			df.setTitleOrientation(TitleOrientation.TOP);
			df.setItems(tiText, siOrientation, siCaptions);
			this.setExpanded(true);
			VLayout fl = new VLayout();
			fl.addMember(df);
			int i = 0;
			ArrayList<FieldDefinition> fdis = _formGroup.getFieldDefinitions();
			for (final FieldDefinition dfi : fdis) {
				i++;
				DynamicForm df1 = new DynamicForm();
				TextItem tiFieldName = new TextItem("tiFieldName", "FieldName");
				tiFieldName.setValue(dfi.getFieldName());
				tiFieldName.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						dfi.setFieldName(event.getValue().toString());
					}
				});

				TextItem tiFieldCaption = new TextItem("tiFieldCaption",
						"FieldCaption");
				tiFieldCaption.setValue(dfi.getFieldCaption());
				tiFieldCaption.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						dfi.setFieldCaption(event.getValue().toString());
					}
				});

				SelectItem siFCaptions = createCaptionCombo(
						dfi.getFieldCaptionId(), "Caption");
				siFCaptions.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						dfi.setFieldCaptionId(Integer.parseInt(event.getValue()
								.toString()));

					}
				});
				df1.setItems(tiFieldName, tiFieldCaption, siFCaptions);

				df1.setNumCols(3);
				df1.setTitleOrientation(TitleOrientation.TOP);
				df1.setIsGroup(true);
				df1.setGroupTitle("Field #" + i);
				fl.addMember(df1);
			}

			this.setItems(fl);
		}

		private SelectItem createCaptionCombo(int id, String title) {
			LinkedHashMap<String, String> _map = new LinkedHashMap<String, String>();
			_map.put("0", "");
			Set<Integer> keys = DocFlow.captions.keySet();
			for (Integer key : keys) {
				_map.put(key + "", DocFlow.captions.get(key));
			}
			SelectItem siCaptions = new SelectItem();
			siCaptions.setValueMap(_map);
			siCaptions.setTitle(title);
			siCaptions.setValue("" + id);
			return siCaptions;
		}
	}

	public static void showIt(final String xml, WDocType wdt) {
		WDocTypeXMLCaptions w = new WDocTypeXMLCaptions(xml, wdt);
		w.show();
	}

	public WDocTypeXMLCaptions(String xml, final WDocType wdt) {

		super();
		setMembersMargin(5);
		setTitle("დოკუმენტის ტიპი XML");

		setWidth(500);

		final FormDefinition fd = new FormDefinition();
		fd.setXml(xml);

		SectionStack sectionStack = new SectionStack();
		sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
		SectionStackSection[] items = new SectionStackSection[fd
				.getFormGroups().size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = new GroupSelection(fd.getFormGroups().get(i), i + 1);
		}
		sectionStack.setSections(items);
		this.addItem(sectionStack);
		HLayout hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		IButton bSave = new IButton("Save", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				wdt.taiXML.setValue(fd.toXmlString());
			}

		});
		IButton bCancel = new IButton("Cancel");
		bCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		hl.addMember(bSave);
		hl.addMember(bCancel);
		hl.setWidth100();
		hl.setMembersMargin(10);
		hl.setHeight("30");
		Label l = new Label();
		l.setTitle("");
		l.setContents("");
		l.setWidth(0);
		hl.addMember(l);
		this.addItem(hl);
		setHeight(800);
		setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		// this.setShowCloseButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		// this.setShowModalMask(true);
		this.centerInPage();
	}

	@SuppressWarnings("unused")
	private void setResult(Throwable caught) {
		if (caught != null) {
			SC.say("შეცდომა", caught.getMessage(), new BooleanCallback() {
				@Override
				public void execute(Boolean value) {

				}
			});
			return;
		} else {

			destroy();
		}
	}
}
