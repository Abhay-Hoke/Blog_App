<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>login</title>
<link rel="stylesheet" href="CSS/style.css">
</head>
<body>
<%@ include file="Header.jsp" %>
<form action="LoginServlet" method="post">
<label>Email:</label><input type="email" name="email" required><br>
<label>Password:</label><input type="password" name="password" required><br>
<input type="submit" value="Login"><br>
<a href="register.jsp">Register</a>

</form>
<footer>
    &copy; 2024 Your Blog Application
</footer>
</body>
</html>