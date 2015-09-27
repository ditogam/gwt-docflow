package com.workflow.client.designer.components;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.client.widgets.BaseWidget;
import com.smartgwt.client.widgets.form.PropertySheet;

@BeanFactory.FrameworkClass
@BeanFactory.ScClassName("PropertyEditor")
public class PropertyEditor extends PropertySheet {

	public static PropertyEditor getOrCreateRef(JavaScriptObject jsObj) {
		if (jsObj == null)
			return null;
		final BaseWidget refInstance = BaseWidget.getRef(jsObj);
		if (refInstance == null) {
			return new PropertyEditor(jsObj);
		} else {
			assert refInstance instanceof PropertySheet;
			return (PropertyEditor) refInstance;
		}
	}

	public PropertyEditor() {
		scClassName = "PropertyEditor";
	}

	public PropertyEditor(JavaScriptObject jsObj) {
		scClassName = "PropertyEditor";
		setJavaScriptObject(jsObj);
	}

	public native boolean isBasic() /*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		return self.isBasic();
	}-*/;

	public native void switch_to_AttributeMode()/*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		self.autoDraw = false;
		self.autoFocus = false;
		self.overflow = "auto";
		self.alwaysShowVScrollbar = true;
		self.showAttributes = true;
		self.showMethods = false;
		self.basicMode = true;

	}-*/;

	public native void switch_to_MethodeMode()/*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		self.sortFields = true;
		self.autoDraw = false;
		self.autoFocus = false;
		self.overflow = "auto";
		self.alwaysShowVScrollbar = true;
		self.showAttributes = false;
		self.showMethods = true;
	}-*/;

	protected native JavaScriptObject create()/*-{
		var config = this.@com.smartgwt.client.widgets.BaseWidget::getConfig()();
		var scClassName = this.@com.smartgwt.client.widgets.BaseWidget::scClassName;
		var widget = $wnd.isc[scClassName].create(config);
		if ($wnd.isc.keepGlobals)
			this.@com.smartgwt.client.widgets.BaseWidget::internalSetID(Lcom/google/gwt/core/client/JavaScriptObject;)(widget);
		this.@com.smartgwt.client.widgets.BaseWidget::doInit()();
		widget.creator = widget;
		return widget;
	}-*/;

	public native void editComponent(JavaScriptObject jsObj, String class_name) /*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		self.editComponent(jsObj, class_name);
	}-*/;

	public native void switchMode() /*-{
		var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
		self.switchMode();
	}-*/;
}
