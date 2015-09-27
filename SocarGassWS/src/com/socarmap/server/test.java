package com.socarmap.server;

import no.tornado.brap.client.ServiceProxyFactory;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.socarmap.proxy.IConnection;
import com.socarmap.proxy.beans.UserContext;

public class test {
	public static void main(String[] args) throws Exception {
		System.out
				.println("public void metterstateCombo(FieldDefinitionItem current) {\n"
						+ "		String oldmaetterFields[] = new String[] { \"meterid\", \"moldvalue\",\n"
						+ "				\"mnewvalue\", \"expensem3\", \"inddaricx\" };\n"
						+ "		String oldmaetterNotReqFields[] = new String[] { \"moldvalue\",\n"
						+ "				\"mnewvalue\", \"expensem3\", \"inddaricx\" };\n"
						+ "		String newmaetterFields[] = new String[] { \"metserial\", \"mettertype\",\n"
						+ "				\"start_index\", \"cortypeid\", \"corserial\", \"montagedate\",\n"
						+ "				\"corvalue\" };\n"
						+ "		String newmaetterNotReqFields[] = new String[] { \"cortypeid\",\n"
						+ "				\"corserial\", \"corvalue\" };\n"
						+ "		double state = getDoubleValue(current);\n"
						+ "		setEditableAndRequered(oldmaetterFields, formitemMap,\n"
						+ "				(state == 1.0 || state == 3.0), oldmaetterNotReqFields);\n"
						+ "		setEditableAndRequered(newmaetterFields, formitemMap,\n"
						+ "				(state == 1.0 || state == 2.0), newmaetterNotReqFields);\n"
						+ "	}");
		ServiceProxyFactory.streamBufferSize = 1024;
		String url = "http://localhost:6767/SocarGassWS/";
		String full_url = url += "XSocarConnectorService";
		XRemotingProxyFactory factory = new XRemotingProxyFactory(full_url);
		IConnection conn = (IConnection) factory.create(IConnection.class);
		conn.ping();
		UserContext userContext = conn.loginUser("kvarelij", "romuli", 1, 1);
		System.out.println(userContext);
	}

	public static boolean isNaN(double paramDouble) {
		return (paramDouble != paramDouble);
	}

	public static boolean isInfinite(double paramDouble) {
		return ((paramDouble == (1.0D / 0.0D)) || (paramDouble == (-1.0D / 0.0D)));
	}
}
