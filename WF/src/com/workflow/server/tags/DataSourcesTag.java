package com.workflow.server.tags;

import java.io.File;
import java.io.FilenameFilter;

import javax.servlet.jsp.JspException;

import com.isomorphic.taglib.BaseTag;

public class DataSourcesTag extends BaseTag {

	public static String DS_SUFIX = ".ds.xml";

	private String dir;
	private String prefix;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5350064442541807818L;

	public int doStartTag() throws JspException {
		init();
		try {
			String dsDir = this.pageContext.getServletContext()
					.getRealPath(dir);

			File dir = new File(dsDir);

			String[] files = dir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.toLowerCase().endsWith(DS_SUFIX);
				}
			});

			StringBuffer sb = new StringBuffer(
					"<script type=\"text/javascript\" language=\"javascript\"src=\""
							+ prefix + "DataSourceLoader?dataSource=");
			StringBuffer ds = new StringBuffer();

			for (int i = 0; i < files.length; i++) {
				String filename = files[i];

				String dsName = filename.substring(0,
						filename.lastIndexOf(DS_SUFIX));
				if (i > 0)
					sb.append(",");
				sb.append(dsName);
				ds.append("\n\tALL_DATASOURCES.push(\"" + dsName + "\");");
			}

			sb.append("\"></script>\n");
			String s = "<script type=\"text/javascript\" language=\"javascript\">\n\t"
					+ ds + "\n</script>\r";
			sb.append(s);
			this.pageContext.getOut().write(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
