package com.workflow.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringEscapeUtils;

import com.isomorphic.datasource.DataSource;
import com.isomorphic.io.ISCFile;
import com.isomorphic.log.Logger;
import com.isomorphic.servlet.BaseServlet;
import com.isomorphic.servlet.DynamicScreenGenerator;
import com.isomorphic.servlet.RequestTimer;
import com.isomorphic.store.DataStructCache;
import com.isomorphic.util.DataTools;
import com.isomorphic.xml.XML;

public class ScreenLoaderServlet extends BaseServlet {
	public static final String PARAM_SCREEN_NAME = "screenName";
	public static final String PROJECT_UI = "project.ui";
	public static final String PARAM_STRUCTURED_RESPONSE = "structuredResponse";
	private static Logger log = new Logger(DataSource.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestTimer requestTimer = new RequestTimer(request);

		String encoding = getServletConfig().getInitParameter("encoding");
		if (encoding == null) {
			encoding = config.getString("RPCManager.defaultCharset", "UTF-8");
		}
		if (!encoding.toLowerCase().equals("none")) {
			request.setCharacterEncoding(encoding);
			response.setContentType("application/javascript;charset="
					+ encoding);
		} else {
			response.setContentType("application/javascript");
		}
		Locale locale = null;
		String localeName = request.getParameter("locale");
		if (localeName != null) {
			locale = DataTools.deriveLocaleFromName(localeName);
			if (locale == null) {
				log.warn("Locale name " + localeName
						+ " is not valid - ignoring");
			}
		}
		if (locale == null) {
			locale = request.getLocale();
		}
		try {
			Writer writer = new StringWriter();
			String screenNames = request.getParameter("screenName");
			Map<String, String[]> mp = request.getParameterMap();
			if (screenNames == null) {
				log.warn("Parameter \"screenName\" is not specified.");
			} else if ("".equals(screenNames.trim())) {
				log.warn("No data specified in parameter \"screenName\".");
			} else {
				log.debug("Requested screens:" + screenNames);
				if (request.getParameter("structuredResponse") == null) {
					writer.write(screenNames.toString());
					writer.write("\r\n");
					// for (Object _screenName : DataTools
					// .commaSeparatedStringToList(_screenName)) {
					// String screenName = _screenName.toString().trim();
					// log.debug("Processing screen \"" + screenName + "\".");
					// if (!"".equals(screenName)) {
					// try {
					// String screen = request.getParameter("content");//
					// loadScreen(screenName,
					// // locale);
					// Writer screenWriter = new StringWriter();
					// XML.toJS("<isomorphicXML>" + screen
					// + "</isomorphicXML>", locale,
					// screenWriter);
					//
					// screen = screenWriter.toString();
					// if (screen == null) {
					// log.warn("Screen \""
					// + screenName
					// + "\" not found in configured locations. Skipping.");
					// } else {
					// writer.write(screen.toString());
					// writer.write("\r\n");
					// }
					// } catch (Exception ex) {
					// log.error("Failed to load screen \""
					// + screenName + "\". Skipping.", ex);
					// }
					// } else {
					// log.warn("Empty string specified as screen name. Skipping.");
					// }
					// }
				} else {
					writer.write("{\n\r");
					writer.write("  screens: [\n\r");
					char separator = ' ';
					for (Object _screenName : DataTools
							.commaSeparatedStringToList(screenNames)) {
						String screenName = _screenName.toString().trim();
						screenName = screenName.trim();
						log.debug("Processing screen \"" + screenName + "\".");
						if (!"".equals(screenName)) {
							try {
								String screen = loadScreen(screenName, locale);
								if (screen == null) {
									log.warn("Screen \""
											+ screenName
											+ "\" not found in configured locations. Skipping.");
								} else {
									writer.write("      " + separator
											+ "{ screenName:\"" + screenName
											+ "\",\n\r");
									writer.write("        source:\""
											+ StringEscapeUtils
													.escapeJavaScript(screen
															.toString())
											+ "\"}\n\r");
									writer.write("\r\n");
									separator = ',';
								}
							} catch (Exception ex) {
								log.error("Failed to load screen \""
										+ screenName + "\". Skipping.", ex);
							}
						} else {
							log.warn("Empty string specified as screen name. Skipping.");
						}
					}
					writer.write("      ]\n\r");
					writer.write("}\n\r");
					writer.write("\r\n");
				}
			}
			log.debug("Generated response:" + writer.toString());
			response.setStatus(200);
			PrintWriter rw = response.getWriter();
			rw.print(writer.toString());
			rw.close();
		} catch (Throwable e) {
			handleError(response, e);
		} finally {
			requestTimer.stop();
			try {
				response.flushBuffer();
			} catch (IOException ignored) {
			}
		}
	}

	public static String loadScreen(String screenName, Locale locale)
			throws Exception {
		String screen = getDynamicScreen(screenName);
		if (screen == null) {
			String screenFile = DataStructCache.getInstanceFile(screenName,
					"ui", "xml");
			if (screenFile != null) {
				screen = new ISCFile(screenFile).getAsString();
			}
		}
		if (screen == null) {
			InputStream st = ScreenLoaderServlet.class
					.getResourceAsStream("test.txt");
			screen = Streams.asString(st);
			st.close();
		}
		if (screen != null) {
			Writer screenWriter = new StringWriter();
			XML.toJS("<isomorphicXML>" + screen + "</isomorphicXML>", locale,
					screenWriter);

			return screenWriter.toString();
		}
		return null;
	}

	private static DynamicScreenGenerator defaultDynamicScreenGenerator = null;
	private static Map dynamicScreenGenerators = Collections
			.synchronizedMap(new LinkedMap());

	public static void addDynamicScreenGenerator(DynamicScreenGenerator dsg) {
		defaultDynamicScreenGenerator = dsg;
	}

	public static void addDynamicScreenGenerator(DynamicScreenGenerator dsg,
			String prefix) {
		dynamicScreenGenerators.put(prefix, dsg);
	}

	public static void addDynamicScreenGenerator(DynamicScreenGenerator dsg,
			Pattern regex) {
		dynamicScreenGenerators.put(regex, dsg);
	}

	public static DynamicScreenGenerator removeDynamicScreenGenerator() {
		DynamicScreenGenerator dsg = defaultDynamicScreenGenerator;
		defaultDynamicScreenGenerator = null;
		return dsg;
	}

	public static DynamicScreenGenerator removeDynamicScreenGenerator(
			String prefix) {
		if (dynamicScreenGenerators.containsKey(prefix)) {
			DynamicScreenGenerator dsg = (DynamicScreenGenerator) dynamicScreenGenerators
					.get(prefix);
			dynamicScreenGenerators.remove(prefix);
			return dsg;
		}
		return null;
	}

	public static DynamicScreenGenerator removeDynamicScreenGenerator(
			Pattern regex) {
		if (dynamicScreenGenerators.containsKey(regex)) {
			DynamicScreenGenerator dsg = (DynamicScreenGenerator) dynamicScreenGenerators
					.get(regex);
			dynamicScreenGenerators.remove(regex);

			return dsg;
		}
		return null;
	}

	public static void clearDynamicScreenGenerators() {
		defaultDynamicScreenGenerator = null;
		dynamicScreenGenerators.clear();
	}

	private static String getDynamicScreen(String id) {
		DynamicScreenGenerator dsg = null;
		for (Iterator i = dynamicScreenGenerators.keySet().iterator(); i
				.hasNext();) {
			Object keyObj = i.next();
			if ((keyObj instanceof String)) {
				if (id.indexOf((String) keyObj) == 0) {
					dsg = (DynamicScreenGenerator) dynamicScreenGenerators
							.get(keyObj);
					break;
				}
			} else if ((keyObj instanceof Pattern)) {
				Pattern p = (Pattern) keyObj;
				Matcher m = p.matcher(id);
				if (m.find()) {
					dsg = (DynamicScreenGenerator) dynamicScreenGenerators
							.get(keyObj);
					break;
				}
			} else {
				log.warn("In the dynamicScreenGenerators list, we found a key of type "
						+ keyObj.getClass().getName() + ". Ignoring");
			}
		}
		if (dsg == null) {
			dsg = defaultDynamicScreenGenerator;
		}
		String screen = null;
		if (dsg != null) {
			screen = dsg.getScreen(id);
		}
		return screen;
	}
}
