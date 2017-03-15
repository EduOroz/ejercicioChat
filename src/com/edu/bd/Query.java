package com.edu.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Query {

	Connection con = null;
	
	public Query(Conexion conexion){
		this.con = conexion.getConnection();
	}
	
	public void newUser(String nick){
		try {
			String query = "insert into users (nick, online) values (?, 1);";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			//System.out.println(query);
			preparedStmt.setString(1, nick);
			preparedStmt.execute();
			System.out.println("Se ha dado de alta un nuevo usuario");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {} 
	
	}
	
	public boolean existsUser (String nick) {
		boolean exists = false;
		try {
			String query = "select count(*) from users where nick ='" +nick +"';";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()){
				if (rs.getInt(1)==1) {exists = true;};
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}
	
	public void setUserOnline(String nick){
		try {
			String query = "update users set online=1 where nick = ? ;";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString(1, nick);
			preparedStmt.executeUpdate();
			System.out.println("El usuario " +nick +" ha entrado al chat");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {} 
	
	}
	
	public void setUserOffline(String nick){
		try {
			String query = "update users set online=0 where nick = ? ;";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString(1, nick);
			preparedStmt.executeUpdate();
			System.out.println("El usuario " +nick +" ha dejado el chat");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {} 
	
	}
	
	public void showUsersOnline () {
		try {
			String query = "select nick from users where online=1;";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("Usuarios Online");
			System.out.println("---------------");
			while (rs.next()){
				System.out.println(rs.getString("nick"));
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {} 
	}
	
	
	
}
