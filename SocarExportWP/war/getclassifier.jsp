<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="src.com.socargass.tabletexporter.DBConnection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.TreeMap"%>
<%@page import="nanoxml.XMLElement"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%!static class Classifier {
		public long id;
		public String value;

		public Classifier(long id, String value) {
			this.id = id;
			this.value = value;
		}

	}

	static class ClassifierType {

		public String sql;
		public boolean hasparent;
		public String dbname;

		public ClassifierType(String dbname, String sql, boolean hasparent) {

			this.sql = sql;
			this.hasparent = hasparent;
			this.dbname = dbname;

		}

	}

	static class ClassifierLoader {
		private static TreeMap<String, ClassifierType> types = null;

		private static void init() {
			if (types != null)
				return;
			types = new TreeMap<String, ClassifierType>();
			types.put("region".toLowerCase(), new ClassifierType("Gass",
					"select ppcityid,ppcityname from ppcity", false));
			types.put("subregion".toLowerCase(), new ClassifierType("Gass",
					"select pcityid,pcityname from pcity where ppcityid=?",
					true));
			String sql = "select distinct zone,zone\n"
					+ "  from customer c\n" + " inner join streets s\n"
					+ "    on s.streetid = c.streetid\n"
					+ " inner join city ct\n"
					+ "    on s.cityid = ct.cityid\n"
					+ " where ct.pcityid = ? order by 1";

			types.put("zones".toLowerCase(), new ClassifierType("Gass", sql,
					true));
		}

		private static ArrayList<Classifier> getValues(String name,
				Long parent_id) {
			ArrayList<Classifier> result = new ArrayList<Classifier>();
			try {
				//out.write(name + " finding <br>".toCharArray());
			} catch (Exception e) {
			}
			ClassifierType ct = types.get(name.trim().toLowerCase());
			try {
				//out.write(name + " found <br>".toCharArray());
			} catch (Exception e) {
			}
			if (ct == null)
				return result;
			try {
				//out.write(((ct.hasparent) + " needparent <br>").toCharArray());
			} catch (Exception e) {
			}
			if (ct.hasparent && parent_id == null)
				return result;
			try {
				//out.write((" initing <br>" + ct.dbname + " " + ct.sql)
				//.toCharArray());
			} catch (Exception e) {
			}
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				con = DBConnection.getConnection(ct.dbname);
				try {
					//out.write((" connection = " + con).toCharArray());
				} catch (Exception e) {
				}
				ps = con.prepareStatement(ct.sql);
				if (ct.hasparent) {
					ps.setLong(1, parent_id);
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					Classifier ctp = new Classifier(rs.getLong(1),
							rs.getString(2));
					try {
						//out.write((" ctp = " + ctp + "<br>").toCharArray());
					} catch (Exception e) {
					}
					result.add(ctp);
				}
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));

			} finally {
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					ps.close();
				} catch (Exception e) {
				}
				try {
					con.close();
				} catch (Exception e) {
				}
			}
			return result;
		}

		public static String getValuseForType(String name, Long parent_id) {
			try {
				//out.write("initing<br>".toCharArray());
			} catch (Exception e) {
			}
			XMLElement el = new XMLElement();
			el.setName("Values");
			String result = "<Values>";
			try {
				//out.write(result);
			} catch (Exception e) {
			}
			init();
			try {
				//	out.write("after<br>".toCharArray());
			} catch (Exception e) {
			}
			ArrayList<Classifier> values = getValues(name, parent_id);
			for (Classifier val : values) {
				XMLElement nel = new XMLElement();
				nel.setName("Value");
				nel.setAttribute("id", val.id);
				nel.setAttribute("value", val.value);
				el.addChild(nel);

				try {
					//out.write(result);
				} catch (Exception e) {
				}
				try {
					//out.write("adding" + val.id + " " + val.value + "<br>");
				} catch (Exception e1) {
				}
			}
			result = "</Values>";
			try {
				//out.write(result);
			} catch (Exception e) {
			}
			result = el.toString();
			return result;
		}
	}%>
<%
	String name = request.getParameter("type");
	if (name == null)
		name = "";
	String sparentid = request.getParameter("parentid");
	Long parent_id = null;
	if (sparentid != null) {
		try {
			parent_id = Long.parseLong(sparentid);
		} catch (Exception e) {
		}
	}
	String str = (ClassifierLoader.getValuseForType(name, parent_id));
	out.write(str);
%>
