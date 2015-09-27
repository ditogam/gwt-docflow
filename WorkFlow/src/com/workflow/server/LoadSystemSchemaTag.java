package com.workflow.server;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.isomorphic.datasource.BasicDataSource;
import com.isomorphic.datasource.DataSourceManager;
import com.isomorphic.io.ISCFile;
import com.isomorphic.js.JSTranslater;
import com.isomorphic.taglib.BaseTag;
import com.isomorphic.util.DataTools;

public class LoadSystemSchemaTag extends BaseTag {
	public int doStartTag() throws JspException {
		init();
		try {
			loadFrameworkDataSources(this.pageContext.getOut());
		} catch (Throwable e) {
			this.log.error(
					"Exception while attempting to process a loadSystemSchema tag.",
					e);
			throw new JspException(DataTools.getStackTrace(e));
		}
		return 0;
	}

	public static List<String> getFrameworkDataSources() {
		List dsList = new ArrayList();

		List paths = config.getCommaSeparatedList("framework.datasources");
		Iterator j;
		if (paths == null)
			staticLog
					.error("LoadSystemSchemaTag.getFrameworkDataSources(): could not get framework.datasources from configuration");
		else
			for (j = paths.iterator(); j.hasNext();) {
				String dsDirPath = ISCFile.canonicalizePath((String) j.next());

				File dsDir = new File(dsDirPath);

				String[] files = dsDir.list();
				if (files == null) {
					staticLog
							.error("LoadSystemSchemaTag.getFrameworkDataSources(): the following path configured for framework.datasources is not a directory: "
									+ dsDirPath);
				} else {
					for (int i = 0; i < files.length; i++) {
						String filename = files[i];

						if ((!filename.equals("Object.ds.xml"))
								&& (!filename.equals("FileAssembly.ds.xml"))
								&& (!filename
										.equals("FileAssemblyEntry.ds.xml"))
								&& (!filename.equals("Filesystem.ds.xml"))) {
							if (filename.endsWith(".ds.xml")) {
								String dsName = filename.substring(0,
										filename.lastIndexOf(".ds.xml"));
								dsList.add(dsName);
							}
						}
					}
				}
			}
		return dsList;
	}

	public static void loadFrameworkDataSources(Writer out) throws Exception {
		JSTranslater jsTrans = JSTranslater.instance();

		Map loadedSoFar = new HashMap();

		List dsList = getFrameworkDataSources();
		for (Iterator i = dsList.iterator(); i.hasNext();) {
			String dsName = (String) i.next();
			outputDSForName(dsName, out, loadedSoFar);
		}
	}

	private String addCaption;
	private static ArrayList<String> listAddCaption;

	public static void outputDSForName(String dataSourceId, Writer out,
			Map loadedSoFar) throws Exception {
		JSTranslater jsTrans = JSTranslater.instance();
		BasicDataSource ds = null;
		if (loadedSoFar.get(dataSourceId) != null)
			return;
		try {
			ds = (BasicDataSource) DataSourceManager.getDataSource(
					dataSourceId, null);
			if (ds == null)
				throw new Exception("No datasource for name: " + dataSourceId);

			String superDSName = ds.getSuperDSName();

			if ((superDSName != null) && (!"Object".equals(superDSName))) {
				outputDSForName(superDSName, out, loadedSoFar);
			}
			StringWriter sw = new StringWriter();
			jsTrans.toJS(ds, sw);
			String dsDef = sw.toString();
			if (listAddCaption.contains(dataSourceId)) {
				dsDef = LanguageTag
						.replaceStringOnce(
								dsDef,
								"fields:[",
								"fields:[\n\t\t{xmlAttribute:true,title:\"Caption\",name:\"caption_id\",type:\"integer\",editorType:\"CaptionEditor\", basic:true,group : \"basics\"},");
			}
			out.write(dsDef);
			loadedSoFar.put(dataSourceId, dataSourceId);
		} finally {
			if (ds != null)
				DataSourceManager.freeDataSource(ds);
		}
		out.write(";\r");
	}

	public String getAddCaption() {
		return addCaption;
	}

	public void setAddCaption(String addCaption) {
		listAddCaption = new ArrayList<String>();
		String[] tmp = addCaption.split(",");
		for (String s : tmp) {
			listAddCaption.add(s);
		}
		this.addCaption = addCaption;
	}
}
