package com.blogapp.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.blogapp.utils.DbUtils;


public class BlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
		String action = request.getParameter("action");
		//System.out.println(action);
		
		Connection conn =null;
		PreparedStatement ps=null;
		ResultSet rs =null;
		
		if("search".equals(action)) {
			
			String searchQuery = request.getParameter("query");
			//System.out.println(searchQuery);
			
			
			
			
			String searchType = request.getParameter("type");
		//	System.out.println(searchType);
			
			
			//if date input is null change the type to title
			if("Date".equals(searchType) && searchQuery=="") searchType="Title";
			//System.out.println(searchType);
			
			try {
				conn = DbUtils.connectDB();
				
				String sql =null;
				if("Title".equals(searchType)) {
					sql = "select * from blogs where title like ? order by id desc";	
				}else if("Date".equals(searchType)) {
					
					sql = "SELECT * FROM blogs WHERE DATE(created_at) =? order by id desc";
					
				}
				
				if(sql!=null) {
				
				ps = conn.prepareStatement(sql);
				
				  if ("Date".equalsIgnoreCase(searchType)) {
					  
				        ps.setString(1, searchQuery);// For Date search
					 
						  
				    } else if ("Title".equalsIgnoreCase(searchType)) {
				        ps.setString(1, "%" + searchQuery + "%"); // For Title 
				    }
				
				//ps.setString(1,"%"+searchQuery+"%");
				rs = ps.executeQuery();
				
				request.setAttribute("blogs", rs);
				
				RequestDispatcher dispath = request.getRequestDispatcher("viewer-home.jsp");
				dispath.forward(request, response);
				
//				ps.close();
				}
//				conn.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(ps!=null )
					ps.close();
					if(conn!=null)conn.close();
					if (rs != null) rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
			}
		}else if("view".equals(action) ) {// i created this for view blog page
			
			int blogId = Integer.parseInt(request.getParameter("id"));
			//System.out.println(blogId);
			
			try {
				conn = DbUtils.connectDB();
				 String sql = "SELECT title, content, created_at, image FROM blogs WHERE id = ?";
//				String sql ="select title, content, created_at from blogs where id =?";
				ps = conn.prepareStatement(sql);
				
				ps.setInt(1,blogId);
				
				rs = ps.executeQuery();
				//System.out.println(rs.next());
				
				byte[] imageData = null;
				 

				
				
				if (rs.next()) {
	                request.setAttribute("title", rs.getString("title"));
	                request.setAttribute("content", rs.getString("content"));
	                request.setAttribute("created_at", rs.getString("created_at"));
	                InputStream inputStream = rs.getBinaryStream("image");
	                if (inputStream != null) {
					    imageData = inputStream.readAllBytes();
					    String base64Image = Base64.getEncoder().encodeToString(imageData);
					    request.setAttribute("imageData", base64Image);
					}
	                
	            } else {
	                request.setAttribute("error", "Blog not found");
	                
	            }
				
				
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("error", "An error occurred while retrieving the blog");
			}finally {
				try {
					if(ps!=null )
					ps.close();
					if(conn!=null)conn.close();
					if (rs != null) rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher("view-blog.jsp");
	        dispatcher.forward(request, response);

			
		}else {
			//this one for default viewing area like home page for unregistered viewer
			
			try {
				
				conn = DbUtils.connectDB();
				
				String sql = "select * from blogs";
				ps = conn.prepareStatement(sql);
				
				rs = ps.executeQuery();
				
				request.setAttribute("blogs", rs);
				RequestDispatcher dispatch = request.getRequestDispatcher("viewer-home.jsp");
				
				dispatch.forward(request, response);
				
//				ps.close();
//				conn.close();
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}finally {
				try {
					if(ps!=null )
					ps.close();
					if(conn!=null)conn.close();
					if (rs != null) rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
			}
			
		}
		
	}

	
	

}
