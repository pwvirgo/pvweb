<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="rsltbean" type="org.pwv.tools.CsvBean" scope="session"/>
<!DOCTYPE html>
<html>
	 <head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
			<title>CSV as HTML </title>
			<!-- this page is CSV2HtmlResult.jsp -->
			<link rel="stylesheet" type="text/css" href="../css/simple.css">
	 </head>
	 <body>
			<h1>CSV shown as HTML</h1>
		 <jsp:getProperty name="rsltbean" property="htmlTable" />
	 </body>
</html>
