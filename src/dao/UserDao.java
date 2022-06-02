package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;

import javafx.scene.image.Image;
import model.User;

public interface UserDao {
    void setup() throws SQLException;

    User getUser(String username, String password) throws SQLException;

    User createUser(String username, String password, String firstName, String lastName, FileInputStream photo)
            throws SQLException, NoSuchAlgorithmException, IOException;

    void changeProfilePic(User user, FileInputStream photo) throws SQLException, IOException;

    Image retrieveImage(String username) throws SQLException, IOException;

    String hashPassword(String password);

    User updateUser(User user, String firstName, String lastName) throws SQLException;

    boolean getUser(String username) throws SQLException;
}
