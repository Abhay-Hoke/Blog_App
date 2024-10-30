<%@page import="java.sql.ResultSet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Viewer Home</title>
<link rel="stylesheet" href="CSS/style.css">
</head>
<body>

<%@ include file="Header.jsp" %>
<%
	HttpSession se = request.getSession(false);
	String role = (String)se.getAttribute("role");
	

%>
<h1>Welcome, <%= (se.getAttribute("name") != null) ? se.getAttribute("name") : "Viewer" %> </h1>


<form action="BlogServlet" method="get" class="search-form">
<input type="hidden" name="action" value="search">
<input type="text" name="query" placeholder="Search by title or date">
<select name="type">
<option name="title">Title</option>
<option name="date">Date</option>
</select>
<input type="submit" value="Search">
</form>

<%
ResultSet blogs = (ResultSet)request.getAttribute("blogs");

if(blogs !=null){
	while(blogs.next()){
		String title = blogs.getString("title");
		String date = blogs.getString("created_at");
		int blogId = blogs.getInt("id");
		%>
		<div class="blog-item">
		<h2><a href="BlogServlet?action=view&id=<%= blogId %>"><%=title %></a></h2>
		<p class="date"><strong>Date:</strong><%=date %></p>
		</div>
		<% 
	}
}
%>


<footer>
    &copy; 2024 Your Blog Application
</footer>

</body>
</html>