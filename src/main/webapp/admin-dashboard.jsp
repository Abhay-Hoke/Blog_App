<%@ page import="java.util.List" %>
<%@ include file="Header.jsp" %>

<div class="container">
    <h1>Admin Dashboard</h1>

    <div class="dashboard-links">
        <a href="create-blog.jsp">Create New Blog</a><br>
        <a href="register-new-admin.jsp">Add New Admin or Viewer</a><br>
        <a href="AdminDashboardServlet">Manage Bolg</a>
    </div>
    <%if(request.getAttribute("blogs")!=null){ %>
<h2>Manage Blogs</h2>

    <%
    List<List<Object>> blogsList = (List<List<Object>>) request.getAttribute("blogs");
    //System.out.println("Blogs list attribute: " + blogsList);
    
        if (blogsList != null && !blogsList.isEmpty()) {
            for (List<Object> blog : blogsList) {
                int blogId = (int) blog.get(0);              // Blog ID
                String title = (String) blog.get(1);          // Blog Title
                String createdAt = blog.get(2).toString();    // Created Date
    %>
                <div class="blog-item">
                    <h3><%= title %></h3>
                    <p><strong>Date:</strong> <%= createdAt %></p>
                    <a href="UpdateBlogServlet?id=<%= blogId %>" class="button">Edit</a>
                    
                    
                    <a href="DeleteBlogServlet?id=<%= blogId %>" class="button delete-button" onclick="return confirm('Are you sure you want to delete this blog?');">Delete</a>
                </div>
    <%
            }
        } else {
    %>
        <p>No blogs found.</p>
    <%
        }
    }
    %>
    
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

<footer>
    &copy; 2024 Your Blog Application
</footer>
