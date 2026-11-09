
package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardController {

    private Scene scene;

    public Parent createDashboardScreen(Scene scene) {

        this.scene = scene;

        StackPane root = new StackPane();

        // ORIGINAL MAROON BACKGROUND
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " +
            "#39000D, #700019, #A00030, #500013);"
        );

        // BACKGROUND DECORATIONS
        Pane decorations = createDecorations();

        // MAIN WHITE CARD
        VBox card = new VBox(15);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 40, 30, 40));
        card.setMaxWidth(850);

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
        logo.setFitWidth(80);
        logo.setFitHeight(80);
        logo.setPreserveRatio(true);

        // SYSTEM HEADING
        Label heading = new Label(
            "Smart Emergency Resource and\nBlood Donor Management System"
        );

        heading.setFont(Font.font("Arial", FontWeight.BOLD, 21));
        heading.setTextFill(Color.web("#700019"));
        heading.setAlignment(Pos.CENTER);
        heading.setWrapText(true);

        // HEART DIVIDER
        Label heartDivider = createHeartDivider();

        // WELCOME MESSAGE
        Label welcome = new Label("WELCOME TO DASHBOARD");

        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcome.setTextFill(Color.web("#A00030"));

        Label subtitle = new Label(
            "Manage blood donors and emergency blood requirements"
        );

        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#777777"));

        // MENU BUTTONS
        Button registerButton =
            createMenuButton("REGISTER DONOR", true);

        Button searchButton =
            createMenuButton("SEARCH DONORS", true);

        Button requirementsButton =
            createMenuButton("BLOOD REQUIREMENTS", true);

        Button matchingButton =
            createMenuButton("DONOR MATCHING", true);

        Button historyButton =
            createMenuButton("REQUEST HISTORY", false);

        Button reportsButton =
            createMenuButton("REPORTS", false);

        Button logoutButton =
            createMenuButton("LOGOUT", false);

        // BUTTON GRID
        GridPane menuGrid = new GridPane();

        menuGrid.setAlignment(Pos.CENTER);
        menuGrid.setHgap(20);
        menuGrid.setVgap(15);

        menuGrid.add(registerButton, 0, 0);
        menuGrid.add(searchButton, 1, 0);

        menuGrid.add(requirementsButton, 0, 1);
        menuGrid.add(matchingButton, 1, 1);

        menuGrid.add(historyButton, 0, 2);
        menuGrid.add(reportsButton, 1, 2);

        // BUTTON ACTIONS

        // REGISTER DONOR
        registerButton.setOnAction(e -> {

            DonorRegistrationController registration =
                new DonorRegistrationController();

            registration.setBackAction(() ->
                scene.setRoot(createDashboardScreen(scene))
            );

            scene.setRoot(registration.createRegistrationScreen());
        });

        // SEARCH DONORS
        searchButton.setOnAction(e -> {

            SearchDonorController search =
                new SearchDonorController();

            search.setBackAction(() ->
                scene.setRoot(createDashboardScreen(scene))
            );

            scene.setRoot(search.createSearchScreen());
        });

        // BLOOD REQUIREMENTS
        requirementsButton.setOnAction(e -> {

            BloodRequirementController requirement =
                new BloodRequirementController();

            requirement.setNavigation(
                new BloodRequirementController.SceneHolder() {

                    @Override
                    public void showDonorMatching(String requestId) {

                        DonorMatchingController matching =
                            new DonorMatchingController();

                        matching.setBackAction(() ->
                            scene.setRoot(createDashboardScreen(scene))
                        );

                        scene.setRoot(
                            matching.createMatchingScreen(requestId)
                        );
                    }

                    @Override
                    public void goToDashboard() {

                        scene.setRoot(
                            createDashboardScreen(scene)
                        );
                    }
                }
            );

            scene.setRoot(
                requirement.createRequirementScreen()
            );
        });

        // DONOR MATCHING
        matchingButton.setOnAction(e -> {

            DonorMatchingController matching =
                new DonorMatchingController();

            matching.setBackAction(() ->
                scene.setRoot(createDashboardScreen(scene))
            );

            scene.setRoot(
                matching.createMatchingScreen()
            );
        });

        // REQUEST HISTORY
        historyButton.setOnAction(e -> {

            RequestHistoryController history =
                new RequestHistoryController();

            history.setBackAction(() ->
                scene.setRoot(createDashboardScreen(scene))
            );

            scene.setRoot(
                history.createHistoryScreen()
            );
        });

        // REPORTS
        reportsButton.setOnAction(e -> {

            ReportsController reports =
                new ReportsController();

            reports.setBackAction(() ->
                scene.setRoot(createDashboardScreen(scene))
            );

            scene.setRoot(
                reports.createReportsScreen()
            );
        });

        // LOGOUT
        logoutButton.setOnAction(e -> {

            LoginController login =
                new LoginController();

            scene.setRoot(
                login.createLoginScreen()
            );
        });

        card.getChildren().addAll(
            logo,
            heading,
            heartDivider,
            welcome,
            subtitle,
            menuGrid,
            logoutButton
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

    // MENU BUTTON DESIGN
    private Button createMenuButton(
        String text,
        boolean primary
    ) {

        Button button = new Button(text);

        button.setPrefWidth(280);
        button.setPrefHeight(48);

        button.setFont(
            Font.font("Arial", FontWeight.BOLD, 13)
        );

        if (primary) {

            button.setStyle(
                "-fx-background-color: #A00030;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10 20 10 20;"
            );

        } else {

            button.setStyle(
                "-fx-background-color: #F3E5E9;" +
                "-fx-text-fill: #700019;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E7A0B1;" +
                "-fx-border-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10 20 10 20;"
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

    // HEART DIVIDER
    private Label createHeartDivider() {

        Label divider = new Label(
            "━━━━━━━━━━━━ ♥ ━━━━━━━━━━━━"
        );

        divider.setFont(Font.font("Arial", 20));
        divider.setTextFill(Color.web("#B00035"));

        return divider;
    }

    // BACKGROUND DECORATIONS
    private Pane createDecorations() {

        Pane pane = new Pane();

        pane.setMouseTransparent(true);

        // BLOOD DROPS
        addBloodDrop(pane, 170, 170, 35);
        addBloodDrop(pane, 1050, 470, 45);

        // ECG LINES
        addECG(pane, 0, 340);
        addECG(pane, 900, 340);

        // BLOOD BAG
        addBloodBag(pane, 1030, 130);

        // BOTTOM WAVES
        addWaves(pane);

        return pane;
    }

    // BLOOD DROP DECORATION
    private void addBloodDrop(
        Pane pane,
        double x,
        double y,
        double size
    ) {

        Polygon drop = new Polygon(
            x, y - size,
            x - size * 0.55, y + size * 0.15,
            x - size * 0.45, y + size * 0.65,
            x, y + size,
            x + size * 0.45, y + size * 0.65,
            x + size * 0.55, y + size * 0.15
        );

        drop.setFill(Color.web("#E00045", 0.35));

        pane.getChildren().add(drop);
    }

    // ECG HEARTBEAT DECORATION
    private void addECG(
        Pane pane,
        double x,
        double y
    ) {

        Polyline ecg = new Polyline(
            x, y,
            x + 70, y,
            x + 90, y - 20,
            x + 110, y + 35,
            x + 135, y - 65,
            x + 155, y,
            x + 260, y
        );

        ecg.setStroke(Color.web("#FF8DA8", 0.55));
        ecg.setStrokeWidth(4);
        ecg.setFill(Color.TRANSPARENT);

        pane.getChildren().add(ecg);
    }

    // BLOOD BAG DECORATION
    private void addBloodBag(
        Pane pane,
        double x,
        double y
    ) {

        Rectangle bag = new Rectangle(
            x, y, 75, 110
        );

        bag.setArcWidth(15);
        bag.setArcHeight(15);
        bag.setFill(Color.web("#F7B6C5", 0.55));
        bag.setStroke(Color.web("#FF8DA8", 0.8));
        bag.setStrokeWidth(3);

        Rectangle top = new Rectangle(
            x + 25, y - 12, 25, 12
        );

        top.setFill(Color.web("#FF8DA8", 0.7));

        Line tube = new Line(
            x + 37, y + 110,
            x + 37, y + 145
        );

        tube.setStroke(Color.web("#FF8DA8", 0.8));
        tube.setStrokeWidth(3);

        Rectangle crossVertical = new Rectangle(
            x + 32, y + 28, 12, 45
        );

        Rectangle crossHorizontal = new Rectangle(
            x + 15, y + 44, 45, 12
        );

        crossVertical.setFill(Color.web("#A00030", 0.8));
        crossHorizontal.setFill(Color.web("#A00030", 0.8));

        pane.getChildren().addAll(
            bag,
            top,
            tube,
            crossVertical,
            crossHorizontal
        );
    }

    // BOTTOM WAVES
    private void addWaves(Pane pane) {

        pane.getChildren().addAll(
            createWave(650, "#8B0025"),
            createWave(675, "#C00040"),
            createWave(695, "#F2A6B8")
        );
    }

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

    // INFORMATION ALERT
    private void showInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
