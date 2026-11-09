
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RequestHistoryController {

    private static final String FILE = "data/requirements.txt";

    private TableView<Request> table;
    private ObservableList<Request> requestList;

    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> newStatusBox;
    private Label messageLabel;

    private Runnable backAction;

    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    public Parent createHistoryScreen() {

        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #39000D, #700019, #A00030, #500013);"
        );

        Pane decorations = createDecorations();

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 35, 30, 35));
        card.setMaxWidth(1100);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: #F2A6B8;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 25, 0.2, 0, 10);"
        );

        ImageView logo = createLogo();
        logo.setFitWidth(70);
        logo.setFitHeight(70);
        logo.setPreserveRatio(true);

        Label heading = new Label(
            "Smart Emergency Resource and\nBlood Donor Management System"
        );
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#700019"));
        heading.setAlignment(Pos.CENTER);
        heading.setWrapText(true);

        HBox divider = createHeartDivider();

        Label title = new Label("BLOOD REQUEST HISTORY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        title.setTextFill(Color.web("#8B0025"));

        Label subtitle = new Label("View and manage submitted blood requests");
        subtitle.setTextFill(Color.web("#777777"));
        subtitle.setFont(Font.font("Arial", 12));

        searchField = new TextField();
        searchField.setPromptText("Search by request ID or patient name");
        searchField.setPrefWidth(280);
        searchField.setStyle(inputStyle());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll(
            "All", "Pending", "Fulfilled", "Cancelled"
        );
        statusFilter.setValue("All");
        styleComboBox(statusFilter);

        Button searchButton = createButton("SEARCH", true);
        Button refreshButton = createButton("REFRESH", false);

        searchButton.setOnAction(e -> applyFilters());
        refreshButton.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All");
            loadRequests();
        });

        HBox searchRow = new HBox(
            10, searchField, statusFilter, searchButton, refreshButton
        );
        searchRow.setAlignment(Pos.CENTER);

        table = new TableView<>();
        requestList = FXCollections.observableArrayList();
        table.setItems(requestList);
        table.setPrefHeight(330);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        addColumn("Request ID", 0);
        addColumn("Patient", 1);
        addColumn("Contact", 2);
        addColumn("Blood", 3);
        addColumn("Units", 4);
        addColumn("Hospital", 5);
        addColumn("Location", 6);
        addColumn("Date", 7);
        addColumn("Urgency", 8);
        addColumn("Status", 9);

        Label updateTitle = new Label("UPDATE REQUEST STATUS");
        updateTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        updateTitle.setTextFill(Color.web("#700019"));

        newStatusBox = new ComboBox<>();
        newStatusBox.getItems().addAll(
            "Pending", "Fulfilled", "Cancelled"
        );
        newStatusBox.setValue("Fulfilled");
        styleComboBox(newStatusBox);

        Button updateButton = createButton("UPDATE STATUS", true);
        updateButton.setOnAction(e -> updateSelectedStatus());

        HBox updateRow = new HBox(12, newStatusBox, updateButton);
        updateRow.setAlignment(Pos.CENTER);

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.web("#16803C"));

        Button backButton = createButton("BACK TO DASHBOARD", false);
        backButton.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            }
        });

        card.getChildren().addAll(
            logo, heading, divider, title, subtitle,
            searchRow, table, updateTitle, updateRow,
            messageLabel, backButton
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

        loadRequests();

        return root;
    }

    private void addColumn(String heading, int index) {
        TableColumn<Request, String> column =
            new TableColumn<>(heading);

        column.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getValue(index)
            )
        );

        table.getColumns().add(column);
    }

    private void loadRequests() {
        requestList.clear();

        File file = new File(FILE);

        if (!file.exists()) {
            messageLabel.setText("No blood requests have been submitted yet.");
            return;
        }

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] details = line.split("\\|", -1);

                if (details.length == 10) {
                    requestList.add(new Request(details));
                }
            }

            messageLabel.setText(
                requestList.size() + " request(s) loaded."
            );

        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to read request history.");
        }
    }

    private void applyFilters() {

        String search = searchField.getText()
            .trim().toLowerCase();

        String selectedStatus = statusFilter.getValue();

        requestList.clear();

        File file = new File(FILE);

        if (!file.exists()) {
            messageLabel.setText("No requests found.");
            return;
        }

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] details = line.split("\\|", -1);

                if (details.length != 10) {
                    continue;
                }

                boolean matchesSearch =
                    details[0].toLowerCase().contains(search)
                    || details[1].toLowerCase().contains(search);

                boolean matchesStatus =
                    selectedStatus == null
                    || selectedStatus.equals("All")
                    || details[9].equalsIgnoreCase(selectedStatus);

                if (matchesSearch && matchesStatus) {
                    requestList.add(new Request(details));
                }
            }

            messageLabel.setText(
                requestList.size() + " matching request(s) found."
            );

        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to search requests.");
        }
    }

    private void updateSelectedStatus() {

        Request selected = table.getSelectionModel()
            .getSelectedItem();

        if (selected == null) {
            showError("Please select a request from the table.");
            return;
        }

        String newStatus = newStatusBox.getValue();

        if (newStatus == null) {
            showError("Please select a status.");
            return;
        }

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try {

            File file = new File(FILE);

            if (!file.exists()) {
                showError("Request history file not found.");
                return;
            }

            try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] details = line.split("\\|", -1);

                    if (details.length == 10
                            && details[0].equals(selected.getValue(0))) {

                        details[9] = newStatus;
                        line = String.join("|", details);
                        found = true;
                    }

                    updatedLines.add(line);
                }
            }

            if (!found) {
                showError("Selected request was not found.");
                return;
            }

            try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(file, false))) {

                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            messageLabel.setText(
                "Request status updated to " + newStatus + "."
            );
            messageLabel.setTextFill(Color.web("#16803C"));

            loadRequests();
            applyFilters();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to update request status.");
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.RED);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Request History");
        alert.setHeaderText("Action could not be completed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String inputStyle() {
        return
            "-fx-background-color: #FFF5F7;" +
            "-fx-border-color: #E7A0B1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8;";
    }

    private void styleComboBox(ComboBox<String> combo) {
        combo.setPrefHeight(38);
        combo.setStyle(
            "-fx-background-color: #FFF5F7;" +
            "-fx-border-color: #E7A0B1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
    }

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

    private Pane createDecorations() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);

        Circle drop1 = new Circle(250, 170, 25, Color.web("#A00030", 0.35));
        Circle drop2 = new Circle(1050, 500, 35, Color.web("#E00045", 0.25));

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

        pane.getChildren().addAll(drop1, drop2, ecg1, ecg2);

        pane.getChildren().addAll(
            createWave(0, 650, "#8B0025"),
            createWave(0, 675, "#C00040"),
            createWave(0, 695, "#F2A6B8")
        );

        return pane;
    }

    private Path createWave(double x, double y, String color) {
        Path path = new Path();

        path.getElements().addAll(
            new MoveTo(x, y),
            new CubicCurveTo(300, y - 80, 450, y + 40, 700, y - 20),
            new CubicCurveTo(900, y - 70, 1050, y + 30, 1200, y - 10),
            new LineTo(1200, 720),
            new LineTo(0, 720),
            new ClosePath()
        );

        path.setFill(Color.web(color, 0.8));
        return path;
    }

    public static class Request {

        private final String[] values;

        public Request(String[] values) {
            this.values = values;
        }

        public String getValue(int index) {
            return values[index];
        }
    }
}
