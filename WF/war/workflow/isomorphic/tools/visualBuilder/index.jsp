<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>
<%@ page import="com.isomorphic.base.Config" %>
<%@ page import="com.isomorphic.base.ISCInit" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>

<%!
    private Config baseConfig;
    private boolean allowAnyRPC = false;
    private final Set enabledBuiltinMethods = new HashSet();
    private boolean useIDACall = false;

    private final boolean isBuiltinMethodEnabled(String methodName) {
        return (allowAnyRPC || enabledBuiltinMethods.contains(methodName));
    }
%>

<%
    // Lets get the path of the current page and replace index.jsp with vbOperations.jsp
    final String vbOperationsPath = request.getRequestURI().replace("/index.jsp", "/vbOperations.jsp");
    // Now we can fetch the vbOperations.jsp resource, if null it's not there.
    final boolean vbOperationsDoesNotExist = application.getResource(vbOperationsPath) == null;

    ISCInit.go(getClass().getName());
    baseConfig = Config.getGlobal();

    enabledBuiltinMethods.addAll(baseConfig.getList("RPCManager.enabledBuiltinMethods"));
    allowAnyRPC = enabledBuiltinMethods.contains("*");

    // Use IDACall if the server is explicitly configured to use it or if vbOperations.jsp does not exist.
    useIDACall = baseConfig.getBoolean("VisualBuilder.useIDACall", false) || vbOperationsDoesNotExist;

    String nEnableMockupMode = request.getParameter("mockups");
    final boolean mockupMode = (nEnableMockupMode != null || "1".equals(nEnableMockupMode)); 

    final String title = (mockupMode ? "SmartMockups" : "SmartClient Visual Builder");
%>
<HTML>
<HEAD>
<TITLE><%=title%></TITLE>
<LINK REL=StyleSheet HREF="visualBuilder.css" TYPE="text/css">
</HEAD>
<BODY STYLE="overflow:hidden">

<%
String skin = request.getParameter("skin");
if (skin == null || "".equals(skin)) skin = "Graphite";

String nSkin = request.getParameter("useNativeSkin");
boolean useNativeSkin = nSkin == null || "1".equals(nSkin);
%>
<!-- load Isomorphic SmartClient -->
<isomorphic:loadISC modulesDir="system/development/" skin="<%=skin%>" includeModules="DBConsole,FileLoader,DocViewer,FileBrowser,Drawing,Charts,Analytics"/> 
<SCRIPT>
isc.nativeSkin = <%=useNativeSkin%>;
if (!<%= useIDACall %>) {
    RPCManager.actionURL = Page.getAppDir() + "vbOperations.jsp";
}

isc.isVisualBuilderSDK = true;
isc.Page.setAppImgDir("graphics/");
isc.Page.leaveScrollbarGap = false;

if (isc.Browser.isSafari) {
    isc.FileLoader.loadFile("referenceDocs.js", "isc.jsdoc.init(docItems)");
} else {
    isc.xml.loadXML("referenceDocs.xml", "isc.jsdoc.init(xmlDoc)");
}
// load datasource files
<isomorphic:loadSystemSchema/>
</SCRIPT>

<!-- load Tools resources -->
<isomorphic:loadModules modulesDir="system/development/" modules="Tools"/>

<!-- Additional ToolSkin to apply to Tools controls -->

<%if (useNativeSkin) {%>
<SCRIPT src=../../isomorphic/skins/ToolSkinNative/load_skin.js></SCRIPT>
<%} else {%>
<SCRIPT src=../../isomorphic/skins/ToolSkin/load_skin.js></SCRIPT>
<%}%>
<!-- load application logic -->
<isomorphic:loadModules modulesDir="system/development/" modules="VisualBuilder,SalesForce"/>

<SCRIPT>

var useIDACall = <%= useIDACall %>;
window.builder = isc.VisualBuilder.create({
    width: "100%",
    height: "100%",
    autoDraw: true,

    saveFileBuiltinIsEnabled: !useIDACall || <%= isBuiltinMethodEnabled("saveFile") %>,
    loadFileBuiltinIsEnabled: !useIDACall || <%= isBuiltinMethodEnabled("loadFile") %>,
    filesystemDataSourceEnabled: !useIDACall || <%= baseConfig.getBoolean("FilesystemDataSource.enabled", false) %>,

	skin: "<%=skin%>",
    defaultApplicationMode: "edit",
    showModeSwitcher: true,
    mockupMode: <%=mockupMode%>,
    
    // provide an initial top-level VLayout that is appropriate for a fullscreen app:
    // take up whole browser, never overflow
    initialComponent: {
        type: "DataView",
        defaults: {
            autoDraw: true,
            overflow: "hidden",
            width: "100%",
            height: "100%",
            // this is enough to make it obvious that a badly scrunched component
            // such as a ListGrid is actually a scrunched ListGrid and not just a
            // 1px black line (which happens with the default minMemberSize of 1)
            minMemberSize: 18
        }
    }
});

<% if (request.getParameter("mockup") != null) { %>
var mockupParam = '<% out.write(request.getParameter("mockup")); %>';
<% } else { %>
var mockupParam = "";
<% } %>

if (mockupParam != "") {
    window.builder.loadBMMLMockup(mockupParam);
}

</SCRIPT>

</BODY>
</HTML>
