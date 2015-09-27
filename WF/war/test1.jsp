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
<style type="text/css" media="screen">
body {
	overflow: hidden;
}

myeditortype {
	margin: 0;
	position: absolute;
	top: 0;
	bottom: 0;
	left: 0;
	right: 0;
}
</style>
<script src="main.js" />

<script type="text/javascript">
	window.isc_expirationOff = true;
	var isomorphicDir = "workflow/isomorphic/";
	//ExampleViewer,
</script>

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
<workflow:loadProjectJS dir="js/common" />
</head>

<body>


	<workflow:loadProjectJS dir="js/development" />

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
	<textarea id="aceeditor" style="width: 100%; height: 100%"></textarea>
	<script type="text/javascript">
		/* isc.defineClass("DevEditor", "Canvas");
		isc.DevEditor
				.addProperties({
					title : "Test",
					height : "100%",
					width : "100%",
					getInnerHTML : function() {
						return "<textarea id=\"aceeditor\"  style='width:100%;height:100%'></textarea>";
					}
				});
		var aLayout = isc.VLayout.create({
			width : "100%",
			height : "100%"
		});
		var ldev = isc.DevEditor.create();
		aLayout.addMember(ldev); */
		isc.Page.setEvent("load", function() {
			var container = document.getElementById("aceeditor");
			function passAndHint(cm) {
				setTimeout(function() {
					cm.execCommand("autocomplete");
				}, 100);
				return CodeMirror.Pass;
			}

			function myHint(cm) {
				return CodeMirror.showHint(cm, CodeMirror.ternHint, {
					async : true
				});
			}

			CodeMirror.commands.autocomplete = function(cm) {
				CodeMirror.showHint(cm, myHint);
			}
			var editor = CodeMirror.fromTextArea(container,
					{
						mode : 'application/javascript',
						theme : "eclipse",
						styleActiveLine : true,
						lineNumbers : true,
						lineWrapping : true,
						autoCloseBrackets : true,
						matchBrackets : true,
						extraKeys : {
							"'.'" : passAndHint,
							"Ctrl-Space" : "autocomplete",
							"Ctrl-I" : function(cm) {
								CodeMirror.tern.showType(cm);
							},
							"Alt-." : function(cm) {
								CodeMirror.tern.jumpToDef(cm);
							},
							"Alt-," : function(cm) {
								CodeMirror.tern.jumpBack(cm);
							},
							"Ctrl-Q" : function(cm) {
								CodeMirror.tern.rename(cm);
							}
						},
						gutters : [ "CodeMirror-lint-markers",
								"CodeMirror-linenumbers" ],
						lintWith : CodeMirror.javascriptValidator,
						textHover : {
							delay : 300
						}
					});
		});
	</script>



</body>
</html>