package dao;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.chart.PieChart.Data;
import javafx.scene.image.Image;
import model.User;

public class UserDaoImpl implements UserDao {
	private final String TABLE_NAME = "users";

	public UserDaoImpl() throws SQLException {
		setup();
	}

	@Override
	public void setup() throws SQLException {
		try (Connection connection = Database.getConnection();
				Statement stmt = connection.createStatement();) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (username VARCHAR(20) NOT NULL,"
					+ "password VARCHAR(20) NOT NULL," + "firstName VARCHAR(20) NOT NULL,"
					+ "lastName VARCHAR(20) NOT NULL," + "photo LONGBLOB NOT NULL," + "PRIMARY KEY (username))";
			stmt.executeUpdate(sql);
		}
	}

	@Override
	public User getUser(String username, String password) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {

			stmt.setString(1, username);
			stmt.setString(2, hashPassword(password));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					User user = new User(username, password);
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setFirstName(rs.getString("firstName"));
					user.setLastName(rs.getString("lastName"));
					user.setPhoto(retrieveImage(rs.getString("username")));
					return user;
				}
				return null;
			}
		}
	}

	@Override
	public boolean getUser(String username) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {

			stmt.setString(1, username);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	@Override
	public User createUser(String username, String password, String firstName, String lastName,
			FileInputStream photoFileInputStream) throws SQLException, NoSuchAlgorithmException, IOException {
		String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {

			stmt.setString(1, username);
			stmt.setString(2, hashPassword(password));
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			stmt.setBytes(5, photoFileInputStream.readAllBytes());
			stmt.executeUpdate();
			User user = new User(username, password, firstName, lastName);
			user.setPhoto(retrieveImage(username));
			return user;
		}
	}

	public String hashPassword(String password) {
		try {
			SecureRandom random = new SecureRandom();
			MessageDigest digest = MessageDigest.getInstance("SHA3-256");
			byte[] hashbytes = digest.digest(
					password.getBytes(StandardCharsets.UTF_8));
			StringBuilder stringBuilder = new StringBuilder();
			for (byte b : hashbytes) {
				stringBuilder.append(Integer.toString(b & 0xff + 0x100, 16).substring(1));
			}
			return stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}

	public void changeProfilePic(User user, FileInputStream photo) throws SQLException, IOException {
		String sql = "UPDATE " + TABLE_NAME + " SET photo = ? WHERE username = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setBytes(1, photo.readAllBytes());
			stmt.setString(2, user.getUsername());
			stmt.executeUpdate();
		}
	}

	public Image retrieveImage(String username) throws SQLException {
		Image image;
		String retrieveStatement = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(retrieveStatement);) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery();) {
				if (rs.next()) {
					byte[] bytes = rs.getBytes("photo");
					InputStream inputStream = new ByteArrayInputStream(bytes);
					image = new Image(inputStream);
					return image;
				} else {
					return null;
				}
			}
		}
	}

	public User updateUser(User user, String firstName, String lastName) throws SQLException {
		String sql = "UPDATE " + TABLE_NAME + " SET firstName = ?, lastName = ? WHERE username = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, firstName);
			stmt.setString(2, lastName);
			stmt.setString(3, user.getUsername());
			stmt.executeUpdate();
		}
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return user;
	}
}
