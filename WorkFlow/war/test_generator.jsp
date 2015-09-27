<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic"%>
<%@ taglib uri="/WEB-INF/wfTaglib.xml" prefix="workflow"%>
<%@ page import="com.isomorphic.base.Config"%>
<%@ page import="com.isomorphic.base.ISCInit"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Set"%>
<script type="text/javascript">
	var isomorphicDir = "workflow/sc/";
</script>
<%!private Config baseConfig;
	private boolean allowAnyRPC = false;
	private final Set enabledBuiltinMethods = new HashSet();
	private boolean useIDACall = false;

	private final boolean isBuiltinMethodEnabled(String methodName) {
		return (allowAnyRPC || enabledBuiltinMethods.contains(methodName));
	}%>

<%
	// Lets get the path of the current page and replace index.jsp with vbOperations.jsp
	final String vbOperationsPath = request.getRequestURI().replace(
			"/index.jsp", "/vbOperations.jsp");
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
<HTML>
<HEAD>
<TITLE>SmartClient Visual Builder</TITLE>
<LINK REL=StyleSheet HREF="visualBuilder.css" TYPE="text/css">
</HEAD>
<BODY STYLE="overflow: hidden">

	<%
		String skin = request.getParameter("skin");
		if (skin == null || "".equals(skin))
			skin = "Graphite";

		String nSkin = request.getParameter("useNativeSkin");
		boolean useNativeSkin = nSkin == null || "1".equals(nSkin);
	%>
	<!-- load Isomorphic SmartClient -->
	<isomorphic:loadISC modulesDir="system/development/" skin="<%=skin%>"
		includeModules="DBConsole,FileLoader,DocViewer,FileBrowser,Drawing,Charts,Analytics" />
	<SCRIPT>
isc.nativeSkin = <%=useNativeSkin%>;
if (!<%=useIDACall%>) {
    RPCManager.actionURL = Page.getAppDir() + "vbOperations.jsp";
}

isc.isVisualBuilderSDK = true;
//isc.Page.setAppImgDir("graphics/");
isc.Page.leaveScrollbarGap = false;

if (isc.Browser.isSafari) {
    isc.FileLoader.loadFile("referenceDocs.js", "isc.jsdoc.init(docItems)");
} else {
    isc.xml.loadXML("referenceDocs.xml", "isc.jsdoc.init(xmlDoc)");
}
// load datasource files

</SCRIPT>
	<SCRIPT SRC=ds_generator.jsp></SCRIPT>
	<!-- load Tools resources -->
	<isomorphic:loadModules modulesDir="system/development/"
		modules="Tools" />

	<!-- Additional ToolSkin to apply to Tools controls -->

	<%
		if (useNativeSkin) {
	%>
	<isomorphic:loadSkin skin="ToolSkinNative" />
	<%
		} else {
	%>
	<isomorphic:loadSkin skin="ToolSkin" />
	<%
		}
	%>
	<!-- load application logic -->
	<isomorphic:loadModules modulesDir="system/development/"
		modules="VisualBuilder,SalesForce" />
	<SCRIPT SRC=common.js></SCRIPT>

	<workflow:loadLanguages dir="ds" />
	<workflow:loadDataSources dir="ds" />
	<SCRIPT SRC=components.js></SCRIPT>

	<SCRIPT>
function removeSaved(_name){
	var _8 = isc.Offline.get(_name);
	if(_8)
	isc.Offline.remove(_name);
}
var useIDACall = <%=useIDACall%>;
removeSaved("VB_AUTOSAVE_PROJECT");
removeSaved("VB_SINGLE_SCREEN");
window.builder = isc.VisualBuilder.create({
    width: "100%",
    height: "100%",
    autoDraw: true,modulesDir:'modules/',

    saveFileBuiltinIsEnabled: !useIDACall || <%=isBuiltinMethodEnabled("saveFile")%>,
    loadFileBuiltinIsEnabled: !useIDACall || <%=isBuiltinMethodEnabled("loadFile")%>,
   /*filesystemDataSourceEnabled: !useIDACall || <%=baseConfig.getBoolean("FilesystemDataSource.enabled",
					false)%>,
*/
	filesystemDataSourceEnabled : false,
	skin: "Enterprise Blue",
    defaultApplicationMode: "edit1",
    showModeSwitcher: true,
    showScreenMenu:false,
    singleScreenMode:true,
    
    // provide an initial top-level VLayout that is appropriate for a fullscreen app:
    // take up whole browser, never overflow
    initialComponent: {
        type: "DataView",
        defaults: {
            autoDraw: true,modulesDir:'modules/',
            overflow: "hidden",
            width: "100%",
            height: "100%",
            // this is enough to make it obvious that a badly scrunched component
            // such as a ListGrid is actually a scrunched ListGrid and not just a
            // 1px black line (which happens with the default minMemberSize of 1)
            minMemberSize: 18
        }
    },
    finishInitWidget : function(){
    	this.Super("finishInitWidget", arguments);
    	/* addNewScreen(); */
    }
});

function addNewScreen(){
	
	var s=""+
	"isc.TreeGrid.create({\n"+
	"    ID:'TreeGrid0',\n"+
	"    autoDraw:false,\n"+
	"    fields:[\n"+
	"        {\n"+
	"            name:'TreeGridField0',\n"+
	"            title:'TreeGridField0'\n"+
	"        },\n"+
	"        {\n"+
	"            name:'TreeGridField1',\n"+
	"            title:'TreeGridField1'\n"+
	"        },\n"+
	"        {\n"+
	"            name:'TreeGridField2',\n"+
	"            title:'TreeGridField2'\n"+
	"        }\n"+
	"    ]\n"+
	"})\n"+
	"\n"+
	"\n"+
	"\n"+
	"isc.DataView.create({\n"+
	"    ID:'DataView1',\n"+
	"    autoDraw:true,\n"+
	"    height:'100%',\n"+
	"    overflow:'hidden',\n"+
	"    width:'100%',\n"+
	"    members:[\n"+
	"        TreeGrid0\n"+
	"    ],\n"+
	"    modulesDir:'modules/',\n"+
	"    minMemberSize:'18'\n"+
	"})";

	var _b=window.builder;
	var _screen=_b.project.addScreen(
			null,
			null,"TestScreen111",false);
	_b.setCurrentScreen(_screen);
	_b.projectComponents
			.destroyAll();
	_b.projectComponents
			.addPaletteNodesFromJS(s);
}

isc.ImgButton
.create({
	ID : "removeButton",
	autoDraw : false,
	src : "[SKIN]actions/remove.png",
	size : 16,
	showFocused : false,
	showRollOver : false,
	showDown : false,
	click : "addNewScreen();"
});
isc.VLayout.create({
	ID : "systemSelector",
	width : "100%",
	height : "100%",
	showEdges : true,
	members : [ isc.ContractForm.create({
		ID : "contacts_df"
		}), window.builder ]

});




<%if (request.getParameter("mockup") != null) {%>
var mockupParam = '<%out.write(request.getParameter("mockup"));%>
		';
	<%} else {%>
		var mockupParam = "";
	<%}%>
		if (mockupParam != "") {
			window.builder.loadBMMLMockup(mockupParam);
		}
	</SCRIPT>

</BODY>
</HTML>
