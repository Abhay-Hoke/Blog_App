package com.blogapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
	final static String DB_USER ="abhay";
	final static String DB_PASSWORD ="student";
	final static String DB_NAME ="practice_blogs";
	final static String DB_URL = "jdbc:mysql://localhost:3306/"+DB_NAME;
	final static String DRIVER_CLASS ="com.mysql.cj.jdbc.Driver";
	
	//static Connection conn ;
	
	public static Connection connectDB() throws ClassNotFoundException, SQLException {
		
		//if(conn == null ) {
			Class.forName(DRIVER_CLASS);
			return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			
		//}
			
			//return conn;
		
			
		
	}

}
