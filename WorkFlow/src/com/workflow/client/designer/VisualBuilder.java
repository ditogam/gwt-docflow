package com.workflow.client.designer;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class VisualBuilder extends VLayout {

	private VisualBuilder(JavaScriptObject jsObj) {
		scClassName = "VisualBuilder";
		setJavaScriptObject(jsObj);
	}

	public static VisualBuilder loadDefault() {
		JavaScriptObject jsObj = loadDefaultComponent();
		VisualBuilder builder = new VisualBuilder(jsObj);
		JavaScriptObject modeSwitcher = builder
				.getAttributeAsJavaScriptObject("modeSwitcher");
		DynamicForm dfmodeSwitcher = DynamicForm.getOrCreateRef(modeSwitcher);
		if (dfmodeSwitcher != null) {
			String[] hide_fields = { "useToolSkin", "skin", "resolution" };
			for (String h : hide_fields) {
				FormItem item = dfmodeSwitcher.getField(h);
				if (item != null)
					item.setVisible(false);

			}
		}
		return builder;
	}

	public native void setPaletteNodesFromJS(String screen_name, String js)/*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		var _screen = self.project.addScreen(null, null, screen_name, false);
		self.setCurrentScreen(_screen);
		self.projectComponents.destroyAll();
		self.projectComponents.addPaletteNodesFromJS(js);
	}-*/;

	private static native JavaScriptObject loadDefaultComponent()/*-{
		$wnd.removeOfflineSaved("VB_AUTOSAVE_PROJECT");
		$wnd.removeOfflineSaved("VB_SINGLE_SCREEN");
		var _vb = $wnd.isc.VisualBuilder.create({
			width : "100%",
			height : "100%",
			autoDraw : true,
			showHelpPane : false,
			modulesDir : 'modules/',
			skin : 'EnterpriseBlue',
			saveFileBuiltinIsEnabled : true,
			loadFileBuiltinIsEnabled : true,
			filesystemDataSourceEnabled : false,
			defaultApplicationMode : "edit",
			showModeSwitcher : true,
			showScreenMenu : false,
			singleScreenMode : true,

			// provide an initial top-level VLayout that is appropriate for a
			// fullscreen app:
			// take up whole browser, never overflow
			initialComponent : {
				type : "DataView",
				defaults : {
					autoDraw : true,
					modulesDir : 'modules/',
					overflow : "hidden",
					width : "100%",
					height : "100%",
					// this is enough to make it obvious that a badly scrunched
					// component
					// such as a ListGrid is actually a scrunched ListGrid and not
					// just a
					// 1px black line (which happens with the default minMemberSize
					// of 1)
					minMemberSize : 18
				}
			}
		});
		_vb.hide();
		var s = "isc.DataView.create({\n" + "    ID:'DataView1',\n"
				+ "    autoDraw:true,\n" + "    height:'100%',\n"
				+ "    overflow:'hidden',\n" + "    width:'100%',\n"
				+ "    members:[],\n" + "    modulesDir:'modules/',\n"
				+ "    minMemberSize:'18'\n" + "})";
		return _vb;
	}-*/;

	public native String setJS()/*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		return self.getUpdatedSource();
	}-*/;

	public native String setXML()/*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		return self.getUpdatedSource();
	}-*/;
}
