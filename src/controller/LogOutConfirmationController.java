package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Model;

public class LogOutConfirmationController {
    @FXML
    private Button logOut;
    @FXML
    private Button cancel;
    
    private Stage stage;
    private Stage parentStage;
    private Model model;

    public LogOutConfirmationController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        logOut.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/LoginView.fxml"));

                Callback<Class<?>, Object> controllerFactory = param -> {
					return new LoginController(stage, model);
				};
                
                loader.setControllerFactory(controllerFactory);
                AnchorPane root = loader.load();

                LoginController loginController = loader.getController();
                loginController.showStage(root);
                stage.close();

            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        cancel.setOnAction(e -> {
            stage.close();
            parentStage.show();
        });
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 330, 180);
        stage.setScene(scene);
        stage.setResizable(false);
		stage.setTitle("Log out?");
		stage.show();
    }
}
