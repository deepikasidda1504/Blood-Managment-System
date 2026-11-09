
package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DonorMatchingController {

    private static final String DONOR_FILE = "data/donors.txt";
    private static final String REQUEST_FILE = "data/requirements.txt";

    private ComboBox<String> requestBox;
    private TextField locationField;

    private TableView<Donor> donorTable;
    private ObservableList<Donor> donorList;

    private Label requestDetailsLabel;
    private Label messageLabel;

    private Runnable backAction;

    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    // OPEN MATCHING SCREEN
    public Parent createMatchingScreen() {
        return createMatchingScreen(null);
    }

    // OPEN WITH SELECTED REQUEST ID
    public Parent createMatchingScreen(String selectedRequestId) {

        StackPane root = new StackPane();

        // ORIGINAL MAROON BACKGROUND
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        Pane decorations = createDecorations();

        // WHITE ROUNDED CARD
        VBox card = new VBox(14);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 30, 30, 30));
        card.setMaxWidth(1050);

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
        Label divider = new Label("━━━━━━━━━━ ♥ ━━━━━━━━━━");

        divider.setTextFill(Color.web("#B00035"));
        divider.setFont(Font.font(20));

        // TITLE
        Label title = new Label("DONOR MATCHING");

        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#A00030"));

        Label subtitle = new Label(
            "Find available donors for a blood requirement"
        );

        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#777777"));

        // REQUEST DROPDOWN
        requestBox = new ComboBox<>();

        requestBox.setPromptText("Select Request ID");
        requestBox.setPrefWidth(300);
        requestBox.setStyle(comboStyle());

        Button loadButton = createButton("LOAD REQUEST", true);

        HBox requestRow = new HBox(10, requestBox, loadButton);
        requestRow.setAlignment(Pos.CENTER);

        // REQUEST DETAILS
        requestDetailsLabel = new Label(
            "Select a request to view its details."
        );

        requestDetailsLabel.setWrapText(true);
        requestDetailsLabel.setTextFill(Color.web("#700019"));
        requestDetailsLabel.setFont(Font.font("Arial", 13));

        // LOCATION FILTER
        Label locationLabel = new Label("Donor Location:");

        locationLabel.setTextFill(Color.web("#700019"));
        locationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        locationField = new TextField();

        locationField.setPromptText("Enter location (optional)");
        locationField.setPrefWidth(200);
        locationField.setStyle(inputStyle());

        Button matchButton =
            createButton("FIND MATCHING DONORS", true);

        Button clearButton =
            createButton("CLEAR LOCATION", false);

        HBox filterRow = new HBox(
            10,
            locationLabel,
            locationField,
            matchButton,
            clearButton
        );

        filterRow.setAlignment(Pos.CENTER);

        // DONOR TABLE
        donorTable = new TableView<>();

        donorList = FXCollections.observableArrayList();

        donorTable.setItems(donorList);
        donorTable.setPrefHeight(280);

        donorTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        addColumn("Donor Name", 0);
        addColumn("Contact", 1);
        addColumn("Blood Group", 2);
        addColumn("Location", 3);
        addColumn("Availability", 4);

        messageLabel = new Label("");

        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.web("#16803C"));

        Button backButton =
            createButton("BACK TO DASHBOARD", false);

        // BUTTON ACTIONS
        loadButton.setOnAction(e -> loadSelectedRequest());

        matchButton.setOnAction(e -> findMatchingDonors());

        clearButton.setOnAction(e -> {
            locationField.clear();
            findMatchingDonors();
        });

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
            requestRow,
            requestDetailsLabel,
            filterRow,
            donorTable,
            messageLabel,
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

        root.getChildren().addAll(decorations, scroll);

        loadRequestsIntoComboBox();

        if (selectedRequestId != null) {
            requestBox.setValue(selectedRequestId);
            loadSelectedRequest();
        }

        return root;
    }

    // LOAD REQUEST IDS
    private void loadRequestsIntoComboBox() {

        requestBox.getItems().clear();

        File file = new File(REQUEST_FILE);

        if (!file.exists()) {
            messageLabel.setText("No blood requests found.");
            return;
        }

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length == 10) {
                    requestBox.getItems().add(details[0]);
                }
            }

            if (requestBox.getItems().isEmpty()) {
                messageLabel.setText("No valid requests available.");
            }

        } catch (IOException e) {

            e.printStackTrace();

            messageLabel.setText("Unable to load requests.");
        }
    }

    // LOAD SELECTED REQUEST
    private void loadSelectedRequest() {

        String requestId = requestBox.getValue();

        if (requestId == null || requestId.trim().isEmpty()) {

            showMessage("Please select a Request ID.", true);
            return;
        }

        File file = new File(REQUEST_FILE);

        if (!file.exists()) {

            showMessage("Request file not found.", true);
            return;
        }

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length == 10
                        && details[0].equals(requestId)) {

                    requestDetailsLabel.setText(
                        "Request ID: " + details[0] +
                        "     Patient: " + details[1] +
                        "     Blood Group: " + details[3] +
                        "     Units: " + details[4] +
                        "\nHospital: " + details[5] +
                        "     Location: " + details[6] +
                        "     Urgency: " + details[8] +
                        "     Status: " + details[9]
                    );

                    donorList.clear();

                    showMessage(
                        "Request loaded. Find matching donors.",
                        false
                    );

                    return;
                }
            }

            showMessage("Selected request was not found.", true);

        } catch (IOException e) {

            e.printStackTrace();

            showMessage("Unable to read request details.", true);
        }
    }

    // FIND MATCHING DONORS
    private void findMatchingDonors() {

        String requestId = requestBox.getValue();

        if (requestId == null) {

            showMessage(
                "Please select and load a request first.",
                true
            );

            return;
        }

        String recipientBlood =
            getRecipientBloodGroup(requestId);

        if (recipientBlood == null) {

            showMessage("Unable to find the selected request.", true);
            return;
        }

        String requiredLocation =
            locationField.getText().trim().toLowerCase();

        donorList.clear();

        File file = new File(DONOR_FILE);

        if (!file.exists()) {

            showMessage("No donor records found.", true);
            return;
        }

        int compatibleCount = 0;

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length != 5) {
                    continue;
                }

                String donorBlood =
                    details[2].trim().toUpperCase();

                String availability =
                    details[4].trim();

                String donorLocation =
                    details[3].trim();

                // ONLY AVAILABLE DONORS
                if (!availability.equalsIgnoreCase("Available")) {
                    continue;
                }

                // PRELIMINARY BLOOD GROUP COMPATIBILITY
                if (!isCompatible(donorBlood, recipientBlood)) {
                    continue;
                }

                // OPTIONAL LOCATION FILTER
                if (!requiredLocation.isEmpty()
                        && !donorLocation.toLowerCase()
                            .contains(requiredLocation)) {
                    continue;
                }

                donorList.add(
                    new Donor(
                        details[0],
                        details[1],
                        donorBlood,
                        donorLocation,
                        availability
                    )
                );

                compatibleCount++;
            }

            showMessage(
                compatibleCount +
                " preliminary compatible available donor(s) found. " +
                "Blood-bank testing and crossmatching are required.",
                false
            );

        } catch (IOException e) {

            e.printStackTrace();

            showMessage("Unable to read donor records.", true);
        }
    }

    // GET RECIPIENT BLOOD GROUP
    private String getRecipientBloodGroup(String requestId) {

        File file = new File(REQUEST_FILE);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length == 10
                        && details[0].equals(requestId)) {

                    return details[3].trim().toUpperCase();
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    // PRELIMINARY ABO/Rh RED-CELL COMPATIBILITY
    private boolean isCompatible(
        String donorBlood,
        String recipientBlood
    ) {

        if (!isValidBloodGroup(donorBlood)
                || !isValidBloodGroup(recipientBlood)) {

            return false;
        }

        String donorABO =
            donorBlood.substring(0, donorBlood.length() - 1);

        String recipientABO =
            recipientBlood.substring(0, recipientBlood.length() - 1);

        String donorRh =
            donorBlood.substring(donorBlood.length() - 1);

        String recipientRh =
            recipientBlood.substring(recipientBlood.length() - 1);

        boolean aboCompatible = false;

        if (donorABO.equals("O")) {

            aboCompatible = true;

        } else if (donorABO.equals("A")) {

            aboCompatible =
                recipientABO.equals("A") ||
                recipientABO.equals("AB");

        } else if (donorABO.equals("B")) {

            aboCompatible =
                recipientABO.equals("B") ||
                recipientABO.equals("AB");

        } else if (donorABO.equals("AB")) {

            aboCompatible = recipientABO.equals("AB");
        }

        boolean rhCompatible =
            donorRh.equals("-") || recipientRh.equals("+");

        return aboCompatible && rhCompatible;
    }

    private boolean isValidBloodGroup(String blood) {

        return blood != null && (
            blood.equals("A+") ||
            blood.equals("A-") ||
            blood.equals("B+") ||
            blood.equals("B-") ||
            blood.equals("AB+") ||
            blood.equals("AB-") ||
            blood.equals("O+") ||
            blood.equals("O-")
        );
    }

    // TABLE COLUMNS
    private void addColumn(String heading, int index) {

        TableColumn<Donor, String> column =
            new TableColumn<>(heading);

        column.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getValue(index)
            )
        );

        donorTable.getColumns().add(column);
    }

    // MESSAGE
    private void showMessage(String message, boolean error) {

        messageLabel.setText(message);

        messageLabel.setTextFill(
            error ? Color.RED : Color.web("#16803C")
        );
    }

    // TEXT FIELD STYLE
    private String inputStyle() {

        return
            "-fx-background-color: #FFF5F7;" +
            "-fx-border-color: #E7A0B1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8;";
    }

    // COMBO BOX STYLE
    private String comboStyle() {

        return
            "-fx-background-color: #FFF5F7;" +
            "-fx-border-color: #E7A0B1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;";
    }

    // BUTTON STYLE
    private Button createButton(String text, boolean primary) {

        Button button = new Button(text);

        button.setPrefHeight(40);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        if (primary) {

            button.setStyle(
                "-fx-background-color: #A00030;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 18 10 18;"
            );

        } else {

            button.setStyle(
                "-fx-background-color: #F3E5E9;" +
                "-fx-text-fill: #700019;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 18 10 18;"
            );
        }

        return button;
    }

    // LOGO
    private ImageView createLogo() {

        ImageView view = new ImageView();

        try {

            Image image = new Image(
                getClass().getResourceAsStream("/images/logo.png")
            );

            view.setImage(image);

        } catch (Exception e) {

            System.out.println("Logo image not found.");
        }

        return view;
    }

    // BACKGROUND DECORATIONS
    private Pane createDecorations() {

        Pane pane = new Pane();

        pane.setMouseTransparent(true);

        Circle drop1 = new Circle(
            250, 170, 25,
            Color.web("#A00030", 0.35)
        );

        Circle drop2 = new Circle(
            1050, 500, 35,
            Color.web("#E00045", 0.25)
        );

        Polyline ecg1 = new Polyline(
            0, 350, 100, 350, 120, 330, 140, 380,
            160, 290, 180, 350, 300, 350
        );

        ecg1.setStroke(Color.web("#FF8DA8", 0.45));
        ecg1.setStrokeWidth(4);
        ecg1.setFill(Color.TRANSPARENT);

        Polyline ecg2 = new Polyline(
            900, 350, 980, 350, 1000, 330, 1020, 380,
            1040, 290, 1060, 350, 1200, 350
        );

        ecg2.setStroke(Color.web("#FF8DA8", 0.45));
        ecg2.setStrokeWidth(4);
        ecg2.setFill(Color.TRANSPARENT);

        pane.getChildren().addAll(
            drop1,
            drop2,
            ecg1,
            ecg2,
            createWave(0, 650, "#8B0025"),
            createWave(0, 675, "#C00040"),
            createWave(0, 695, "#F2A6B8")
        );

        return pane;
    }

    // BOTTOM WAVES
    private Path createWave(double x, double y, String color) {

        Path path = new Path();

        path.getElements().addAll(
            new MoveTo(x, y),

            new CubicCurveTo(
                300, y - 80,
                450, y + 40,
                700, y - 20
            ),

            new CubicCurveTo(
                900, y - 70,
                1050, y + 30,
                1200, y - 10
            ),

            new LineTo(1200, 720),
            new LineTo(0, 720),
            new ClosePath()
        );

        path.setFill(Color.web(color, 0.8));

        return path;
    }

    // DONOR MODEL
    public static class Donor {

        private final String[] values;

        public Donor(
            String name,
            String phone,
            String blood,
            String location,
            String availability
        ) {

            values = new String[] {
                name,
                phone,
                blood,
                location,
                availability
            };
        }

        public String getValue(int index) {
            return values[index];
        }
    }
}
