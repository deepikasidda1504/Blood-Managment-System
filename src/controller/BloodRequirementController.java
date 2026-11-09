
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BloodRequirementController {

    private static final String FILE =
        "data/requirements.txt";

    private TextField patientField;
    private TextField phoneField;
    private TextField unitsField;
    private TextField hospitalField;
    private TextField locationField;

    private ComboBox<String> bloodGroupBox;
    private ComboBox<String> urgencyBox;

    private DatePicker datePicker;

    private SceneHolder navigation;

    public interface SceneHolder {

        void showDonorMatching(String requestId);

        void goToDashboard();
    }

    public void setNavigation(SceneHolder navigation) {
        this.navigation = navigation;
    }

    // CREATE BLOOD REQUIREMENT SCREEN
    public Parent createRequirementScreen() {

        StackPane root = new StackPane();

        // MAROON BACKGROUND
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        Pane decorations = createDecorations();

        // MAIN WHITE CARD
        VBox card = new VBox(14);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 40, 30, 40));
        card.setMaxWidth(750);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: #F2A6B8;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 25, 0.2, 0, 10);"
        );

        // LOGO
        ImageView logo = createLogo();

        logo.setFitWidth(75);
        logo.setFitHeight(75);
        logo.setPreserveRatio(true);

        // HEADING
        Label heading = new Label(
            "Smart Emergency Resource and\nBlood Donor Management System"
        );

        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#700019"));
        heading.setAlignment(Pos.CENTER);
        heading.setWrapText(true);

        // HEART DIVIDER
        Label divider = new Label(
            "━━━━━━━━━━━━ ♥ ━━━━━━━━━━━━"
        );

        divider.setFont(Font.font(20));
        divider.setTextFill(Color.web("#B00035"));

        // TITLE
        Label title = new Label("BLOOD REQUIREMENT");

        title.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        title.setTextFill(Color.web("#A00030"));

        Label subtitle = new Label(
            "Enter patient and emergency blood requirement details"
        );

        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#777777"));

        // FORM FIELDS
        patientField = new TextField();
        patientField.setPromptText("Enter patient name");

        phoneField = new TextField();
        phoneField.setPromptText("Enter 10-digit mobile number");

        bloodGroupBox = new ComboBox<>();

        bloodGroupBox.getItems().addAll(
            "A+", "A-", "B+", "B-",
            "AB+", "AB-", "O+", "O-"
        );

        bloodGroupBox.setPromptText("Select blood group");

        unitsField = new TextField();
        unitsField.setPromptText("Enter number of units");

        hospitalField = new TextField();
        hospitalField.setPromptText("Enter hospital name");

        locationField = new TextField();
        locationField.setPromptText("Enter hospital location");

        datePicker = new DatePicker();
        datePicker.setPromptText("Select required date");
        datePicker.setValue(LocalDate.now());

        urgencyBox = new ComboBox<>();

        urgencyBox.getItems().addAll(
            "High", "Medium", "Low"
        );

        urgencyBox.setPromptText("Select urgency");

        // STYLE INPUTS
        styleInput(patientField);
        styleInput(phoneField);
        styleInput(unitsField);
        styleInput(hospitalField);
        styleInput(locationField);
        styleInput(bloodGroupBox);
        styleInput(datePicker);
        styleInput(urgencyBox);

        // FORM GRID
        GridPane form = new GridPane();

        form.setHgap(18);
        form.setVgap(14);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(620);

        addFormRow(form, 0, "Patient Name:", patientField);
        addFormRow(form, 1, "Phone Number:", phoneField);
        addFormRow(form, 2, "Blood Group:", bloodGroupBox);
        addFormRow(form, 3, "Required Units:", unitsField);
        addFormRow(form, 4, "Hospital Name:", hospitalField);
        addFormRow(form, 5, "Hospital Location:", locationField);
        addFormRow(form, 6, "Required Date:", datePicker);
        addFormRow(form, 7, "Urgency:", urgencyBox);

        // BUTTONS
        Button submitButton =
            createButton("SUBMIT REQUIREMENT", true);

        Button clearButton =
            createButton("CLEAR", false);

        Button backButton =
            createButton("BACK TO DASHBOARD", false);

        HBox buttonRow = new HBox(
            12,
            submitButton,
            clearButton
        );

        buttonRow.setAlignment(Pos.CENTER);

        // BUTTON ACTIONS
        submitButton.setOnAction(e -> submitRequirement());

        clearButton.setOnAction(e -> clearForm());

        backButton.setOnAction(e -> {

            if (navigation != null) {
                navigation.goToDashboard();
            }
        });

        card.getChildren().addAll(
            logo,
            heading,
            divider,
            title,
            subtitle,
            form,
            buttonRow,
            backButton
        );

        ScrollPane scroll = new ScrollPane(card);

        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);

        scroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;"
        );

        root.getChildren().addAll(
            decorations,
            scroll
        );

        return root;
    }

    // ADD FORM ROW
    private void addFormRow(
        GridPane grid,
        int row,
        String text,
        Node input
    ) {

        Label label = new Label(text);

        label.setFont(
            Font.font("Arial", FontWeight.BOLD, 13)
        );

        label.setTextFill(Color.web("#700019"));

        if (input instanceof Region) {
            ((Region) input).setMaxWidth(Double.MAX_VALUE);
        }

        grid.add(label, 0, row);
        grid.add(input, 1, row);

        GridPane.setHgrow(input, Priority.ALWAYS);
    }

    // SUBMIT REQUIREMENT
    private void submitRequirement() {

        String patient = patientField.getText().trim();
        String phone = phoneField.getText().trim();
        String units = unitsField.getText().trim();
        String hospital = hospitalField.getText().trim();
        String location = locationField.getText().trim();

        String blood = bloodGroupBox.getValue();
        String urgency = urgencyBox.getValue();

        LocalDate requiredDate = datePicker.getValue();

        // VALIDATION
        if (patient.isEmpty()
                || phone.isEmpty()
                || units.isEmpty()
                || hospital.isEmpty()
                || location.isEmpty()
                || blood == null
                || urgency == null
                || requiredDate == null) {

            showAlert(
                Alert.AlertType.WARNING,
                "Please fill in all the fields."
            );

            return;
        }

        if (!patient.matches("[a-zA-Z ]+")) {

            showAlert(
                Alert.AlertType.WARNING,
                "Please enter a valid patient name."
            );

            return;
        }

        if (!phone.matches("[0-9]{10}")) {

            showAlert(
                Alert.AlertType.WARNING,
                "Phone number must contain exactly 10 digits."
            );

            return;
        }

        if (!units.matches("[1-9][0-9]*")) {

            showAlert(
                Alert.AlertType.WARNING,
                "Please enter a valid number of units."
            );

            return;
        }

        if (requiredDate.isBefore(LocalDate.now())) {

            showAlert(
                Alert.AlertType.WARNING,
                "Required date cannot be in the past."
            );

            return;
        }

        if (containsSeparator(patient)
                || containsSeparator(hospital)
                || containsSeparator(location)) {

            showAlert(
                Alert.AlertType.WARNING,
                "The characters | are not allowed in text fields."
            );

            return;
        }

        // GENERATE REQUEST ID
        String requestId = generateRequestId();

        if (requestId == null) {

            showAlert(
                Alert.AlertType.ERROR,
                "Unable to generate Request ID."
            );

            return;
        }

        String formattedDate = requiredDate.format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );

        // SAVE REQUEST
        String record =
            requestId + "|" +
            patient + "|" +
            phone + "|" +
            blood + "|" +
            units + "|" +
            hospital + "|" +
            location + "|" +
            formattedDate + "|" +
            urgency + "|" +
            "Pending";

        try {

            File file = new File(FILE);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (BufferedWriter writer =
                     new BufferedWriter(
                         new FileWriter(file, true)
                     )) {

                writer.write(record);
                writer.newLine();
            }

            Alert alert = new Alert(
                Alert.AlertType.INFORMATION
            );

            alert.setTitle("Requirement Submitted");
            alert.setHeaderText("Blood Requirement Saved Successfully");

            alert.setContentText(
                "Your Request ID is: " + requestId +
                "\n\nOpening Donor Matching..."
            );

            alert.showAndWait();

            // OPEN DONOR MATCHING FOR THIS REQUEST
            if (navigation != null) {
                navigation.showDonorMatching(requestId);
            }

        } catch (IOException e) {

            e.printStackTrace();

            showAlert(
                Alert.AlertType.ERROR,
                "Unable to save blood requirement."
            );
        }
    }

    // GENERATE REQUEST ID
    private String generateRequestId() {

        File file = new File(FILE);

        int count = 0;

        if (file.exists()) {

            try (BufferedReader reader =
                     new BufferedReader(
                         new FileReader(file)
                     )) {

                while (reader.readLine() != null) {
                    count++;
                }

            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }
        }

        return String.format("REQ%03d", count + 1);
    }

    // CHECK SEPARATOR
    private boolean containsSeparator(String value) {

        return value.contains("|");
    }

    // CLEAR FORM
    private void clearForm() {

        patientField.clear();
        phoneField.clear();
        unitsField.clear();
        hospitalField.clear();
        locationField.clear();

        bloodGroupBox.setValue(null);
        urgencyBox.setValue(null);

        datePicker.setValue(LocalDate.now());
    }

    // STYLE INPUT FIELDS
    private void styleInput(Region input) {

        input.setPrefHeight(38);

        input.setStyle(
            "-fx-background-color: #FFF5F7;" +
            "-fx-border-color: #E7A0B1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 7;"
        );
    }

    // BUTTON DESIGN
    private Button createButton(
        String text,
        boolean primary
    ) {

        Button button = new Button(text);

        button.setPrefHeight(42);

        button.setFont(
            Font.font("Arial", FontWeight.BOLD, 12)
        );

        if (primary) {

            button.setStyle(
                "-fx-background-color: #A00030;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                "-fx-background-color: #F3E5E9;" +
                "-fx-text-fill: #700019;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E7A0B1;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-cursor: hand;"
            );
        }

        return button;
    }

    // LOGO
    private ImageView createLogo() {

        ImageView logo = new ImageView();

        try {

            Image image = new Image(
                getClass().getResourceAsStream("/images/logo.png")
            );

            logo.setImage(image);

        } catch (Exception e) {

            System.out.println("Logo image not found.");
        }

        return logo;
    }

    // BACKGROUND DECORATIONS
    private Pane createDecorations() {

        Pane pane = new Pane();

        pane.setMouseTransparent(true);

        Circle drop1 = new Circle(
            170, 170, 30,
            Color.web("#E00045", 0.30)
        );

        Circle drop2 = new Circle(
            1050, 470, 40,
            Color.web("#E00045", 0.25)
        );

        Polyline ecg1 = new Polyline(
            0, 340, 80, 340, 100, 320,
            120, 375, 145, 275, 170, 340, 280, 340
        );

        ecg1.setStroke(Color.web("#FF8DA8", 0.5));
        ecg1.setStrokeWidth(4);
        ecg1.setFill(Color.TRANSPARENT);

        Polyline ecg2 = new Polyline(
            900, 340, 980, 340, 1000, 320,
            1020, 375, 1045, 275, 1070, 340, 1200, 340
        );

        ecg2.setStroke(Color.web("#FF8DA8", 0.5));
        ecg2.setStrokeWidth(4);
        ecg2.setFill(Color.TRANSPARENT);

        pane.getChildren().addAll(
            drop1,
            drop2,
            ecg1,
            ecg2,
            createWave(650, "#8B0025"),
            createWave(675, "#C00040"),
            createWave(695, "#F2A6B8")
        );

        return pane;
    }

    // BOTTOM WAVES
    private javafx.scene.shape.Path createWave(
        double y,
        String color
    ) {

        javafx.scene.shape.Path path =
            new javafx.scene.shape.Path();

        path.getElements().addAll(
            new javafx.scene.shape.MoveTo(0, y),

            new javafx.scene.shape.CubicCurveTo(
                300, y - 80,
                450, y + 40,
                700, y - 20
            ),

            new javafx.scene.shape.CubicCurveTo(
                900, y - 70,
                1050, y + 30,
                1200, y - 10
            ),

            new javafx.scene.shape.LineTo(1200, 720),
            new javafx.scene.shape.LineTo(0, 720),
            new javafx.scene.shape.ClosePath()
        );

        path.setFill(Color.web(color, 0.85));

        return path;
    }

    // ALERT
    private void showAlert(
        Alert.AlertType type,
        String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle("Blood Requirement");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
