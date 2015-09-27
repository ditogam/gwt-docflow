<%@page import="com.rdcommon.server.DSGenerator"%>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<!--                                                               -->
<!-- Consider inlining CSS to reduce the number of requested files -->
<!--                                                               -->
<link type="text/css" rel="stylesheet" href="RDesigner.css">

<!--                                           -->
<!-- Any title is fine                         -->
<!--                                           -->
<title>Web Application Starter Project</title>
<script type="text/javascript">
	var isomorphicDir = "rdesigner/sc/";
	var myfunctions = [];
	function testGwt(methodeName, invokerFieldName, values) {
		values['ddddd'] = false;
		myfunctions[methodeName](invokerFieldName, values);
	}

	function openwwindowandrender() {
		var myWindow = window.open("www.google.com", "mywindow", "location=1,status=1,scrollbars=1,width=100,height=100");
		
		myWindow.document.write("<p>This is 'myWindow'</p>");
		var newDiv = myWindow.document.createElement("kkk");
		var collection = myWindow.document.getElementsByTagName('body');
		collection.item(0).firstChild.appendChild(newDiv);
		return newDiv;
	}
</script>
<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<script type="text/javascript" language="javascript"
	src="rdesigner/rdesigner.nocache.js"></script>
<script type="text/javascript" language="javascript"
	src="rdesigner/sc/DataSourceLoader?dataSource=RegionDS,DSdefinitionsDS,DSdefinitionsSODS,ClassDefinitionsDS,JSDefinitionsDS,PropertyDefinitionDS"></script>
<script type="text/javascript" language="javascript"
	src="rdesigner/sc/DataSourceLoader?dataSource=<%out.write(DSGenerator.getDSDefiniString());%>"></script>

</head>
<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic UI.        -->
<!--                                           -->
<body>
</body>
</html>
