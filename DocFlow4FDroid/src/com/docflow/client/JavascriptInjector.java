package com.docflow.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;

public class JavascriptInjector {

	private static HeadElement head;

	public static ScriptElement inject(String javascript) {
		HeadElement head = getHead();
		ScriptElement element = createScriptElement();
		element.setText(javascript);
		head.appendChild(element);
		return element;
	}

	public static ScriptElement injectPath(String javascript) {
		HeadElement head = getHead();
		ScriptElement element = createScriptElement();
		element.setAttribute("src", javascript);
		head.appendChild(element);
		return element;
	}

	public static void removeInject(ScriptElement elem) {
		HeadElement head = getHead();
		head.removeChild(elem);
	}

	public static native void removeJs(String _js)/*-{
		var _foo = $wnd[_js];
		var _foo_del;
		delete _foo;
		_foo = _foo_del;
		$wnd[_js] = _foo_del;
	}-*/;

	private static ScriptElement createScriptElement() {
		ScriptElement script = Document.get().createScriptElement();
		script.setAttribute("language", "javascript");
		return script;
	}

	private static HeadElement getHead() {
		if (head == null) {
			Element element = Document.get().getElementsByTagName("head").getItem(0);
			assert element != null : "HTML Head element required";
			HeadElement head = HeadElement.as(element);
			JavascriptInjector.head = head;
		}
		return JavascriptInjector.head;
	}

}