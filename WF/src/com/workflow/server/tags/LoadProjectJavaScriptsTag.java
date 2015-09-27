package com.workflow.server.tags;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.isomorphic.taglib.BaseTag;
import com.isomorphic.util.DataTools;

public class LoadProjectJavaScriptsTag extends BaseTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3806392011493761777L;
	private String dir;

	public static ArrayList<String> travelByDir(ServletContext pageContext,
			String sdir) throws JspException {
		ArrayList<String> result = new ArrayList<String>();
		String jsDir = pageContext.getRealPath(sdir);
		File dir = new File(jsDir);
		String srootDir = pageContext.getRealPath("d");
		File rootDir = new File(srootDir).getParentFile();

		Collection<File> files = FileUtils.listFiles(dir, new IOFileFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return accept(new File(dir, name));
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory())
					return false;
				String file_name = file.getName().toLowerCase();
				return file_name.endsWith(".js") || file_name.endsWith(".jsp")
						|| file_name.endsWith(".css");
			}
		}, TrueFileFilter.INSTANCE);
		for (File file : files) {
			String jsFile = file.getName();
			File parent = file.getParentFile();
			do {
				jsFile = parent.getName() + "/" + jsFile;

				if (rootDir.equals(parent))
					break;
				parent = parent.getParentFile();
			} while (!rootDir.equals(parent));
			result.add(jsFile);
		}

		return result;
	}

	public int doStartTag() throws JspException {
		init();
		try {
			ArrayList<String> files = travelByDir(
					pageContext.getServletContext(), dir);
			for (String jsFile : files) {
				String sb;
				if (jsFile.toLowerCase().endsWith(".css"))
					sb = "<link rel=\"stylesheet\" type=\"text/css\" href=\""
							+ jsFile + "\" />\n";
				else
					sb = "<script type=\"text/javascript\" src=\"" + jsFile
							+ "\" ></script>\n";
				this.pageContext.getOut().write(sb.toString());
			}

		} catch (Throwable e) {
			this.log.error(
					"Exception while attempting to process a loadSystemSchema tag.",
					e);
			throw new JspException(DataTools.getStackTrace(e));
		}
		return 0;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

}
