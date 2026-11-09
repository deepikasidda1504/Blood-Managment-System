
package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import service.AuthService;

public class LoginController {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;

    private AuthService authService = new AuthService();

    public Parent createLoginScreen() {

        StackPane root = new StackPane();

        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        // Background decorations
        Pane background = new Pane();
        background.setMouseTransparent(true);

        // Blood drops
        addBloodDrop(background, 250, 180, 1.0);
        addBloodDrop(background, 1080, 550, 1.3);

        // ECG heartbeat lines
        addECG(background, -10, 350);
        addECG(background, 850, 360);

        // Blood bag decoration
        addBloodBag(background, 1000, 65);

        // Bottom waves
        addWaves(background);

        // Main login card
        VBox card = new VBox(12);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 35, 25, 35));
        card.setMaxWidth(640);
        card.setMaxHeight(650);

        card.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: #F7C4D0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 25, 0, 0, 8);"
        );

        // Official logo
        ImageView logo = createLogo();

        // System title
        Label systemTitle = new Label(
            "Smart Emergency Resource and\nBlood Donor Management System"
        );

        systemTitle.setFont(
            Font.font("Arial", FontWeight.BOLD, 25)
        );

        systemTitle.setTextFill(Color.web("#780019"));
        systemTitle.setTextAlignment(TextAlignment.CENTER);
        systemTitle.setAlignment(Pos.CENTER);
        systemTitle.setWrapText(true);

        // Heart divider
        HBox divider = new HBox(15);
        divider.setAlignment(Pos.CENTER);

        Region line1 = new Region();
        line1.setPrefWidth(170);
        line1.setPrefHeight(2);
        line1.setStyle(
            "-fx-background-color: #E51B36;"
        );

        Label heart = new Label("♥");
        heart.setFont(Font.font("Arial", 28));
        heart.setTextFill(Color.web("#E51B36"));

        Region line2 = new Region();
        line2.setPrefWidth(170);
        line2.setPrefHeight(2);
        line2.setStyle(
            "-fx-background-color: #E51B36;"
        );

        divider.getChildren().addAll(
            line1, heart, line2
        );

        // Welcome heading
        Label welcome = new Label("Welcome Back");

        welcome.setFont(
            Font.font("Arial", FontWeight.BOLD, 34)
        );

        welcome.setTextFill(Color.web("#850019"));

        Label subtitle = new Label("Administrator Login");

        subtitle.setFont(Font.font("Arial", 21));
        subtitle.setTextFill(Color.web("#555555"));

        // Username field
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(52);
        usernameField.setStyle(fieldStyle());

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(52);
        passwordField.setStyle(fieldStyle());

        // Login button
        Button loginButton = new Button("➜    LOGIN");

        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(55);

        loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #C90020, #800000);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 19px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;"
        );

        // Clear button
        Button clearButton = new Button("⟳    CLEAR");

        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setPrefHeight(52);

        clearButton.setStyle(
            "-fx-background-color: #F1F1F1;" +
            "-fx-border-color: #BBBBBB;" +
            "-fx-border-radius: 13;" +
            "-fx-background-radius: 13;" +
            "-fx-text-fill: #333333;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        // Message label
        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.web("#B00020"));

        // Registration link
        Hyperlink registerLink = new Hyperlink(
            "Don't have an account? Register here"
        );

        registerLink.setStyle(
            "-fx-text-fill: #A00030;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;"
        );

        // Button actions
        loginButton.setOnAction(e -> handleLogin());

        clearButton.setOnAction(e -> {
            usernameField.clear();
            passwordField.clear();
            messageLabel.setText("");
        });

        registerLink.setOnAction(e -> openRegistration());

        // Add components
        card.getChildren().addAll(
            logo,
            systemTitle,
            divider,
            welcome,
            subtitle,
            usernameField,
            passwordField,
            loginButton,
            clearButton,
            messageLabel,
            registerLink
        );

        root.getChildren().addAll(
            background,
            card
        );

        return root;
    }

    // Load official logo
    private ImageView createLogo() {

        java.io.InputStream stream =
            getClass().getResourceAsStream("/images/logo.png");

        if (stream == null) {
            throw new IllegalStateException(
                "Logo not found. Check out/images/logo.png"
            );
        }

        Image image = new Image(stream);

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(110);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    // Input field styling
    private String fieldStyle() {

        return
            "-fx-background-color: white;" +
            "-fx-border-color: #CCCCCC;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 17px;";
    }

    // Blood drop decoration
    private void addBloodDrop(
            Pane pane, double x, double y, double scale) {

        SVGPath drop = new SVGPath();

        drop.setContent(
            "M 0 -55 C -20 -20 -48 12 -48 38 " +
            "C -48 70 -25 88 0 88 " +
            "C 25 88 48 70 48 38 " +
            "C 48 12 20 -20 0 -55 Z"
        );

        drop.setFill(Color.web("#E51B36"));
        drop.setOpacity(0.85);

        drop.setScaleX(scale);
        drop.setScaleY(scale);

        drop.setLayoutX(x);
        drop.setLayoutY(y);

        pane.getChildren().add(drop);
    }

    // ECG heartbeat decoration
    private void addECG(
            Pane pane, double x, double y) {

        Polyline ecg = new Polyline(
            0.0, 40.0,
            65.0, 40.0,
            90.0, 10.0,
            115.0, 75.0,
            145.0, -45.0,
            175.0, 90.0,
            205.0, 25.0,
            225.0, 40.0,
            290.0, 40.0,
            320.0, 40.0,
            350.0, 5.0,
            375.0, 75.0,
            405.0, 25.0,
            430.0, 40.0
        );

        ecg.setStroke(Color.web("#FF3150"));
        ecg.setStrokeWidth(4);
        ecg.setFill(Color.TRANSPARENT);
        ecg.setOpacity(0.9);

        ecg.setLayoutX(x);
        ecg.setLayoutY(y);

        pane.getChildren().add(ecg);
    }

    // Blood bag decoration
    private void addBloodBag(
            Pane pane, double x, double y) {

        VBox bag = new VBox(3);

        bag.setAlignment(Pos.CENTER);
        bag.setPrefSize(95, 140);

        bag.setStyle(
            "-fx-background-color: rgba(255,180,195,0.20);" +
            "-fx-border-color: #FF9EAF;" +
            "-fx-border-width: 5;" +
            "-fx-background-radius: 18;" +
            "-fx-border-radius: 18;"
        );

        Label plus = new Label("+");

        plus.setFont(
            Font.font("Arial", FontWeight.BOLD, 55)
        );

        plus.setTextFill(Color.web("#FF9EAF"));

        bag.getChildren().add(plus);

        bag.setLayoutX(x);
        bag.setLayoutY(y);

        pane.getChildren().add(bag);

        Label heart = new Label("♡");

        heart.setFont(Font.font("Arial", 105));
        heart.setTextFill(Color.web("#FF9EAF"));

        heart.setLayoutX(x + 110);
        heart.setLayoutY(y + 60);

        pane.getChildren().add(heart);
    }

    // Bottom wave decorations
    private void addWaves(Pane pane) {

        SVGPath wave1 = new SVGPath();

        wave1.setContent(
            "M 0 620 C 250 700 350 590 650 650 " +
            "C 900 720 1100 630 1650 570 " +
            "L 1650 850 L 0 850 Z"
        );

        wave1.setFill(Color.web("#A00025"));

        SVGPath wave2 = new SVGPath();

        wave2.setContent(
            "M 0 690 C 300 740 450 650 750 720 " +
            "C 1050 790 1300 670 1650 630 " +
            "L 1650 850 L 0 850 Z"
        );

        wave2.setFill(Color.web("#E00030"));

        SVGPath wave3 = new SVGPath();

        wave3.setContent(
            "M 0 760 C 250 730 500 820 800 790 " +
            "C 1100 760 1400 740 1650 700 " +
            "L 1650 850 L 0 850 Z"
        );

        wave3.setFill(Color.WHITE);

        pane.getChildren().addAll(
            wave1, wave2, wave3
        );
    }

    // Login validation
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {

            messageLabel.setText(
                "Please enter username and password."
            );

            return;
        }

        if (authService.validateLogin(username, password)) {

            Scene scene = usernameField.getScene();

            DashboardController dashboard =
                new DashboardController();

            scene.setRoot(
                dashboard.createDashboardScreen(scene)
            );

        } else {

            messageLabel.setText(
                "Invalid username or password. " +
                "Please register if you don't have an account."
            );
        }
    }

    // Registration navigation
    private void openRegistration() {

        DonorRegistrationController registration =
            new DonorRegistrationController();

        Scene scene = usernameField.getScene();

        registration.setBackAction(() -> {
            scene.setRoot(createLoginScreen());
        });

        scene.setRoot(
            registration.createRegistrationScreen()
        );
    }
}
