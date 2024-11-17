<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Update Blog</title>
    <link rel="stylesheet" href="CSS/style.css">
</head>
<body>
<%@ include file="Header.jsp" %>

<h2>Update Blog</h2>

<form action="UpdateBlogServlet" method="post" enctype="multipart/form-data">
    <input type="hidden" name="id" value="<%= request.getAttribute("id") %>">

    <label>Title: </label>
    <input type="text" name="title" value="<%= request.getAttribute("title") %>" required><br>

    <label>Content: </label>
    <textarea name="content" required><%= request.getAttribute("content") %></textarea><br>

    <label>Current Image:</label><br>
    <% String imageData = (String) request.getAttribute("imageData"); %>
    <% if (imageData != null) { %>
        <img src="data:image/jpeg;base64,<%= imageData %>" alt="Blog Image" class="blog-image"><br>
    <% } else { %>
        <p>No Image Found</p>
    <% } %>

    <label>Upload New Image: </label>
    <input type="file" name="image"><br>

    <input type="submit" value="Update Blog">
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

<a href="DeleteBlogServlet?id=<%= request.getAttribute("id") %>" class="delete-button">Delete Blog</a>

</body>
</html>
