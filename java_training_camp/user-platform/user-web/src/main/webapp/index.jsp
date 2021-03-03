<%@ page import="org.geektimes.projects.user.domain.User" %>
<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<% User u = (User) request.getAttribute("user"); %>
	<div class="container-lg">
		<!-- Content here -->
		Hello,This is your commit

		<br>
		<b>Email:</b><%=u.getEmail()%>
		<br>
		<b>PhoneNumber:</b><%=u.getPhoneNumber()%>
		<br>
		<b>Username:</b><%=u.getName()%>
		<br>
		<b>Password:</b><%=u.getPassword()%>
	</div>
</body>