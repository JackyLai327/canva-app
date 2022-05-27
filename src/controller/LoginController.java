package controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import model.Model;
import model.User;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.util.Callback;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label message;
    @FXML
    private Button signIn;
    @FXML 
    private Button close;
    @FXML
    private Hyperlink createUserLink;

    private Model model;
    private Stage stage;
    private Stage parentStage;

    public LoginController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.model = model;
        this.parentStage = parentStage;
    }

    @FXML
    public void initialize() {
        signIn.setOnAction(event -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                User user;
                try {
                    user = model.getUserDao().getUser(username.getText(), password.getText());
                    if (user != null) {
                        model.setCurrentUser(user);
                        message.setText("Login success for " + user.getFirstName() + user.getLastName());
                        message.setTextFill(Color.GREEN);
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/CanvasView.fxml"));

                            Callback<Class<?>, Object> controllerFactory = param -> {
                                return new CanvasController(stage, model);
                            };
                            
                            loader.setControllerFactory(controllerFactory);
                            AnchorPane root = loader.load();

                            CanvasController canvasController = loader.getController();
                            canvasController.showStage(root);

                            message.setText("");
                            username.clear();
                            password.clear();

                            stage.close();
                        } catch (IOException e) {
                            message.setText(e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        message.setText("Wrong username or password");
                        message.setTextFill(Color.RED);
                    }
                } catch (SQLException e) {
                    message.setText(e.getMessage());
                    message.setTextFill(Color.RED);
                    e.printStackTrace();
                } 
            } else {
                message.setTextFill(Color.RED);
                message.setText("Empty username or password");
            }
            username.clear();
            password.clear();
        });

        createUserLink.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/RegistrationView.fxml"));

                Callback<Class<?>, Object> controllerFactory = param -> {
					return new RegistrationController(stage, model);
				};
                
                loader.setControllerFactory(controllerFactory);
                AnchorPane root = loader.load();

                RegistrationController registrationController = loader.getController();
                registrationController.showStage(root);

                message.setText("");
                username.clear();
                password.clear();

                stage.close();
            } catch (IOException e) {
                message.setText(e.getMessage());
            }
        });

        close.setOnAction(event -> {
            stage.close();
            System.exit(0);
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showStage(Pane root) {
		Scene scene = new Scene(root, 451, 363);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Welcome");
		stage.show();
	}
}
