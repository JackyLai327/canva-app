package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.imageio.stream.FileImageInputStream;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import model.Model;
import model.User;

public class ProfileController {
    @FXML
    private Button cancel;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Button save;
    @FXML
    private Label username;

    private Model model;
    private Stage stage;
    private Stage parentStage;
    private User user;
    private File selectedFile;

    public ProfileController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        user = model.getCurrentUser();

        cancel.setOnAction(e -> {
            stage.close();
            parentStage.show();
            parentStage.centerOnScreen();
        });

        try {
            profilePicture.setImage(model.getUserDao().retrieveImage(user.getUsername()));
            username.setText(user.getUsername());
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profilePicture.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg");
            fileChooser.getExtensionFilters().add(extensionFilter);
            selectedFile = fileChooser.showOpenDialog(stage);
            InputStream fileInputStream;

            try {
                if (selectedFile == null) {
                    profilePicture.setImage(model.getUserDao().retrieveImage(user.getUsername()));
                } else {
                    fileInputStream = new FileInputStream(selectedFile);
                    profilePicture.setImage(new Image(fileInputStream));
                }
            } catch (IOException err) {
                err.printStackTrace();
            } catch (SQLException err1) {
                err1.printStackTrace();
            }
        });

        save.setOnAction(e -> {
            try {
                user = model.getUserDao().updateUser(user, firstName.getText(), lastName.getText());
                if (selectedFile != null) {
                    FileInputStream fis = new FileInputStream(selectedFile);
                    model.getUserDao().changeProfilePic(user, fis);
                }
                model.setCurrentUser(user);
            } catch (SQLException | IOException err) {
                err.printStackTrace();
                System.out.println(err.getMessage());
            }
            stage.close();
            parentStage.show();
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 380, 250);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Profile");
        stage.show();
    }
}
