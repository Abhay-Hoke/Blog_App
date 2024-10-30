package com.blogapp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.blogapp.utils.DbUtils;

public class DeleteBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 HttpSession session = request.getSession(false);
	     String role = (String) session.getAttribute("role");
		
	     if (session == null || !"Admin".equals(role)) {
	            response.sendRedirect("login.jsp?error=Unauthorized");
	            return;
	        }
	     
	     int blogId = Integer.parseInt(request.getParameter("id"));
	     System.out.println(blogId);
	     Connection conn = null;
	     PreparedStatement ps =null;
	     
	     try {
			conn = DbUtils.connectDB();
			
			String sql = "delete from blogs where id=?";
			
			ps =conn.prepareStatement(sql);
			
			ps.setInt(1, blogId);
			ps.executeUpdate();
			
			response.sendRedirect("admin-dashboard.jsp?success=BlogDeleted");
	    	 
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("admin-dashboard.jsp?error=Database error");
		}finally {
			try {
				if(ps!=null)ps.close();
				if(conn!=null)conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	
	

}
