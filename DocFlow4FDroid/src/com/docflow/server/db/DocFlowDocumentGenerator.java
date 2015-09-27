package com.docflow.server.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nanoxml.XMLElement;

import com.docflow.shared.Report;
import com.docflow.shared.ReportHeaderItem;
import com.docflow.shared.docflow.DocumentShort;

public class DocFlowDocumentGenerator {
	@SuppressWarnings("rawtypes")
	public DocFlowDocumentGenerator(HttpServletRequest request,
			HttpServletResponse response, ServletContext context) {

		Map pmap = request.getParameterMap();
		if (pmap.isEmpty())
			return;
		int languageId = 1;
		long startdate = 0;
		long enddate = 0;
		ArrayList<DocumentShort> docs = null;
		ArrayList<String> criterias = new ArrayList<String>();
		try {
			Set set = pmap.keySet();
			for (Object key : set) {
				Object value = pmap.get(key);
				System.out.println("key=" + key + " value=" + value.toString());
			}
			languageId = Integer.parseInt(((String[]) pmap.get("language"))[0]);
			startdate = Long.parseLong(((String[]) pmap.get("startdate"))[0]);
			enddate = Long.parseLong(((String[]) pmap.get("enddate"))[0]);
			String xml = ((String[]) pmap.get("xml"))[0];
			xml = xml == null ? "" : xml.trim();
			if (xml.length() > 0) {
				try {
					XMLElement el = new XMLElement();
					el.parseString(xml);
					Vector childs = el.getChildren();
					for (Object o : childs) {
						if (o instanceof XMLElement) {
							XMLElement el1 = (XMLElement) o;
							o = el1.getAttribute("V");
							if (o != null)
								criterias.add(o.toString());
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			docs = MDBConnection.getDocList(startdate, enddate, languageId,
					criterias);
		} catch (Exception e) {
			return;
		}
		if (docs == null || docs.isEmpty()) {
			return;
		}
		ArrayList<ReportHeaderItem> headerItems = new ArrayList<ReportHeaderItem>();
		headerItems.add(createHeader("ID", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("ვერსია", ReportHeaderItem.LEFT, 1, 50));
		headerItems.add(createHeader("ნომერი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("თარიღი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("აბონენტი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("აბონენტის სახელი", ReportHeaderItem.LEFT,
				1, 50));
		headerItems.add(createHeader("ტიპი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("ზონა", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("რაიონი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("რეგიონი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("მომხმარებელი", ReportHeaderItem.LEFT, 1,
				30));
		headerItems.add(createHeader("სტატუსი", ReportHeaderItem.LEFT, 1, 30));
		headerItems.add(createHeader("კონტრ.რგოლი", ReportHeaderItem.LEFT, 1,
				30));
		headerItems
				.add(createHeader("დამატებითი", ReportHeaderItem.LEFT, 1, 30));

		Report rep = new Report();
		rep.setHeaders(headerItems.toArray(new ReportHeaderItem[] {}));
		ArrayList<String[]> data = new ArrayList<String[]>();

		for (DocumentShort d : docs) {
			String[] row = new String[headerItems.size()];
			row[0] = "" + d.getId();
			row[1] = "" + d.getVersion_id();
			row[2] = "" + d.getDoc_flow_num();
			row[3] = "" + new Date(d.getTransaction_date());
			row[4] = "" + d.getCust_id();
			row[5] = "" + d.getCustomer_name();
			row[6] = "" + d.getDoctype();
			row[7] = "" + d.getCzona();
			row[8] = "" + d.getRegionname();
			row[9] = "" + d.getSubregionname();
			row[10] = "" + d.getUser_name();
			row[11] = "" + d.getDocstatus();
			row[12] = "" + d.getController_name();
			row[13] = "" + d.getDoc_template();
			data.add(row);
		}
		rep.setData(data.toArray(new String[][] {}));
		try {
			new ReportGenerator(rep, request, response, context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ReportHeaderItem createHeader(String title, int orientation,
			int cellorientation, int width) {
		ReportHeaderItem item = new ReportHeaderItem();
		item.setTitle(title);
		item.setCellorientation(cellorientation);
		item.setOrientation(orientation);
		item.setWidth(width);
		return item;
	}
}
