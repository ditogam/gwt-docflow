<%@page import="java.util.Set"%>
<%@page import="com.isomorphic.base.ISCInit"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.isomorphic.base.Config"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic"%>
<%@ taglib uri="/WEB-INF/wfTaglib.xml" prefix="workflow"%>

<%!private Config baseConfig;
	private String visualbuilder_path = "workflow/isomorphic/tools/visualBuilder/";
	private String visualbuilder_path_str = "'" + visualbuilder_path + "'";
	private boolean allowAnyRPC = false;
	private final Set enabledBuiltinMethods = new HashSet();
	private boolean useIDACall = false;

	private final boolean isBuiltinMethodEnabled(String methodName) {
		return (allowAnyRPC || enabledBuiltinMethods.contains(methodName));
	}%>

<%
	// Lets get the path of the current page and replace index.jsp with vbOperations.jsp
	final String vbOperationsPath = request.getRequestURI().replace(
			"/index.jsp", visualbuilder_path + "vbOperations.jsp");
	// Now we can fetch the vbOperations.jsp resource, if null it's not there.
	final boolean vbOperationsDoesNotExist = application
			.getResource(vbOperationsPath) == null;

	ISCInit.go(getClass().getName());
	baseConfig = Config.getGlobal();

	enabledBuiltinMethods.addAll(baseConfig
			.getList("RPCManager.enabledBuiltinMethods"));
	allowAnyRPC = enabledBuiltinMethods.contains("*");

	// Use IDACall if the server is explicitly configured to use it or if vbOperations.jsp does not exist.
	useIDACall = baseConfig.getBoolean("VisualBuilder.useIDACall",
			false) || vbOperationsDoesNotExist;
%>

<html>
<head>
<script type="text/javascript">
	window.isc_expirationOff = true;
	var isomorphicDir = "workflow/isomorphic/";
	//ExampleViewer,
</script>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<!--                                                               -->
<!-- Consider inlining CSS to reduce the number of requested files -->
<!--                                                               -->
<link type="text/css" rel="stylesheet" href="Workflow.css">

<!--                                           -->
<!-- Any title is fine                         -->
<!--                                           -->
<title>workflow</title>
<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->

<%
	String skin = "Enterprise";
%>

<script src="main.js" />



<isomorphic:loadISC skin="<%=skin%>" modulesDir="system/development/"
	includeModules="ExampleViewer,DBConsole,FileLoader,DocViewer,FileBrowser,Drawing,Charts,Analytics" />
<isomorphic:loadModules modulesDir="system/development/" modules="Tools" />
<script type="text/javascript">
	isc.Page.setAppImgDir(isc.Page.getIsomorphicDocsDir() + "images/");
</script>
<isomorphic:loadModules modulesDir="system/modules-debug/" />
<isomorphic:loadModules modulesDir="system/development/"
	modules="SyntaxHiliter" />
<script type="text/javascript">
	isc.nativeSkin = true;
</script>
<workflow:loadDataSources dir="ds" prefix="isomorphic/" />
<workflow:loadLanguages dir="ds" />
<workflow:loadProjectJS dir="js" />
</head>

<body>

	<SCRIPT>
		updateLanguageSession();
		var jsvisualbuilder_path =
	<%=visualbuilder_path_str%>
		;
	<%String nSkin = request.getParameter("useNativeSkin");
			boolean useNativeSkin = nSkin == null || "1".equals(nSkin);%>
		isc.nativeSkin =
	<%=useNativeSkin%>
		if (!
	<%=useIDACall%>
		) {
			RPCManager.actionURL =
	<%=visualbuilder_path%>
		+ "vbOperations.jsp";
		}

		isc.isVisualBuilderSDK = true;
		isc.Page.setAppImgDir("images/");
		isc.Page.leaveScrollbarGap = false;

		if (isc.Browser.isSafari) {
			isc.FileLoader.loadFile(jsvisualbuilder_path + "referenceDocs.js",
					"isc.jsdoc.init(docItems)");
		} else {
			isc.xml.loadXML(jsvisualbuilder_path + "referenceDocs.xml",
					"isc.jsdoc.init(xmlDoc)");
		}
		// load datasource files
	</SCRIPT>
	<isomorphic:loadModules modulesDir="system/development/"
		modules="VisualBuilder,SalesForce" />

	<script type="text/javascript">
		loginWF();
	</script>


</body>
</html>