package com.docflow.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class MyIsomorphicInit extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2554324189518956086L;

	public MyIsomorphicInit() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);

		// ConfigLoader.server = "docflow.properties";

		try {

			// Config.initGlobalConfig();

		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}
	}

}
