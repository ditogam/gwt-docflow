package com.workflow.server.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.isomorphic.taglib.BaseTag;
import com.isomorphic.util.DataTools;
import com.workflow.server.utils.DMIUtils;

public class LoadSystemModulesTag extends BaseTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3806392011493761777L;

	private String concatString(List<String> list) {
		String result = "";
		for (String str : list) {
			if (!result.isEmpty())
				result += ",";
			result += "\"" + str + "\"";
		}
		return result;
	}

	public int doStartTag() throws JspException {
		init();
		try {
			this.pageContext
					.getOut()
					.write("<script type=\"text/javascript\" language=\"javascript\">\n");
			List<Map<?, ?>> systems = DMIUtils.findRecordsByCriteria(
					"SystemDS", null, null);
			for (Map<?, ?> map : systems) {
				Object load_modules = map.get("load_modules");
				int id = Integer.parseInt(map.get("id").toString());
				if (load_modules == null
						|| load_modules.toString().length() < 2)
					continue;
				ArrayList<String> jsFiles = new ArrayList<String>();
				ArrayList<String> cssFiles = new ArrayList<String>();
				for (String module : load_modules.toString().split(",")) {
					ArrayList<String> files = LoadProjectJavaScriptsTag
							.travelByDir(this.pageContext.getServletContext(),
									module);
					for (String file : files) {
						if (file.toLowerCase().endsWith(".css"))
							cssFiles.add(file);
						else
							jsFiles.add(file);
					}
				}
				String strJs = concatString(jsFiles);
				String strcss = concatString(cssFiles);
				this.pageContext.getOut().write(
						"\tSYSTEM_MODULES['" + id + "']= {jsmodules:[" + strJs
								+ "], cssmodules:[" + strcss + "]};\n");
			}
		} catch (Throwable e) {
			this.log.error(
					"Exception while attempting to process a loadSystemSchema tag.",
					e);
			throw new JspException(DataTools.getStackTrace(e));
		} finally {
			try {
				this.pageContext.getOut().write("</script>");
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return 0;
	}

}
