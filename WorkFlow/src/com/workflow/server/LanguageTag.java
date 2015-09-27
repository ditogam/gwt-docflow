package com.workflow.server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.isomorphic.taglib.BaseTag;

public class LanguageTag extends BaseTag {
	private static String DS_SUFIX = ".ds.xml";
	private static String CaptionsDS = "CaptionsDS" + DS_SUFIX;
	private static String OTHER_FILED_PROPS = "OTHER_FILED_PROPS";
	private static String OTHER_FILEDS = "OTHER_FILEDS";
	private static String OTHER_SELECT_FILEDS = "OTHER_SELECT_FILEDS";

	private static String OTHER_UPDATE_FILEDS = "OTHER_UPDATE_FILEDS";
	private static String OTHER_UPDATETABLE_FILEDS = "OTHER_UPDATETABLE_FILEDS";
	private String dir;
	private static Boolean CAPTION_DS_CREATED = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5350064442541807818L;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	private String readFile(Reader r) throws IOException {
		BufferedReader reader = new BufferedReader(r);
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		return stringBuilder.toString();
	}

	private void close(Closeable... cls) {
		for (Closeable closeable : cls) {
			if (closeable != null)
				try {
					closeable.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
	}

	public static String replaceString(String source, String dest, String with) {

		int ind = source.indexOf(dest);
		if (ind > 0) {
			source = source.substring(0, ind) + with
					+ source.substring(ind + dest.length());
			return replaceString(source, dest, with);
		} else
			return source;

	}

	public static String replaceStringOnce(String source, String dest,
			String with) {

		int ind = source.indexOf(dest);
		if (ind > 0) {
			source = source.substring(0, ind) + with
					+ source.substring(ind + dest.length());
			return source;
		} else
			return source;

	}

	public int doStartTag() throws JspException {
		init();
		try {
			StringBuffer dbFieldDef = new StringBuffer();
			StringBuffer dbFields = new StringBuffer();
			StringBuffer dbSelectFields = new StringBuffer();
			StringBuffer dbUpdateubleFields = new StringBuffer();
			StringBuffer dbUpdateFields = new StringBuffer();

			List<Map<?, ?>> list = DMIUtils.findRecordsByCriteria("LanguageDS",
					null, new HashMap());
			StringBuffer sb = new StringBuffer(
					"<script type=\"text/javascript\">\n");
			int i = 0;
			for (Map<?, ?> map : list) {
				Object id = map.get("id");
				Object lngName = map.get("language_name");
				String lng = "{id:" + id + ",name:'" + lngName + "'}";
				lng = "\tLANGUAGES.push(" + lng + ")";
				String field_name = "language_" + id;
				dbFields.append(",getcaption(id," + id + ") " + field_name);
				dbSelectFields.append("," + field_name);
				dbFieldDef.append("\t\t<field name=\"" + field_name
						+ "\" title=\"" + lngName + "\" type=\"text\" />\n");

				dbUpdateubleFields.append(",$criteria." + field_name + " "
						+ field_name);

				if (!dbUpdateFields.toString().isEmpty())
					dbUpdateFields.append(",");

				dbUpdateFields.append("HSTORE('lang_id','" + id
						+ "')||HSTORE('val',$criteria." + field_name + ")");

				i++;
				sb.append(lng + "\r");
			}
			sb.append("</script>\r");
			this.pageContext.getOut().write(sb.toString());
			if (!CAPTION_DS_CREATED) {
				String dsDir = this.pageContext.getServletContext()
						.getRealPath(dir);
				File dir = new File(dsDir);
				InputStream str = null;
				Reader r = null;
				FileOutputStream fos = null;
				try

				{
					str = this.getClass().getResourceAsStream(CaptionsDS);
					r = new InputStreamReader(str);
					fos = new FileOutputStream(new File(dir, CaptionsDS));
					String dsStr = readFile(r);
					dsStr = dsStr.replaceAll(OTHER_FILEDS, dbFields.toString());
					dsStr = dsStr.replaceAll(OTHER_FILED_PROPS,
							dbFieldDef.toString());
					dsStr = dsStr.replaceAll(OTHER_SELECT_FILEDS,
							dbSelectFields.toString());

					dsStr = replaceString(dsStr, OTHER_UPDATETABLE_FILEDS,
							dbUpdateubleFields.toString());
					dsStr = replaceString(dsStr, OTHER_UPDATE_FILEDS, "Array["
							+ dbUpdateFields.toString() + "]::hstore[]");

					fos.write(dsStr.getBytes("UTF8"));
					fos.flush();
					CAPTION_DS_CREATED = true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					close(str, r, fos);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
