package src.com.socargass.tabletexporter;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

public class LoginUser {
	public static void login(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		String user_name = request.getParameter("user_name");
		String pwd = request.getParameter("pwd");
		Connection pg = DBConnection.getConnection("Gass");
		String sql = "select 1 from users where username='" + user_name
				+ "' and md5(pass)='" + pwd + "'";
		ResultSet rs = pg.createStatement().executeQuery(sql);
		String result = "Cannot find user with name=" + user_name + "!!!";

		if (rs.next()) {
			result = "1";
		}
		// out.print("ending building" + "<br>");
		rs.getStatement().close();
		rs.close();
		pg.close();
		out.write(result);
	}

	public static void main(String[] args) throws Exception {
		String pwd = "point";
		byte[] bytesOfMessage = pwd.getBytes("UTF-8");

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] thedigest = md.digest(bytesOfMessage);
		BigInteger bigInt = new BigInteger(1, thedigest);
		String hashtext = bigInt.toString(16);
		String password = hashtext;
		System.out.println(password);
	}
}
