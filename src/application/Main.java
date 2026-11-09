package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.LoginController;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        LoginController controller = new LoginController();

        Scene scene = new Scene(
            controller.createLoginScreen(),
            1200,
            720
        );

        scene.getStylesheets().add(
            getClass().getResource("/css/login.css").toExternalForm()
        );

        stage.setTitle(
            "Smart Emergency Resource and Blood Donor Management System"
        );

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}