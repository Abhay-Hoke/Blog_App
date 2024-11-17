<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>register</title>
<link rel="stylesheet" href="CSS/style.css">
</head>
<body>
<%@ include file="Header.jsp" %>
<form action="RegisterServlet" method="post">
	<label>Name:</label><input type="text" name="name" required><br>
	<label>Email:</label><input type="email" name="email" required><br>
	<label>Password:</label><input type="password" name="password" required><br>
	
	<input type="submit" value="Register">
 <%
    String message = request.getParameter("message");
    if (message != null) {
%>
    <div class="alert success">
        <%= message %>
    </div>
<%
    }
%>
</div>
</form>
<footer>
    &copy; 2024 Your Blog Application
</footer>
</body>
</html>