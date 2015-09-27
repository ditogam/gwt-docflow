package com.rd.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.rdcommon.client.ClientGlobalSettings;
import com.rdcommon.client.components.ds.GROperationBindings;
import com.rdcommon.client.components.ds.PDSDefinition;
import com.rdcommon.client.components.ds.PDSGroupTree;
import com.rdcommon.client.components.ds.PGroupEdit;
import com.rdcommon.shared.GlobalValues;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RDesigner implements EntryPoint {
	public static final GreetingServiceAsync docFlowService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */

	PPanel headingElement;

	public void onModuleLoad() {

		Boolean b = new Boolean("true");
		System.out.println(b);
		// DSDefinition ds = new DSDefinition();
		//
		// DSFormDefinition form = new DSFormDefinition();
		// form.setName("main");
		// form.setDsGroups(new ArrayList<DSGroup>());
		// DSGroup gr = new DSGroup();
		// DSComponent dynamicFieldProps = new DSComponent();
		// dynamicFieldProps.setAdditionalProps(new TreeMap<String, String>());
		// dynamicFieldProps.getAdditionalProps().put("groupTitle",
		// "5px solid green");
		// dynamicFieldProps.getAdditionalProps().put("titleOrientation",
		// "top");
		// gr.setDynamicFieldProps(dynamicFieldProps);
		//
		// gr.setAdditionalProps(new TreeMap<String, String>());
		//
		// gr.getAdditionalProps().put("border", "5px solid green");
		// gr.setWidth(-100);
		// gr.setHeight(-100);
		// gr.setName("main");
		// form.getDsGroups().add(gr);
		//
		// gr = new DSGroup();
		// gr.setWidth(-100);
		// gr.setHeight(-100);
		// gr.setName("main2");
		// form.getDsGroups().add(gr);
		// ds.setSearchForms(new ArrayList<DSFormDefinition>());
		// ds.getSearchForms().add(form);
		// ds.setDsFields(new ArrayList<DSField>());
		//
		// gr.setAdditionalProps(new TreeMap<String, String>());
		//
		// gr.getAdditionalProps().put("border", "1px solid red");
		// DSField field;
		// DSClientFieldDef cl;
		//
		// field = new DSField();
		// field.setfName("ddddd");
		// field.setfTitle("dddddkkkkk");
		//
		// cl = new DSClientFieldDef();
		// cl.setName(field.getfName());
		// cl.setTitle(field.getfName());
		// cl.setType(ClientFieldDef.FT_BOOLEAN);
		// cl.setGroupName("main");
		// field.setSearchProps(cl);
		// ds.getDsFields().add(field);
		//
		// field = new DSField();
		// field.setfName("ddddd1");
		// field.setfTitle("dddddkkkkkb");
		// cl = new DSClientFieldDef();
		// cl.setName(field.getfName());
		// cl.setTitle(field.getfName());
		// cl.setGroupName("main");
		// cl.setType(ClientFieldDef.FT_DATE);
		// field.setSearchProps(cl);
		// ds.getDsFields().add(field);
		//
		// field = new DSField();
		// field.setfName("ccc");
		// field.setfTitle("ccc1");
		// cl = new DSClientFieldDef();
		// cl.setName(field.getfName());
		// cl.setTitle(field.getfName());
		// cl.setType(ClientFieldDef.FT_SELECTION);
		// cl.setDsName("RegionDS");
		// cl.setDsIdField("ppcityid");
		// cl.setDsValueField("ppcityname");
		// cl.setGroupName("main2");
		// cl.setChangeHandlerMethode("testGwt");
		// field.setSearchProps(cl);
		// ds.getDsFields().add(field);
		// DSDefinition ds = new DSDefinition();
		// ds.setId(1);
		// ds.setDsName("DocTypeDS");
		// ds.setDbName("DocFlow");
		// ds.setTableName("v_doc_type_group");
		// ds.setOperationBindings(new ArrayList<String>());
		//
		// DSFormDefinition form = new DSFormDefinition();
		// form.setName("main");
		// form.setDsGroups(new ArrayList<DSGroup>());
		// DSGroup gr = new DSGroup();
		// DSComponent dynamicFieldProps = new DSComponent();
		// dynamicFieldProps.setAdditionalProps(new TreeMap<String, String>());
		// dynamicFieldProps.getAdditionalProps().put("groupTitle",
		// "5px solid green");
		// dynamicFieldProps.getAdditionalProps().put("titleOrientation",
		// "top");
		// gr.setDynamicFieldProps(dynamicFieldProps);
		//
		// gr.setAdditionalProps(new TreeMap<String, String>());
		//
		// gr.getAdditionalProps().put("border", "5px solid green");
		// gr.setWidth(-100);
		// gr.setHeight(-100);
		// gr.setName("main");
		// form.getDsGroups().add(gr);
		//
		// ds.setSearchForms(new ArrayList<DSFormDefinition>());
		// ds.getSearchForms().add(form);
		//
		// ds.setDsFields(new ArrayList<DSField>());
		//
		// gr.setAdditionalProps(new TreeMap<String, String>());
		//
		// gr.getAdditionalProps().put("border", "1px solid red");
		// DSField field;
		// DSClientFieldDef cl;
		//
		// field = new DSField();
		// field.setfName("ppcityid");
		// field.setfTitle("ID");
		// field.setDsfType("integer");
		// field.setPrimaryKey(true);
		//
		// cl = new DSClientFieldDef();
		// cl.setName(field.getfName());
		// cl.setTitle(field.getfName());
		// cl.setType(ClientFieldDef.FT_INTEGER);
		// cl.setGroupName("main");
		// cl.setHidden(true);
		//
		// field.setSearchProps(cl);
		// ds.getDsFields().add(field);
		//
		// field = new DSField();
		// field.setfName("ppcityname");
		// field.setfTitle("Caption");
		// field.setDsfType("text");
		//
		// cl = new DSClientFieldDef();
		// cl.setName(field.getfName());
		// cl.setTitle(field.getfTitle());
		// cl.setGroupName("main");
		// cl.setType(ClientFieldDef.FT_SELECTION);
		// cl.setDsName("DocTypeDS");
		// cl.setAdditionalProps(new TreeMap<String, String>());
		// // cl.getAdditionalProps().put("canEdit", "dfsdfsdf");
		// // cl.setReadOnly(true);
		// cl.setWidth(500);
		// cl.setDsIdField("ppcityid");
		// cl.setDsValueField("ppcityname");
		// cl.setDsIsCustomGenerated(true);
		// field.setSearchProps(cl);
		//
		// ds.getDsFields().add(field);
		//
		// DSClientSearchForm p = new DSClientSearchForm(ds, "main");
		// p.setShowEdges(true);

		docFlowService.getGlobalValues(new AsyncCallback<GlobalValues>() {

			@Override
			public void onSuccess(GlobalValues result) {
				ClientGlobalSettings.globalValues = result;
				PDSDefinition p;
				p = new PDSDefinition(result.getDsDefinitions().get(0));
				p.setWidth100();
				p.setHeight100();
				RootPanel.get().add(p);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

		// JavaScriptObject d = createElement();
		// com.google.gwt.dom.client.Element el = Element.as(d);
		// headingElement = new PPanel(el);
		// VLayout l = new VLayout();
		// l.setShowEdges(true);
		// l.addClickHandler(new ClickHandler() {
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// p.setShowEdges(p.getShowEdges());
		//
		// }
		// });
		// headingElement.add(l);
		// System.out.println(d);

	}

	private static native JavaScriptObject createElement()/*-{
		return $wnd.openwwindowandrender();
	}-*/;
}
