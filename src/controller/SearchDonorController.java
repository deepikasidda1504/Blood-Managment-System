
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SearchDonorController {

    private ComboBox<String> bloodGroupBox;
    private TextField locationField;
    private Label messageLabel;

    private TableView<Donor> donorTable;

    private ObservableList<Donor> donorList =
            FXCollections.observableArrayList();

    private Runnable backAction;

    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    public Parent createSearchScreen() {

        StackPane root = new StackPane();

        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        // Background decorations
        Pane decorations = new Pane();
        decorations.setMouseTransparent(true);

        addBloodDrop(decorations, 140, 170, 35);
        addBloodDrop(decorations, 1100, 530, 45);
        addECG(decorations, 30, 350);
        addECG(decorations, 1000, 400);
        addBloodBag(decorations, 1100, 80);
        addWaves(decorations);

        // Main white card
        VBox card = new VBox(15);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30, 40, 30, 40));
        card.setMaxWidth(1000);
        card.setMaxHeight(680);

        card.setStyle(
            "-fx-background-color: #FFF5F8;" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: #F3A6C0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 20, 0, 0, 8);"
        );

        // Logo
        ImageView logo = createLogo();

        Label heading = new Label(
            "SMART EMERGENCY RESOURCE\nAND BLOOD DONOR MANAGEMENT SYSTEM"
        );

        heading.setFont(Font.font("Arial", FontWeight.BOLD, 21));
        heading.setTextFill(Color.web("#85002F"));
        heading.setAlignment(Pos.CENTER);
        heading.setWrapText(true);

        Label divider = new Label("──────────── ♥ ────────────");
        divider.setTextFill(Color.web("#B00040"));
        divider.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label title = new Label("SEARCH BLOOD DONORS");

        title.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        title.setTextFill(Color.web("#85002F"));

        Label subtitle = new Label(
            "Find available donors by blood group and location"
        );

        subtitle.setTextFill(Color.web("#7A3550"));
        subtitle.setFont(Font.font("Arial", 14));

        // Blood group selection
        Label bloodLabel = createLabel("Select Blood Group");

        bloodGroupBox = new ComboBox<>();

        bloodGroupBox.getItems().addAll(
            "All", "A+", "A-", "B+", "B-",
            "AB+", "AB-", "O+", "O-"
        );

        bloodGroupBox.setValue("All");
        bloodGroupBox.setPrefHeight(42);
        bloodGroupBox.setMaxWidth(Double.MAX_VALUE);
        styleComboBox(bloodGroupBox);

        // Location input
        Label locationLabel = createLabel("Enter Location");

        locationField = new TextField();
        locationField.setPromptText("Enter city or area");
        locationField.setPrefHeight(42);
        locationField.setStyle(inputStyle());

        // Search button
        Button searchButton = new Button("SEARCH DONORS");

        searchButton.setPrefHeight(45);
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setStyle(buttonStyle());

        searchButton.setOnAction(e -> searchDonors());

        // Message
        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.web("#B00040"));

        // Results table
        donorTable = new TableView<>();

        donorTable.setItems(donorList);
        donorTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        donorTable.setPrefHeight(230);
        donorTable.setMinHeight(180);

        TableColumn<Donor, String> nameColumn =
                new TableColumn<>("Donor Name");

        nameColumn.setCellValueFactory(
            data -> data.getValue().nameProperty()
        );

        TableColumn<Donor, String> phoneColumn =
                new TableColumn<>("Contact Number");

        phoneColumn.setCellValueFactory(
            data -> data.getValue().phoneProperty()
        );

        TableColumn<Donor, String> bloodColumn =
                new TableColumn<>("Blood Group");

        bloodColumn.setCellValueFactory(
            data -> data.getValue().bloodGroupProperty()
        );

        TableColumn<Donor, String> locationColumn =
                new TableColumn<>("Location");

        locationColumn.setCellValueFactory(
            data -> data.getValue().locationProperty()
        );

        TableColumn<Donor, String> availabilityColumn =
                new TableColumn<>("Availability");

        availabilityColumn.setCellValueFactory(
            data -> data.getValue().availabilityProperty()
        );

        donorTable.getColumns().addAll(
            nameColumn,
            phoneColumn,
            bloodColumn,
            locationColumn,
            availabilityColumn
        );

        donorTable.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E99AB5;" +
            "-fx-border-radius: 8;"
        );

        // Back button
        Button backButton = new Button("BACK TO DASHBOARD");

        backButton.setPrefHeight(42);
        backButton.setMaxWidth(Double.MAX_VALUE);

        backButton.setStyle(
            "-fx-background-color: #65002A;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;"
        );

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
            bloodLabel,
            bloodGroupBox,
            locationLabel,
            locationField,
            searchButton,
            messageLabel,
            donorTable,
            backButton
        );

        ScrollPane scrollPane = new ScrollPane(card);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;"
        );

        scrollPane.setMaxWidth(1050);
        scrollPane.setMaxHeight(710);

        root.getChildren().addAll(decorations, scrollPane);

        return root;
    }

    // Search donor records from donors.txt
    private void searchDonors() {

        donorList.clear();
        messageLabel.setText("");

        String selectedBloodGroup = bloodGroupBox.getValue();

        String location = locationField.getText()
                .trim().toLowerCase();

        File file = new File("data/donors.txt");

        if (!file.exists()) {
            messageLabel.setText(
                "Donor file not found. Please register donors first."
            );
            return;
        }

        int count = 0;

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                // Expected format:
                // name|phone|bloodGroup|location|availability

                if (details.length != 5) {
                    continue;
                }

                String name = details[0].trim();
                String phone = details[1].trim();
                String bloodGroup = details[2].trim();
                String donorLocation = details[3].trim();
                String availability = details[4].trim();

                boolean bloodMatches =
                    selectedBloodGroup.equals("All")
                    || bloodGroup.equalsIgnoreCase(selectedBloodGroup);

                boolean locationMatches =
                    location.isEmpty()
                    || donorLocation.toLowerCase().contains(location);

                boolean isAvailable =
                    availability.equalsIgnoreCase("Available");

                if (bloodMatches && locationMatches && isAvailable) {

                    donorList.add(
                        new Donor(
                            name,
                            phone,
                            bloodGroup,
                            donorLocation,
                            availability
                        )
                    );

                    count++;
                }
            }

            if (count == 0) {
                messageLabel.setText(
                    "No available donors found for your search."
                );
            } else {
                messageLabel.setText(
                    count + " available donor(s) found."
                );
                messageLabel.setTextFill(Color.GREEN);
            }

        } catch (IOException e) {
            messageLabel.setText(
                "Error reading donor file: " + e.getMessage()
            );
        }
    }

    private Label createLabel(String text) {

        Label label = new Label(text);

        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#65002A"));

        return label;
    }

    private ImageView createLogo() {

        try {
            Image image = new Image(
                getClass().getResourceAsStream("/images/logo.png")
            );

            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(75);
            imageView.setFitHeight(75);
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
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;";
    }

    private void styleComboBox(ComboBox<String> box) {

        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E99AB5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-font-size: 14px;"
        );
    }

    private String buttonStyle() {

        return
            "-fx-background-color: linear-gradient(to right, #A00040, #E64A78);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 10;";
    }

    // Background decorations

    private void addBloodDrop(
            Pane pane, double x, double y, double size) {

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

        Rectangle blood = new Rectangle(
            x + 5, y + 48, 45, 30
        );

        blood.setArcWidth(8);
        blood.setArcHeight(8);
        blood.setFill(Color.web("#E64A78", 0.35));

        pane.getChildren().addAll(bag, blood);
    }

    private void addWaves(Pane pane) {

        Path wave1 = new Path();

        wave1.getElements().addAll(
            new MoveTo(0, 650),
            new CubicCurveTo(
                350, 570, 650, 760, 1000, 650
            ),
            new CubicCurveTo(
                1250, 570, 1450, 650, 1650, 600
            ),
            new LineTo(1650, 850),
            new LineTo(0, 850),
            new ClosePath()
        );

        wave1.setFill(Color.web("#A00040", 0.45));

        Path wave2 = new Path();

        wave2.getElements().addAll(
            new MoveTo(0, 700),
            new CubicCurveTo(
                400, 610, 700, 800, 1100, 690
            ),
            new CubicCurveTo(
                1300, 640, 1450, 710, 1650, 670
            ),
            new LineTo(1650, 850),
            new LineTo(0, 850),
            new ClosePath()
        );

        wave2.setFill(Color.web("#E64A78", 0.30));

        pane.getChildren().addAll(wave1, wave2);
    }

    // Donor model class
    public static class Donor {

        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleStringProperty phone;
        private final javafx.beans.property.SimpleStringProperty bloodGroup;
        private final javafx.beans.property.SimpleStringProperty location;
        private final javafx.beans.property.SimpleStringProperty availability;

        public Donor(
                String name,
                String phone,
                String bloodGroup,
                String location,
                String availability) {

            this.name =
                new javafx.beans.property.SimpleStringProperty(name);

            this.phone =
                new javafx.beans.property.SimpleStringProperty(phone);

            this.bloodGroup =
                new javafx.beans.property.SimpleStringProperty(bloodGroup);

            this.location =
                new javafx.beans.property.SimpleStringProperty(location);

            this.availability =
                new javafx.beans.property.SimpleStringProperty(availability);
        }

        public javafx.beans.property.StringProperty nameProperty() {
            return name;
        }

        public javafx.beans.property.StringProperty phoneProperty() {
            return phone;
        }

        public javafx.beans.property.StringProperty bloodGroupProperty() {
            return bloodGroup;
        }

        public javafx.beans.property.StringProperty locationProperty() {
            return location;
        }

        public javafx.beans.property.StringProperty availabilityProperty() {
            return availability;
        }
    }
}
