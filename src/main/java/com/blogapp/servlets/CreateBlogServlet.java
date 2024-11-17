package com.blogapp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.blogapp.utils.DbUtils;

/**
 * Servlet implementation class CreateBlogServlet
 */


@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB before written to disk
	    maxFileSize = 1024 * 1024 * 10,       // 10MB max file size
	    maxRequestSize = 1024 * 1024 * 50     // 50MB max request size
	)
public class CreateBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		Part image = request.getPart("image");
		
//		System.out.println(title);
//		System.out.println(content);
		
		HttpSession session  = request.getSession(false);//false removed causing problems
		String role = (String)session.getAttribute("role");
//		System.out.println(role);
		
		PreparedStatement ps =null;
		Connection conn =null;

		
		
		if(session == null || !"Admin".equals(role)) {
			response.sendRedirect("login.jsp?error=Unauthorized");
			return;
		}
		
		 int adminId = (Integer) session.getAttribute("id");
//		 System.out.println(adminId);
	        if (adminId ==0) {
	            response.sendRedirect("login.jsp?error=SessionExpired");
	            return;
	        }
		
		
		
		//to convert image to binary i'm using inputstream class
		
		InputStream imageStream = null;
		if(image !=null && image.getSize()>0) {
			imageStream = image.getInputStream();
		}
		
		
		try {
			conn = DbUtils.connectDB();
			String sql = "insert into blogs(title,content,image,created_by,created_at) values(?,?,?,?,now())";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, title);
			ps.setString(2, content);
			
			if(imageStream != null) {
				ps.setBlob(3, imageStream);
			}else {
				ps.setNull(3, java.sql.Types.BLOB);
			}
			
			
			ps.setInt(4, adminId);
			
			ps.executeUpdate();
			
			
			
			response.sendRedirect("admin-dashboard.jsp?message=Blog post Added successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("create-blog.jsp?error=Someting went wrong");
		}finally {
			try {
				if(ps!=null )ps.close();
				if(conn!=null)conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		
		
		
	}
	
}
