package com.blogapp.servlets;

import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.mindrot.bcrypt.BCrypt;

//import org.mindrot.jbcrypt.BCrypt;

import com.blogapp.utils.DbUtils;


public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

//    /**
//     * Default constructor. 
//     */
//    public RegisterServlet() {
//        // TODO Auto-generated constructor stub
//    }
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		
//		response.getWriter().append("Served at: ").append(request.getContextPath());
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name  =request.getParameter("name");
		String email = request.getParameter("email");
		String password =request.getParameter("password");
		String role =request.getParameter("role");
		
		String hashedPassword =BCrypt.hashpw(password, BCrypt.gensalt()); 
		
		HttpSession session = request.getSession();
		String currentUserRole = (session!=null) ? (String)session.getAttribute("role") : null;
		
		boolean isAdmin = currentUserRole!=null && "Admin".equals(currentUserRole);
		
		
		if(!isAdmin) {
			role = "Viewer";
		}
		Connection conn =null;
		PreparedStatement ps=null;
		ResultSet rs =null;
		
		try {
			conn = DbUtils.connectDB();
			String sql ="insert into users(name,email,password,role) values(?,?,?,?)";
			//ps = conn.prepareStatement(sql);
			
			ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, name);
			ps.setString(2, email);
			ps.setString(3, hashedPassword);
			ps.setString(4, role);
			
			ps.executeUpdate();
//			ps.close();
//			conn.close();
			
			rs = ps.getGeneratedKeys();
			
			if(rs.next()) {
				int generatedId = rs.getInt(1);
				session.setAttribute("id", generatedId);
			}
			
			if(isAdmin) {
				response.sendRedirect("admin-dashboard.jsp?meassge=New user registerd successfully");
				
			}else {
				response.sendRedirect("login.jsp?message=Registration successful, please log in");
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			response.sendRedirect("register.jsp?error=Something went wrong");
			return;
		}finally {
			 try {
	                if(rs != null) rs.close();
	                if (ps != null) ps.close();
	                if (conn != null) conn.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
		}
		
		
		
	
	}

}
