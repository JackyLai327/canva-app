package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Model;
import model.User;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class RegistrationController {
    @FXML
    private TextField username;
    @FXML
    private TextField lastName;
    @FXML 
    private TextField firstName;
    @FXML
    private PasswordField password;
    @FXML
    private Label message;
    @FXML
    private Button createUser;
    @FXML 
    private Button close;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Button clearImage;

    private Stage stage;
    private Stage parentStage;
    private Model model;
    File selectedFile = new File("src/images/user.png");

    public RegistrationController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        createUser.setOnAction(event -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty() && !firstName.getText().isEmpty() && !lastName.getText().isEmpty()) {
                User user;
                try {
                    FileInputStream photoFileInputStream = new FileInputStream(selectedFile);
                    user = model.getUserDao().createUser(username.getText(), password.getText(), firstName.getText(), lastName.getText(), photoFileInputStream); 
                        if (user != null) {
                            message.setText("Created an account for " + user.getFirstName() + " " + user.getLastName());
                            message.setTextFill(Color.GREEN);
                        } else {
                            message.setText("Cannot create user!");
                            message.setTextFill(Color.RED);
                        
                    }
                } catch (SQLException e) { 
                    message.setText(e.getMessage());
                    message.setTextFill(Color.RED);
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } catch (FileNotFoundException err) {
                    message.setText(err.getMessage());
                    message.setTextFill(Color.RED);
                } catch (IOException err) {
                    message.setText(err.getMessage());
                    message.setTextFill(Color.RED);
                } catch (NoSuchAlgorithmException err) {
                    message.setText(err.getMessage());
                    message.setTextFill(Color.RED);
                }
            } else {
                message.setTextFill(Color.RED);
                message.setText("All fields are required!");
            }
            username.clear();
            password.clear();
            lastName.clear();
            firstName.clear();
        });

        profilePicture.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
            fileChooser.getExtensionFilters().add(extensionFilter);
            selectedFile = fileChooser.showOpenDialog(stage);
            InputStream fileInputStream;

            try {
                fileInputStream = new FileInputStream(selectedFile);
                profilePicture.setImage(new Image(fileInputStream));
                clearImage.setDisable(false);
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        clearImage.setOnAction(e -> {
            try {
                selectedFile = new File("src/images/user.png");
                InputStream fis = new FileInputStream(selectedFile);
                profilePicture.setImage(new Image(fis));
                clearImage.setDisable(false);
            } catch (IOException err) {
                message.setText(err.getMessage());
            }
        });

        close.setOnAction(event -> {
            stage.close();
            parentStage.show();
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 430, 590);
        stage.setScene(scene);
        stage.setResizable(false);
		stage.setTitle("Sign up");
		stage.show();
    }
}
