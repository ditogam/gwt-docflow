<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.isomorphic.taglib.LoadSystemSchemaTag"%>
<%@page import="java.util.List"%>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic"%>

<html>
<head>
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
	String skin = "EnterpriseBlue";
%>



<isomorphic:loadISC modulesDir="system/development/" skin="<%=skin%>"
	includeModules="DBConsole,FileLoader,DocViewer,FileBrowser,Drawing,Charts,Analytics" />


<script type="text/javascript">
	isc.nativeSkin = true;
</script>
</head>
<script type="text/javascript" language="javascript"
	src="workflow/workflow.nocache.js"></script>
<body>

	<script type="text/javascript">
		isc.nativeSkin = true;
		//isc.isVisualBuilderSDK = true;
		RPCManager.actionURL = Page.getAppDir() + "vbOperations.jsp";
		var isomorphicDir = "workflow/sc/";

		if (isc.Browser.isSafari) {
			isc.FileLoader.loadFile("referenceDocs.js",
					"isc.jsdoc.init(docItems)");
		} else {
			isc.xml.loadXML("referenceDocs.xml", "isc.jsdoc.init(xmlDoc)");
		}
	</script>


	<SCRIPT SRC=ds_generator.jsp></SCRIPT>

	<isomorphic:loadModules modulesDir="system/development/"
		modules="VisualBuilder,SalesForce,DocViewer,Tools" />
	<SCRIPT SRC=common.js></SCRIPT>
	<SCRIPT SRC=components.js></SCRIPT>
	<!-- history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>
</body>
</html>