package com.blogapp.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import com.blogapp.utils.DbUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

//@WebServlet("/UpdateBlogServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB before written to disk
    maxFileSize = 1024 * 1024 * 10,       // 10MB max file size
    maxRequestSize = 1024 * 1024 * 50     // 50MB max request size
)
public class UpdateBlogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || !"Admin".equals(role)) {
            response.sendRedirect("login.jsp?error=Unauthorized");
            return;
        }

        Connection conn =null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        
        Integer blogId = Integer.parseInt(request.getParameter("id"));
        System.out.println(blogId);
        try {conn = DbUtils.connectDB();
        
        	String sql ="SELECT * FROM blogs WHERE id = ?";
        
        
             ps = conn.prepareStatement(sql);
             ps.setInt(1, blogId);
             rs = ps.executeQuery();
//             Integer blogId = Integer.parseInt(request.getParameter("id"));
//
//             System.out.println(blogId);
           

           
            if (rs.next()) {
                request.setAttribute("id", rs.getInt("id"));
                request.setAttribute("title", rs.getString("title"));
                request.setAttribute("content", rs.getString("content"));
                request.setAttribute("created_at", rs.getTimestamp("created_at"));

                byte[] imageData = rs.getBytes("image");
                if (imageData != null) {
                    String base64Image = Base64.getEncoder().encodeToString(imageData);
                    request.setAttribute("imageData", base64Image);
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher("update-blog.jsp");
                dispatcher.forward(request, response);
            } else {
                response.sendRedirect("admin-dashboard.jsp?error=BlogNotFound");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin-dashboard.jsp?error=DatabaseError");
        }finally {
			try {
				if(rs!=null) rs.close();
				if(ps!=null) ps.close();
				if(conn!=null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int blogId = Integer.parseInt(req.getParameter("id"));
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        Part imagePart = req.getPart("image");
        InputStream imageStream = null;
        Connection conn =null;
        PreparedStatement ps =null;
        
        if (imagePart != null && imagePart.getSize() > 0) {
            imageStream = imagePart.getInputStream();
        }

        try {
        		conn = DbUtils.connectDB();
        		String sql ="UPDATE blogs SET title = ?, content = ?, updated_at = CURRENT_TIMESTAMP" +
                        (imageStream != null ? ", image = ?" : "") +
                        " WHERE id = ?";
        		
        		ps = conn.prepareStatement(sql);
                 

            ps.setString(1, title);
            ps.setString(2, content);

            if (imageStream != null) {
                ps.setBlob(3, imageStream);
                ps.setInt(4, blogId);
            } else {
                ps.setInt(3, blogId);
            }

            ps.executeUpdate();
            resp.sendRedirect("AdminDashboardServlet?message=BlogUpdated");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("admin-dashboard.jsp?error=DatabaseError");
        }
        finally {
			try {
				
				if(ps!=null) ps.close();
				if(conn!=null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
