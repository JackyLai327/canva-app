package model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.scene.image.Image;

public class User {
    
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Image photo;

    public User(String username, String password, String firstName, String lastName, Image photo) throws NoSuchAlgorithmException {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
    }

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // GETTERS

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Image getPhoto() {
        return this.photo;
    }

    // SETTERS

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    // METHODS 

    public void login() {
        // NOT DONE
        // LOGIC TO DISPLAY PHOTO, FIRST NAME, LAST NAME
    }

    public void editFirstName() {
        setFirstName(firstName);
        // NOT DONE
    }

    public void editLastName() {
        setLastName(lastName);
        // NOT DONE
    }

    public void editPhoto() {
        setPhoto(photo);
        // NOT DONE
    }

    public void createNewCanva() {
        // NOT DONE
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hashbytes = digest.digest(
                password.getBytes(StandardCharsets.UTF_8)
            );
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
}
