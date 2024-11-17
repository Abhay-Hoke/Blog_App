<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>create blog</title>
<link rel="stylesheet" href="CSS/style.css">
</head>
<body>

<%
	HttpSession se = request.getSession(false);//false causing problems
	//String role = (String) se.getAttribute("role");
	//if(se == null || !"Admin".equals(role)){
		//response.sendRedirect("login.jsp?error=UnAuthorized");
		//return;
	//}
	if(se == null || !"Admin".equals((String) se.getAttribute("role"))){
		response.sendRedirect("login.jsp?error=UnAuthorized");
		return;
	}

%>
<%@ include file="Header.jsp" %>
<h2>Create New Blog Post</h2>
<form action="CreateBlogServlet" method="post" enctype="multipart/form-data">
	<label>Title: </label><input type="text" name="title" required><br>
	<label>Content: </label><textarea name="content" required></textarea><br>
	<label>Image: </label><input type="file" name="image"><br>
	<input type="submit" value="Create">
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

<!-- enctype="multipart/form-data" -->