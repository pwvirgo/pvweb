<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Budget</title>
		<link rel="stylesheet" type="text/css" href="../css/simple.css">
	</head>
	<body onkeypress="return checkSubmit(event)">
		<h1>This is the budget application</h1>
		<h2>Import transaction data</h2>
		<form action="/pvweb/budget/BudgetServlet" method="post"
				enctype="multipart/form-data" onsubmit="return genHTML()">
			<fieldset><legend>Select a CSV file to process</legend>
				<label for="csvfile">File of CSV data to be processed</label>
				<input type="file" id="csvfile" name="csvfile">
			</fieldset>
			<br>
			<div id="errorMsg"></div>
			<br>
			<fieldset>
				<legend>Options</legend>
				<label for="delim">Single character Delimiter (use "\t" for a tab) : </label> 
				<input type="text" name="delim" id="delim" size="2" value=",">
				<br><br>
				<label>Where to process your request? </label>
				<label for="local">Locally</label>
				<input type="radio" name="runWhere" id="local" value="local">
				<label for="srvr">Server</label>
				<input type="radio" name="runWhere" id="srvr" value="srvr" checked>
				<br><br>column headers?
			</fieldset>
			<br>
			<button type="submit" onkeypress="return checkSubmit(event)">Submit</button>
		</form>
		
		<script>			
			// submit form if enter is pressed
			function checkSubmit(e) {
				if (e && e.keyCode == 13 && 
					document.getElementById('srvr').checked) {
					{ document.forms[0].submit();}}
			}
			
		</script>

	 </body>
</html>
