package com.docflow.client.components.common;

import java.io.OutputStream;
import java.io.PrintStream;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ExceptionDialog extends Window {
	private DynamicForm form = null;

	private static ExceptionWrapper convert(Throwable aException) {

		GWT.log(aException.getLocalizedMessage(), aException);

		String message = aException.getMessage();
		String trace = null;

		if (trace == null) {
			OutputStream out = new GWTByteArrayOutputStream();
			aException.printStackTrace(new PrintStream(out));
			trace = out.toString();
		}
		String caption = null;

		return new ExceptionWrapper(message, caption, trace);

	}

	public static void showError(Throwable aException) {
		new ExceptionDialog(aException);
	}

	public ExceptionDialog(Throwable aException) {
		this(convert(aException));
	}

	public ExceptionDialog(ExceptionWrapper aException) {

		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null
						&& event.getKeyName().equals("Escape")) {
					destroy();
				}
			}
		});
		final String COLLAPSE = "collapse.png";
		final String EXPAND = "expand.png";
		final int VISIBLE_HEIGHT = 500;
		final int INVISIBLE_HEIGHT = 115;
		setWidth(600);
		setTitle("Error");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		setShowCloseButton(false);
		setCanDrag(false);
		setCanDragReposition(false);
		setCanDragResize(false);
		setCanDragScroll(false);

		HLayout titleLayout = new HLayout();
		titleLayout.setWidth100();
		titleLayout.setHeight(20);
		titleLayout.setAlign(Alignment.LEFT);
		titleLayout.setMembersMargin(5);

		Img img = new Img();
		img.setSrc("error.png");
		img.setWidth(25);
		img.setHeight(25);

		final Label lName = new Label();
		lName.setWidth100();
		lName.setContents(aException.getDetail() == null ? aException
				.getMessage() : aException.getDetail());
		titleLayout.setMembers(img, lName);
		form = new DynamicForm();

		final ButtonItem bShowError = new ButtonItem("bShowError",
				"Show details");
		bShowError.setIcon(EXPAND);
		bShowError.setEndRow(false);
		final TextAreaItem taiTrace = new TextAreaItem("taiTrace", "Details");
		taiTrace.setWidth(590);
		taiTrace.setHeight(VISIBLE_HEIGHT - INVISIBLE_HEIGHT - 20);
		taiTrace.setVisible(false);
		taiTrace.setCanEdit(false);
		taiTrace.setValue(aException.getTrace());
		taiTrace.setColSpan(2);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setFields(bShowError, taiTrace);
		VLayout hLayout = new VLayout();
		hLayout.setWidth100();
		hLayout.setHeight100();

		bShowError.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean vis = !taiTrace.getVisible();
				if (vis)
					taiTrace.show();
				else
					taiTrace.hide();
				bShowError.setIcon(vis ? COLLAPSE : EXPAND);
			}
		});

		HLayout buttonsLayout = new HLayout();
		buttonsLayout.setWidth100();
		buttonsLayout.setAlign(Alignment.CENTER);
		buttonsLayout.setMembersMargin(5);

		VLayout footer = new VLayout();
		footer.setPadding(5);
		footer.setWidth100();
		footer.setMembersMargin(5);

		IButton closeBtn = new IButton("Close");
		buttonsLayout.addMembers(closeBtn);
		footer.addMembers(buttonsLayout);

		hLayout.addMember(titleLayout);
		hLayout.addMember(form);
		hLayout.addMember(footer);
		addItem(hLayout);

		closeBtn.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				destroy();

			}
		});
		setAutoSize(true);
		setAutoCenter(true);
		show();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible())
					destroy();

			}
		});

	}

}
