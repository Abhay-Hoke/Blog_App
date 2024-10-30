<%@page import="java.sql.ResultSet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>view blog</title>
<link rel="stylesheet" href="CSS/style.css">
</head>
<body>

<%@ include file="Header.jsp" %>
<%
HttpSession se = request.getSession(false);
String role = (String) se.getAttribute("role");

if(se ==null){
	response.sendRedirect("login.jsp?error=Unathorixed");
	return;
}

String title = (String) request.getAttribute("title");
String content = (String) request.getAttribute("content");
String created_at = (String) request.getAttribute("created_at");

String error = (String) request.getAttribute("error");

%>
<div class="blog-card">
        <% if (error != null) { %>
            <p><%= error %></p>
        <% } else { %>
            <h1><%= title %></h1>
            <p class="date"><strong>Date:</strong> <%= created_at %></p>
            
            <% String imageData = (String) request.getAttribute("imageData"); %>
        <% if (imageData != null && !imageData.isEmpty()) { %>
            <img src="data:image/jpeg;base64,<%= imageData %>" alt="Blog Image" class="blog-image"/>
        <% } %>
            
            <div class="content"><%= content %></div>
            
        <% } %>
    </div>

<footer>
    &copy; 2024 Your Blog Application
</footer>
</body>
</html>