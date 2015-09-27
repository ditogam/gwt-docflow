package com.socarmap.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.socargass.tabletexporter.GetMap;
import com.socarmap.server.db.DBOperations;

/**
 * Servlet implementation class XYZRequest
 */
public class XYZRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XYZRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doProceed(request, response);
	}

	private void doProceed(HttpServletRequest request,
			HttpServletResponse response) {
		String zoom = request.getParameter("z");
		String x = request.getParameter("x");
		String y = request.getParameter("y");

		zoom = zoom == null ? "" : zoom.trim();
		x = x == null ? "" : x.trim();
		y = y == null ? "" : y.trim();

		if (zoom.isEmpty() || x.isEmpty() || y.isEmpty())
			return;
		byte[] bt = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select img_data from maps.zoom_xy where zoom=? and x=? and y=?";
			conn = DBOperations.getConnection(Constants.DBN_MAP);
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, Integer.valueOf(zoom));
			stmt.setInt(2, Integer.valueOf(x));
			stmt.setInt(3, Integer.valueOf(y));
			rs = stmt.executeQuery();
			if (rs.next()) {
				bt = rs.getBytes("img_data");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}
		// if (bt == null) {
		// try {
		// bt = GetMap.getBytes(zoom, x, y);
		// if (bt != null) {
		// try {
		// String sql = "select maps.savemapdata(?,?,?,?)";
		// conn = DBOperations.getConnection(Constants.DBN_MAP);
		// stmt = conn.prepareStatement(sql);
		// stmt.setInt(1, Integer.valueOf(zoom));
		// stmt.setInt(2, Integer.valueOf(x));
		// stmt.setInt(3, Integer.valueOf(y));
		// stmt.setBytes(4, bt);
		// rs = stmt.executeQuery();
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// DBOperations.closeAll(rs, stmt, conn);
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		if (bt != null) {
			try {
				GetMap.writeImage(bt, response.getOutputStream());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doProceed(request, response);
	}

	public static void main(String[] args) {
		String sql = "select ppcityid,pcityid, AsBinary(centroid(geom)) centroid,  AsBinary(GeomFromText('POINT('||MbrMinX(geom)||' ' ||MbrMinY(geom)||')',4326)) tl\n"
				+ ",  AsBinary(GeomFromText('POINT('||MbrMaxX(geom)||' ' ||MbrMaxY(geom)||')',4326)) br\n"
				+ "from\n"
				+ "(select ppcityid,pcityid,Envelope(GeomFromText(geomtext,4326)) geom from region_bounds rb\n"
				+ "inner join users u on (u.ppcityid=-1 or (rb.vtype=1 and id=u.ppcityid)) and (u.pcityid=-1 or (rb.vtype=2 and id=u.pcityid))\n"
				+ "where u.userid=?) k";
		System.out.println(sql);
	}

}
