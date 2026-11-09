
package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReportsController {

    private static final String DONOR_FILE = "data/donors.txt";
    private static final String REQUIREMENT_FILE = "data/requirements.txt";

    private Runnable backAction;

    private Label totalDonorsLabel;
    private Label availableDonorsLabel;
    private Label unavailableDonorsLabel;

    private Label totalRequestsLabel;
    private Label pendingLabel;
    private Label fulfilledLabel;
    private Label cancelledLabel;

    private Label highLabel;
    private Label mediumLabel;
    private Label lowLabel;

    private VBox bloodGroupBox;
    private Label messageLabel;

    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    public Parent createReportsScreen() {

        StackPane root = new StackPane();

        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        Pane decorations = createDecorations();

        VBox card = new VBox(16);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28, 35, 30, 35));
        card.setMaxWidth(850);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: #F2A6B8;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 25, 0.2, 0, 10);"
        );

        ImageView logo = createLogo();
        logo.setFitWidth(75);
        logo.setFitHeight(75);
        logo.setPreserveRatio(true);

        Label heading = new Label(
            "Smart Emergency Resource and\nBlood Donor Management System"
        );

        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#700019"));
        heading.setAlignment(Pos.CENTER);
        heading.setWrapText(true);

        HBox divider = createHeartDivider();

        Label title = new Label("SYSTEM REPORTS");

        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#A00030"));

        Label subtitle = new Label(
            "Overview of donors and emergency blood requests"
        );

        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#777777"));

        // DONOR STATISTICS

        Label donorTitle = createSectionTitle("DONOR STATISTICS");

        totalDonorsLabel = new Label("0");
        availableDonorsLabel = new Label("0");
        unavailableDonorsLabel = new Label("0");

        GridPane donorGrid = new GridPane();

        donorGrid.setHgap(12);
        donorGrid.setVgap(12);
        donorGrid.setAlignment(Pos.CENTER);

        donorGrid.add(
            createStatCard("TOTAL DONORS", totalDonorsLabel),
            0, 0
        );

        donorGrid.add(
            createStatCard("AVAILABLE DONORS", availableDonorsLabel),
            1, 0
        );

        donorGrid.add(
            createStatCard("UNAVAILABLE DONORS", unavailableDonorsLabel),
            2, 0
        );

        // BLOOD GROUP STATISTICS

        Label bloodTitle = createSectionTitle("BLOOD GROUP DISTRIBUTION");

        bloodGroupBox = new VBox(8);

        bloodGroupBox.setPadding(new Insets(15));
        bloodGroupBox.setStyle(
            "-fx-background-color: #FFF5F7;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #F2A6B8;" +
            "-fx-border-radius: 15;"
        );

        // REQUEST STATISTICS

        Label requestTitle = createSectionTitle("BLOOD REQUEST STATISTICS");

        totalRequestsLabel = new Label("0");
        pendingLabel = new Label("0");
        fulfilledLabel = new Label("0");
        cancelledLabel = new Label("0");

        GridPane requestGrid = new GridPane();

        requestGrid.setHgap(12);
        requestGrid.setVgap(12);
        requestGrid.setAlignment(Pos.CENTER);

        requestGrid.add(
            createStatCard("TOTAL REQUESTS", totalRequestsLabel),
            0, 0
        );

        requestGrid.add(
            createStatCard("PENDING", pendingLabel),
            1, 0
        );

        requestGrid.add(
            createStatCard("FULFILLED", fulfilledLabel),
            2, 0
        );

        requestGrid.add(
            createStatCard("CANCELLED", cancelledLabel),
            3, 0
        );

        // URGENCY STATISTICS

        Label urgencyTitle = createSectionTitle("REQUEST URGENCY");

        highLabel = new Label("0");
        mediumLabel = new Label("0");
        lowLabel = new Label("0");

        GridPane urgencyGrid = new GridPane();

        urgencyGrid.setHgap(12);
        urgencyGrid.setVgap(12);
        urgencyGrid.setAlignment(Pos.CENTER);

        urgencyGrid.add(
            createStatCard("HIGH URGENCY", highLabel),
            0, 0
        );

        urgencyGrid.add(
            createStatCard("MEDIUM URGENCY", mediumLabel),
            1, 0
        );

        urgencyGrid.add(
            createStatCard("LOW URGENCY", lowLabel),
            2, 0
        );

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.web("#700019"));

        Button refreshButton =
            createButton("REFRESH REPORTS", true);

        Button backButton =
            createButton("BACK TO DASHBOARD", false);

        refreshButton.setOnAction(e -> loadReports());

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
            donorTitle,
            donorGrid,
            bloodTitle,
            bloodGroupBox,
            requestTitle,
            requestGrid,
            urgencyTitle,
            urgencyGrid,
            messageLabel,
            refreshButton,
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

        loadReports();

        return root;
    }

    // SECTION TITLE

    private Label createSectionTitle(String text) {

        Label label = new Label(text);

        label.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        label.setTextFill(Color.web("#8B0025"));

        return label;
    }

    // STATISTIC CARD

    private VBox createStatCard(String title, Label value) {

        Label heading = new Label(title);

        heading.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        heading.setTextFill(Color.web("#700019"));
        heading.setWrapText(true);
        heading.setAlignment(Pos.CENTER);

        value.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        value.setTextFill(Color.web("#A00030"));

        VBox box = new VBox(8, heading, value);

        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setMinWidth(145);
        box.setPrefWidth(175);
        box.setMinHeight(90);

        box.setStyle(
            "-fx-background-color: #FFF5F7;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #F2A6B8;" +
            "-fx-border-radius: 15;"
        );

        return box;
    }

    // LOAD ALL REPORTS

    private void loadReports() {

        int totalDonors = 0;
        int availableDonors = 0;
        int unavailableDonors = 0;

        int totalRequests = 0;
        int pending = 0;
        int fulfilled = 0;
        int cancelled = 0;

        int high = 0;
        int medium = 0;
        int low = 0;

        Map<String, Integer> bloodGroups = new HashMap<>();

        String[] groups = {
            "A+", "A-", "B+", "B-",
            "AB+", "AB-", "O+", "O-"
        };

        for (String group : groups) {
            bloodGroups.put(group, 0);
        }

        // READ DONOR FILE

        File donorFile = new File(DONOR_FILE);

        if (donorFile.exists()) {

            try (BufferedReader reader =
                     new BufferedReader(new FileReader(donorFile))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] details = line.split("\\|", -1);

                    if (details.length != 5) {
                        continue;
                    }

                    totalDonors++;

                    String blood = details[2].trim().toUpperCase();
                    String availability = details[4].trim();

                    if (bloodGroups.containsKey(blood)) {
                        bloodGroups.put(
                            blood,
                            bloodGroups.get(blood) + 1
                        );
                    }

                    if (availability.equalsIgnoreCase("Available")) {
                        availableDonors++;
                    } else {
                        unavailableDonors++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error reading donor records.");
            }

        }

        // READ BLOOD REQUIREMENT FILE

        File requestFile = new File(REQUIREMENT_FILE);

        if (requestFile.exists()) {

            try (BufferedReader reader =
                     new BufferedReader(new FileReader(requestFile))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] details = line.split("\\|", -1);

                    if (details.length != 10) {
                        continue;
                    }

                    totalRequests++;

                    String urgency = details[8].trim();
                    String status = details[9].trim();

                    if (status.equalsIgnoreCase("Pending")) {
                        pending++;
                    } else if (status.equalsIgnoreCase("Fulfilled")) {
                        fulfilled++;
                    } else if (status.equalsIgnoreCase("Cancelled")) {
                        cancelled++;
                    }

                    if (urgency.equalsIgnoreCase("High")) {
                        high++;
                    } else if (urgency.equalsIgnoreCase("Medium")) {
                        medium++;
                    } else if (urgency.equalsIgnoreCase("Low")) {
                        low++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error reading blood requests.");
            }
        }

        // UPDATE DONOR LABELS

        totalDonorsLabel.setText(String.valueOf(totalDonors));
        availableDonorsLabel.setText(String.valueOf(availableDonors));
        unavailableDonorsLabel.setText(String.valueOf(unavailableDonors));

        // UPDATE BLOOD GROUP DISTRIBUTION

        bloodGroupBox.getChildren().clear();

        for (String group : groups) {

            int count = bloodGroups.get(group);

            Label groupLabel = new Label(
                group + "     :     " + count + " donor(s)"
            );

            groupLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
            );

            groupLabel.setTextFill(Color.web("#700019"));

            bloodGroupBox.getChildren().add(groupLabel);
        }

        // UPDATE REQUEST LABELS

        totalRequestsLabel.setText(String.valueOf(totalRequests));
        pendingLabel.setText(String.valueOf(pending));
        fulfilledLabel.setText(String.valueOf(fulfilled));
        cancelledLabel.setText(String.valueOf(cancelled));

        // UPDATE URGENCY LABELS

        highLabel.setText(String.valueOf(high));
        mediumLabel.setText(String.valueOf(medium));
        lowLabel.setText(String.valueOf(low));

        messageLabel.setText(
            "Reports updated successfully."
        );

        messageLabel.setTextFill(Color.web("#16803C"));
    }

    // BUTTON DESIGN

    private Button createButton(String text, boolean primary) {

        Button button = new Button(text);

        button.setPrefWidth(260);
        button.setPrefHeight(40);

        button.setFont(
            Font.font("Arial", FontWeight.BOLD, 12)
        );

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

    // HEART DIVIDER

    private HBox createHeartDivider() {

        Label left = new Label("━━━━━━━━");
        Label heart = new Label(" ♥ ");
        Label right = new Label("━━━━━━━━");

        left.setTextFill(Color.web("#B00035"));
        right.setTextFill(Color.web("#B00035"));
        heart.setTextFill(Color.web("#B00035"));

        heart.setFont(Font.font(22));

        HBox divider = new HBox(left, heart, right);

        divider.setAlignment(Pos.CENTER);

        return divider;
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
            0, 350,
            100, 350,
            120, 330,
            140, 380,
            160, 290,
            180, 350,
            300, 350
        );

        ecg1.setStroke(Color.web("#FF8DA8", 0.45));
        ecg1.setStrokeWidth(4);
        ecg1.setFill(Color.TRANSPARENT);

        Polyline ecg2 = new Polyline(
            900, 350,
            980, 350,
            1000, 330,
            1020, 380,
            1040, 290,
            1060, 350,
            1200, 350
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

    private Path createWave(
        double x,
        double y,
        String color
    ) {

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
}
