<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<!-- Content here -->
		Hello,This is your commit
		<br>
		<b>Email:</b><%=request.getAttribute("email")%>
		<br>
		<b>Password:</b><%=request.getAttribute("password")%>
	</div>
</body>