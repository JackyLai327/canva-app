package application;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import model.Model;
import javafx.scene.control.Label;
import model.User;

public class Main extends Application {
    private Model model;

    @Override
    public void init() throws Exception {
        model = new Model();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/LoginView.fxml"));
            
            Callback<Class<?>, Object> controllerFactory = param -> {
                return new LoginController(primaryStage, model);
            };

            loader.setControllerFactory(controllerFactory);

            AnchorPane root = loader.load();

            LoginController loginController = loader.getController();
            loginController.showStage(root);
            
        } catch (IOException | RuntimeException e) {
            Scene scene =  new Scene(new Label(e.getMessage()), 200, 100);
            primaryStage.setTitle("Error");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        }
        
    }

    @Override
    public void stop() throws Exception {

    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
