package com.workflow.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.workflow.client.designer.VisualBuilder;
import com.workflow.client.designer.components.DesignerBeanFactory;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WorkFlow implements EntryPoint {

	public static final WorkFlowServiceAsync workflowService = GWT
			.create(WorkFlowService.class);
	VLayout v = new VLayout();
	VisualBuilder vb = null;

	public void onModuleLoad() {
		GWT.create(DesignerBeanFactory.class);
		GWT.create(BeanFactory.CanvasMetaFactory.class);
		GWT.create(BeanFactory.FormItemMetaFactory.class);

		v.setHeight100();
		v.setWidth100();
		v.addMember(new IButton("sdfsdf", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (vb == null) {
					vb = VisualBuilder.loadDefault();
					v.addMember(vb);
				}
				vb.setVisible(!vb.isVisible());

			}
		}));
		v.addMember(new IButton("sdfsdf1", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (vb == null) {
					return;
				}
				String s = "" + "isc.TreeGrid.create({\n"
						+ "    ID:'TreeGrid0',\n" + "    autoDraw:false,\n"
						+ "    fields:[\n" + "        {\n"
						+ "            name:'TreeGridField0',\n"
						+ "            title:'TreeGridField0'\n"
						+ "        },\n" + "        {\n"
						+ "            name:'TreeGridField1',\n"
						+ "            title:'TreeGridField1'\n"
						+ "        },\n" + "        {\n"
						+ "            name:'TreeGridField2',\n"
						+ "            title:'TreeGridField2'\n"
						+ "        }\n" + "    ]\n" + "})\n" + "\n" + "\n"
						+ "\n" + "isc.DataView.create({\n"
						+ "    ID:'DataView1',\n" + "    autoDraw:true,\n"
						+ "    height:'100%',\n" + "    overflow:'hidden',\n"
						+ "    width:'100%',\n" + "    members:[\n"
						+ "        TreeGrid0\n" + "    ],\n"
						+ "    modulesDir:'modules/',\n"
						+ "    minMemberSize:'18'\n" + "})";
//				vb.setPaletteNodesFromJS("TTTT", s);
				SC.say("JS=" + vb.setJS() + "\n XML=" + vb.setXML());
			}
		}));
		v.draw();
	}
}
