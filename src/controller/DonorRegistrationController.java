
package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import service.AuthService;

public class DonorRegistrationController {

    private TextField nameField;
    private TextField phoneField;
    private TextField usernameField;
    private TextField locationField;

    private PasswordField passwordField;

    private ComboBox<String> bloodGroupBox;
    private ComboBox<String> availabilityBox;

    private Label messageLabel;

    private Runnable backAction;

    private AuthService authService = new AuthService();

    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    public Parent createRegistrationScreen() {

        StackPane root = new StackPane();

        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        Pane decorations = new Pane();
        decorations.setMouseTransparent(true);

        addBloodDrop(decorations, 250, 180, 35);
        addBloodDrop(decorations, 1080, 550, 45);
        addECG(decorations, -10, 350);
        addECG(decorations, 850, 360);
        addBloodBag(decorations, 1000, 65);
        addWaves(decorations);

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(22, 40, 22, 40));
        card.setMaxWidth(640);

        card.setStyle(
            "-fx-background-color: #FFF5F8;" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: #F3A6C0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 20, 0, 0, 8);"
        );

        ImageView logo = createLogo(85);

        Label heading = new Label(
            "Smart Emergency Resource and\nBlood Donor Management System"
        );
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 19));
        heading.setTextFill(Color.web("#85002F"));
        heading.setAlignment(Pos.CENTER);
        heading.setWrapText(true);

        Label divider = new Label("──────────── ♥ ────────────");
        divider.setTextFill(Color.web("#B00040"));
        divider.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label title = new Label("DONOR REGISTRATION");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        title.setTextFill(Color.web("#85002F"));

        Label subtitle = new Label("Create your donor account");
        subtitle.setTextFill(Color.web("#7A3550"));
        subtitle.setFont(Font.font("Arial", 13));

        nameField = createTextField("Enter donor name");
        phoneField = createTextField("Enter 10-digit contact number");
        usernameField = createTextField("Create username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Create password");
        passwordField.setPrefHeight(38);
        passwordField.setStyle(inputStyle());

        locationField = createTextField("Enter your location");

        bloodGroupBox = new ComboBox<>();
        bloodGroupBox.getItems().addAll(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        );
        bloodGroupBox.setPromptText("Select blood group");
        bloodGroupBox.setMaxWidth(Double.MAX_VALUE);
        bloodGroupBox.setPrefHeight(38);
        styleComboBox(bloodGroupBox);

        availabilityBox = new ComboBox<>();
        availabilityBox.getItems().addAll(
            "Available", "Not Available"
        );
        availabilityBox.setPromptText("Select availability");
        availabilityBox.setMaxWidth(Double.MAX_VALUE);
        availabilityBox.setPrefHeight(38);
        styleComboBox(availabilityBox);

        Button registerButton = new Button("REGISTER");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(42);
        registerButton.setStyle(buttonStyle());

        Button clearButton = new Button("CLEAR");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setPrefHeight(40);
        clearButton.setStyle(
            "-fx-background-color: #E8DDE2;" +
            "-fx-text-fill: #65002A;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;"
        );

        Button backButton = new Button("BACK TO LOGIN");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setPrefHeight(40);
        backButton.setStyle(
            "-fx-background-color: #65002A;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;"
        );

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.web("#B00040"));

        registerButton.setOnAction(e -> registerDonor());
        clearButton.setOnAction(e -> clearFields());

        backButton.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            }
        });

        card.getChildren().addAll(
            logo,
            heading,
            divider,
            title,
            subtitle,

            createLabel("Donor Name"),
            nameField,

            createLabel("Contact Number"),
            phoneField,

            createLabel("Username"),
            usernameField,

            createLabel("Password"),
            passwordField,

            createLabel("Blood Group"),
            bloodGroupBox,

            createLabel("Location"),
            locationField,

            createLabel("Availability"),
            availabilityBox,

            registerButton,
            clearButton,
            backButton,
            messageLabel
        );

        ScrollPane scrollPane = new ScrollPane(card);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;"
        );
        scrollPane.setMaxWidth(700);
        scrollPane.setMaxHeight(680);

        root.getChildren().addAll(decorations, scrollPane);

        return root;
    }

    private void registerDonor() {

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String bloodGroup = bloodGroupBox.getValue();
        String location = locationField.getText().trim();
        String availability = availabilityBox.getValue();

        if (name.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty()
                || bloodGroup == null || location.isEmpty()
                || availability == null) {

            showMessage("Please fill in all the fields.");
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            showMessage("Enter a valid name.");
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            showMessage("Enter a valid 10-digit phone number.");
            return;
        }

        if (username.contains(",")) {
            showMessage("Username cannot contain a comma.");
            return;
        }

        if (password.length() < 6) {
            showMessage("Password must contain at least 6 characters.");
            return;
        }

        try {
            Files.createDirectories(Paths.get("data"));

            // Check whether username already exists
            if (!authService.registerUser(username, password)) {
                showMessage("Username already exists. Choose another.");
                return;
            }

            // Save donor details
            try (BufferedWriter writer =
                     new BufferedWriter(
                         new FileWriter("data/donors.txt", true))) {

                writer.write(
                    name + "|" +
                    phone + "|" +
                    bloodGroup + "|" +
                    location + "|" +
                    availability
                );

                writer.newLine();
            }

            messageLabel.setText(
                "Registration successful! You can now login."
            );
            messageLabel.setTextFill(Color.GREEN);

            clearFields();

        } catch (IOException e) {
            showMessage("Error saving donor details: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        usernameField.clear();
        passwordField.clear();
        locationField.clear();

        bloodGroupBox.setValue(null);
        availabilityBox.setValue(null);

        messageLabel.setText("");
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.web("#B00040"));
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        label.setTextFill(Color.web("#65002A"));
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setStyle(inputStyle());
        return field;
    }

    private ImageView createLogo(double size) {
        try {
            Image image = new Image(
                getClass().getResourceAsStream("/images/logo.png")
            );

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);

            return imageView;

        } catch (Exception e) {
            return new ImageView();
        }
    }

    private String inputStyle() {
        return
            "-fx-background-color: white;" +
            "-fx-border-color: #E99AB5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 9;" +
            "-fx-font-size: 13px;";
    }

    private void styleComboBox(ComboBox<String> box) {
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E99AB5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-font-size: 13px;"
        );
    }

    private String buttonStyle() {
        return
            "-fx-background-color: linear-gradient(to right, #A00040, #E64A78);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;";
    }

    // Decorative background elements

    private void addBloodDrop(Pane pane, double x, double y, double size) {
        Path drop = new Path();

        drop.getElements().addAll(
            new MoveTo(x, y - size),
            new CubicCurveTo(
                x - size * 0.5, y - size * 0.2,
                x - size * 0.8, y + size * 0.4,
                x, y + size
            ),
            new CubicCurveTo(
                x + size * 0.8, y + size * 0.4,
                x + size * 0.5, y - size * 0.2,
                x, y - size
            )
        );

        drop.setFill(Color.web("#E64A78", 0.25));
        drop.setStroke(Color.web("#FF8EAC", 0.3));
        pane.getChildren().add(drop);
    }

    private void addECG(Pane pane, double x, double y) {
        Polyline ecg = new Polyline(
            x, y,
            x + 35, y,
            x + 55, y - 18,
            x + 70, y + 30,
            x + 90, y - 55,
            x + 110, y + 5,
            x + 140, y,
            x + 180, y
        );

        ecg.setStroke(Color.web("#FF8EAC", 0.45));
        ecg.setStrokeWidth(3);
        ecg.setFill(Color.TRANSPARENT);

        pane.getChildren().add(ecg);
    }

    private void addBloodBag(Pane pane, double x, double y) {
        Rectangle bag = new Rectangle(x, y, 55, 85);
        bag.setArcWidth(15);
        bag.setArcHeight(15);
        bag.setFill(Color.web("#FFF5F8", 0.16));
        bag.setStroke(Color.web("#FF8EAC", 0.45));
        bag.setStrokeWidth(2);

        Line top = new Line(x + 18, y, x + 37, y - 12);
        top.setStroke(Color.web("#FF8EAC", 0.5));
        top.setStrokeWidth(3);

        Rectangle blood = new Rectangle(x + 5, y + 48, 45, 30);
        blood.setArcWidth(8);
        blood.setArcHeight(8);
        blood.setFill(Color.web("#E64A78", 0.35));

        pane.getChildren().addAll(bag, top, blood);
    }

    private void addWaves(Pane pane) {
        Path wave1 = new Path();

        wave1.getElements().addAll(
            new MoveTo(0, 650),
            new CubicCurveTo(350, 570, 650, 760, 1000, 650),
            new CubicCurveTo(1250, 570, 1450, 650, 1650, 600),
            new LineTo(1650, 850),
            new LineTo(0, 850),
            new ClosePath()
        );

        wave1.setFill(Color.web("#A00040", 0.45));

        Path wave2 = new Path();

        wave2.getElements().addAll(
            new MoveTo(0, 700),
            new CubicCurveTo(400, 610, 700, 800, 1100, 690),
            new CubicCurveTo(1300, 640, 1450, 710, 1650, 670),
            new LineTo(1650, 850),
            new LineTo(0, 850),
            new ClosePath()
        );

        wave2.setFill(Color.web("#E64A78", 0.30));

        pane.getChildren().addAll(wave1, wave2);
    }
}
