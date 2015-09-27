package com.workflow.server.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectVBXML extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8058497127146583355L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer requestURL = req.getRequestURL();
		String visualbuilder_path = "workflow/isomorphic/tools/visualBuilder/";
		String path = new File(new URL(requestURL.toString()).getFile())
				.getName();

		String newPath = visualbuilder_path + path;

		resp.sendRedirect(newPath);
	}
}
