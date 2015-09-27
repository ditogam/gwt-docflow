package com.docflow.client.components.hr;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.shared.Language;
import com.docflow.client.DocFlow;
import com.docflow.shared.hr.Captions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class WCaptions extends Window {

	private ArrayList<FormItem> formItems;

	private Long id;

	private LanguageValueSet valueSet;

	private static final String LANGUAGE_PR = "LANGUAGE_PR";

	public static void showForm(boolean simple, Long id, String title,
			LanguageValueSet valueSet, HashMap<Integer, Captions> captions) {
		WCaptions c = new WCaptions(simple, id, title, valueSet, captions);
		c.show();
	}

	public WCaptions(boolean simple, Long id, String title,
			LanguageValueSet valueSet, HashMap<Integer, Captions> captions) {
		super();

		setMembersMargin(5);
		this.valueSet = valueSet;
		this.id = id;
		setTitle(DocFlow.getCaption(119));
		DynamicForm df = new DynamicForm();
		formItems = new ArrayList<FormItem>();
		ArrayList<Language> langs = DocFlow.langs;
		for (Language language : langs) {

			FormItem fi = null;
			if (simple) {

				fi = new TextItem("TI" + language.getId(),
						language.getLanguage_name());
			} else {
				fi = new TextAreaItem("TI" + language.getId(),
						language.getLanguage_name());
			}
			fi.setAttribute(LANGUAGE_PR, language.getId());
			if (language.getId() == 1) {
				fi.setAttribute(
						"crgs",
						"97:4304;98:4305;103:4306;100:4307;101:4308;118:4309;122:4310;84:4311;105:4312;107:4313;108:4314;109:4315;110:4316;111:4317;112:4318;74:4319;114:4320;115:4321;116:4322;117:4323;102:4324;113:4325;82:4326;121:4327;83:4328;67:4329;99:4330;90:4331;119:4332;87:4333;120:4334;106:4335;104:4336");
			}
			final FormItem fiF = fi;
			final FocusHandler bh = new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					String str = fiF.getAttribute("crgs");
					if (str == null || str.trim().length() == 0)
						return;
					fiF.setAttribute("crgs", (String) null);
					// registerKeyPressElement(fiF.getName(), str);
				}
			};
			fi.addFocusHandler(bh);
			Captions c = captions.get(language.getId());
			if (c != null)
				fi.setValue(c.getCvalue());
			fi.setWidth("100%");
			formItems.add(fi);
		}
		df.setAlign(Alignment.LEFT);
		df.setTitleOrientation(TitleOrientation.TOP);
		df.setWidth100();
		df.setNumCols(1);
		df.setAutoHeight();
		df.setFields(formItems.toArray(new FormItem[] {}));
		this.addItem(df);
		HLayout hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		IButton bSave = new IButton("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
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
		setHeight(40 + 170 + (simple ? 0 : 230));
		setWidth(500);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);

		this.setIsModal(true);
		this.setShowFooter(true);

		this.centerInPage();
		// createKeyPressElement(this);
	}

	protected void saveData() {
		final Captions[] captions = new Captions[formItems.size()];
		String tmpValue = "";
		for (int i = 0; i < captions.length; i++) {
			FormItem formItem = formItems.get(i);
			Integer lang = formItem.getAttributeAsInt(LANGUAGE_PR);
			lang = lang == null ? -1 : lang.intValue();
			id = id == null ? -1 : id.longValue();
			captions[i] = new Captions(id, lang);
			Object obj = formItem.getValue();
			obj = obj == null ? "" : obj;
			captions[i].setCvalue(obj.toString());
			if (lang.intValue() == DocFlow.language_id)
				tmpValue = obj.toString();
		}

		final String value = tmpValue;
		DocFlow.docFlowService.saveCaptions(captions,
				new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						valueSet.setValue(result, value);
						destroy();
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());

					}
				});

	}

	public int replaceChar(int ch) {
		char newCode = (char) ch;

		int k = (int) (newCode);
		return k;
	}

	public static native void createKeyPressElement(WCaptions captions)/*-{
																		$wnd.keyboardchange = function(ch) {
																		return captions.@com.docflow.client.components.hr.WCaptions::replaceChar(I)(ch);
																		};
																		}-*/;

	public static native void registerKeyPressElement(String elemName,
			String chng)/*-{
						var elem = $doc.getElementsByName(elemName)[0];
						elem.setAttribute("changes", chng);
						$wnd.setkeyboardPress(elem);

						}-*/;

}
