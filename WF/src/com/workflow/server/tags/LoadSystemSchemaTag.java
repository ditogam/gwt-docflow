package com.workflow.server.tags;

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
	/**
	 * 
	 */
	private static final long serialVersionUID = 8103132603078932327L;

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

	@SuppressWarnings("rawtypes")
	public static List<String> getFrameworkDataSources() {
		List<String> dsList = new ArrayList<String>();

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

	@SuppressWarnings("rawtypes")
	public static void loadFrameworkDataSources(Writer out) throws Exception {

		Map loadedSoFar = new HashMap();

		List dsList = getFrameworkDataSources();
		for (Iterator i = dsList.iterator(); i.hasNext();) {
			String dsName = (String) i.next();
			outputDSForName(dsName, out, loadedSoFar);
		}
		String _format = "\nmakeFieldsBasics('%s',[%s]);";
		String var = "";
		for (String ds : listAddCaption) {
			var += String.format(_format, ds, "'title'");
		}
		var += String.format(_format, "DynamicForm", "'groupTitle','isGroup'");
		var += String.format(_format, "ComboBoxItem",
				"'valueField','displayField'");
		var += String.format(_format, "SelectItem",
				"'valueField','displayField'");
		out.write(var);
	}

	private String addCaption;
	private String addDSAndDependies;
	private static ArrayList<String> listAddCaption;
	private static ArrayList<String> listAddDSAndDependies;

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
								"fields:[\n\t{\n\t\txmlAttribute:true,title:\"Caption\",\n\t\tname:\"caption_id\",\n\t\ttype:\"integer\",\n\t\teditorType:\"CaptionEditor\",\n\t\tbasic:true,group : \"basics\"\n\t},");
			}

			if ("DynamicForm".equals(dataSourceId)) {
				dsDef = LanguageTag
						.replaceStringOnce(
								dsDef,
								"fields:[",
								"fields:[\n\t{\n\t\txmlAttribute:true,title:\"Value Manager\",\n\t\tname:\"value_manager_group\",\n\t\ttype:\"text\",\n\t\tbasic:true,group : \"basics\"\n\t},");
			}

			// if (listAddDSAndDependies.contains(dataSourceId)) {
			// out.write("\nALL_DEPENDENCY.push('" + dataSourceId + "');\n");
			// dsDef = LanguageTag
			// .replaceStringOnce(
			// dsDef,
			// "fields:[",
			// "fields:[\n\t{\n\t\txmlAttribute:true,title:\"Dependency\",\n\t\tname:\"dependency\",\n\t\ttype:\"text\",\n\t\teditorType:\"DependencyEditor\",\n\t\tbasic:true,group : \"basics\"\n\t},");
			// }
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

	public String getAddDSAndDependies() {
		return addDSAndDependies;
	}

	public void setAddDSAndDependies(String addDSAndDependies) {
		listAddDSAndDependies = new ArrayList<String>();
		String[] tmp = addDSAndDependies.split(",");
		for (String s : tmp) {
			listAddDSAndDependies.add(s);
		}
		this.addDSAndDependies = addDSAndDependies;
	}
}
