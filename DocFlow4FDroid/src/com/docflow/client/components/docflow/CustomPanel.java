package com.docflow.client.components.docflow;

import java.util.HashMap;

import com.docflow.client.DocFlow;
import com.docflow.client.JavascriptInjector;
import com.docflow.client.components.CardLayoutCanvas;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.docflow.DocType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.layout.VLayout;

public class CustomPanel {
	private int id;
	private CPDocType cp;
	public static final HashMap<Integer, CustomPanel> allPanels = new HashMap<Integer, CustomPanel>();
	private String canvas_name;
	private ScriptElement element;

	private void Attach() {
		DocFlow.docFlow.card.showCard(canvas_name);
	}

	public static native JavaScriptObject exucuteFunction(String functionName)/*-{
		return $wnd[functionName]();
	}-*/;

	public static native void addMember(JavaScriptObject parent,
			JavaScriptObject child)/*-{
		parent.addMember(child);
	}-*/;

	public CustomPanel(CPDocType cp) {
		id = cp.getId();
		canvas_name = CardLayoutCanvas.CUSTOM_PANEL + id;
		this.cp = cp;
		try {
			String js = cp.getContent().trim();
			element = JavascriptInjector.inject(js);
			JavaScriptObject js_o = exucuteFunction(cp.getMainFunction());
			VLayout vl = new VLayout();
			vl.setWidth100();
			vl.setHeight100();
			addMember(vl.getOrCreateJsObj(), js_o);
			cp.setCanvas(vl);
		} catch (Throwable e) {
			DocTypeTreeGrid.attachDocflowPanel(id);
			return;
		}
		DocFlow.docFlow.card.addCard(canvas_name, cp.getCanvas());
		Attach();
		allPanels.put(id, this);
	}

	public static void destroyAndRecreate(final int id) {
		CustomPanel panel = allPanels.get(id);
		if (panel == null)
			return;
		DocFlow.docFlow.card.showCard(CardLayoutCanvas.DOCFLOW_PANEL);
		panel.cp.getCanvas().destroy();
		try {
			JavascriptInjector.removeInject(panel.element);

		} catch (Throwable e) {
			// TODO: handle exception
		}
		String[] jsElements = panel.cp.getJsElements();
		for (String elem : jsElements) {
			try {
				JavascriptInjector.removeJs(elem);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		allPanels.remove(id);
		attachPanel(id);
	}

	public static void attachPanel(final int id) {
		if (allPanels.containsKey(id)) {
			allPanels.get(id).Attach();
			return;
		}
		SplashDialog.showSplash();
		DocFlow.docFlowService.getDocType(id, DocFlow.language_id,
				new AsyncCallback<DocType>() {

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();

					}

					@Override
					public void onSuccess(DocType result) {
						SplashDialog.hideSplash();
						CPDocType cp = CPDocType.getCustomType(result);
						if (cp == null) {
							DocTypeTreeGrid.attachDocflowPanel(id);
							return;
						}
						new CustomPanel(cp);
					}
				});
	}
}
