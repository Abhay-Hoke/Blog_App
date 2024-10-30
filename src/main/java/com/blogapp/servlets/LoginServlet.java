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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.bcrypt.BCrypt;

//import org.mindrot.bcrypt.BCrypt;

//import org.mindrot.jbcrypt.BCrypt;

import com.blogapp.utils.DbUtils;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		PreparedStatement ps =null;
		Connection conn =null;
		ResultSet rs =null;
		
		try {
			conn = DbUtils.connectDB();
			String sql = "Select * from users where email = ?";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, email);
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				String hashedPassword = rs.getString("password");
				String role = rs.getString("role");
				
				if(BCrypt.checkpw(password, hashedPassword)) {
					
					HttpSession session = request.getSession();
					session.setAttribute("name", rs.getString("name"));
					session.setAttribute("role", role);
					session.setAttribute("email", email);
					session.setAttribute("id", rs.getInt("id"));
					
					if("Admin".equals(role)) {
						response.sendRedirect("admin-dashboard.jsp");
					}else {
						response.sendRedirect("viewer-home.jsp");
					}
				}else {
					response.sendRedirect("login.jsp?error=Invalid Password");
				}
			}else {
				response.sendRedirect("login.jsp?error= User not found");
			}
			
			
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			 response.sendRedirect("login.jsp?error=Something went wrong");
			 
			
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
		
		//doGet(request, response);
	}

}
