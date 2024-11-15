<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title></title>
<link rel="stylesheet" href="CSS/style.css">
</head>
<body>
<div>

<nav>
<h1>Welcome to the Blog Application</h1>


<a href="viewer-home.jsp">Home</a>|

<a href="viewer-home.jsp">view blogs</a>|
<%
HttpSession sess = request.getSession(false);
String role = (String) sess.getAttribute("role");
if(sess !=null && sess.getAttribute("id")!= null){
%>
<%if("Admin".equals(role)){ %>
<a href="admin-dashboard.jsp">Dash Board</a>|
<%} %>

<a href="LogoutServlet" >Logout</a>|
<%
} else { 
%>
<a href="login.jsp" class="login-btn">Login</a>|
<%
}
%>


</nav>
</div>
<footer>
    &copy; 2024 Your Blog Application
</footer>

</body>
</html>