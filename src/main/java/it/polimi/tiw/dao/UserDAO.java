package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.beans.User;

public class UserDAO {
    private Connection con;

    public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(int login, String pwd) throws SQLException {
		String query = "SELECT * FROM user WHERE login = ? AND password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, login);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setLogin(login);
					user.setRole(result.getString("role"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					
					return user;
				}
			}
		}
	}
}
