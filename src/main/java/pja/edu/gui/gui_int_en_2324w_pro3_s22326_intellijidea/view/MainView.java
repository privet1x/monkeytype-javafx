package pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.controller.MainController;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.model.MainModel;

import java.net.URL;

public class MainView {
    private BorderPane root;
    private MainController controller;
    private MainModel model;
    private TextFlow testTextFlow;
    private TextField userInputField;
    private ComboBox<String> languageSelector;
    private Label timerLabel;
    private Button startTestButton;

    public MainView(MainModel model) {
        this.model = model;
        root = new BorderPane();
        testTextFlow = new TextFlow();
        userInputField = new TextField();
        languageSelector = new ComboBox<>();
        controller = new MainController(this, testTextFlow, userInputField);

        this.timerLabel = new Label("00:00");
        this.timerLabel.setId("timerLabel");
        setupLayout();
    }

    private void setupLayout() {
        VBox topPanel = createTopPanel();
        VBox bottomPanel = createBottomPanel();
        HBox typingTestArea = createTypingTestArea();

        HBox topLayout = new HBox();
        topLayout.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(topPanel, Priority.ALWAYS);


        HBox topRightPanel = new HBox(timerLabel);
        topRightPanel.setAlignment(Pos.CENTER_RIGHT);

        topLayout.getChildren().addAll(topPanel, topRightPanel);

        root.setTop(topLayout);

        root.setBottom(bottomPanel);
        root.setCenter(typingTestArea);

        VBox.setVgrow(testTextFlow, Priority.ALWAYS);

        testTextFlow.setMinSize(200, 100);
        userInputField.setMinHeight(50);
    }


    private VBox createTopPanel() {
        VBox topPanel = new VBox(10);
        topPanel.setPadding(new Insets(15, 12, 15, 12));

        ComboBox<String> languageDropdown = new ComboBox<>();
        controller.populateLanguageDropdown(languageDropdown);
        setupLanguageComboBox(languageDropdown);
        Label languageLabel = new Label("Select Language:");
        languageLabel.setLabelFor(languageDropdown);

        ComboBox<Integer> durationDropdown = new ComboBox<>();
        controller.populateDurationDropdown(durationDropdown);

        // Set up animation for languageDropdown
        controller.setupLanguageComboBoxAnimation(languageDropdown);
        setupDurationComboBox(durationDropdown);
        Label durationLabel = new Label("Select Test Duration (seconds):");
        durationLabel.setLabelFor(durationDropdown);

        startTestButton = new Button("Start Test");
        startTestButton.setOnAction(e -> controller.startTestFromButton());


        topPanel.getChildren().addAll(languageLabel, languageDropdown, durationLabel, durationDropdown, startTestButton);
        return topPanel;
    }

    private HBox createTypingTestArea() {
        HBox typingTestArea = new HBox(10);
        typingTestArea.setPadding(new Insets(10, 12, 10, 12));

        TextFlow testTextFlow = new TextFlow();
        testTextFlow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(testTextFlow, Priority.ALWAYS);

        testTextFlow.getStyleClass().add("test-text-flow");

        // Initialize TextFlow with the test paragraph
        controller.initializeTestParagraph(testTextFlow);

        TextField userInputField = new TextField();
        userInputField.setPromptText("Type anything to start");
        userInputField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(userInputField, Priority.SOMETIMES);

        controller.handleUserInput(userInputField, testTextFlow);

        typingTestArea.getChildren().addAll(testTextFlow, userInputField);
        return typingTestArea;
    }


    public void applyStyles(Scene scene) {
        URL stylesheetURL = getClass().getClassLoader().getResource("style.css");
        if (stylesheetURL != null) {
            scene.getStylesheets().add(stylesheetURL.toExternalForm());
            System.out.println("Stylesheet loaded successfully.");
        } else {
            System.err.println("Could not find style.css");
        }
    }


    private VBox createBottomPanel() {
        VBox bottomPanel = new VBox(10); // 10 is the spacing between elements
        bottomPanel.setPadding(new Insets(10, 12, 10, 12));

        Label restartLabel = new Label("Tab + Enter: Restart Test");
        Label pauseLabel = new Label("Ctrl + Shift + P: Pause Test");
        Label endLabel = new Label("Esc: End Test");

        bottomPanel.getChildren().addAll(restartLabel, pauseLabel, endLabel);
        return bottomPanel;
    }

    public void setupLanguageComboBox(ComboBox<String> languageComboBox) {
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                controller.onLanguageSelectionChanged(newVal);

                String newParagraph = model.getRandomParagraph();
                updateTestParagraph(getTestTextFlow(), newParagraph);
                clearUserInput();
            }
        });
    }

    public void setupDurationComboBox(ComboBox<Integer> durationComboBox) {
        durationComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                controller.onDurationSelectionChanged(newVal);
            }
        });
    }

    public BorderPane getRoot() {
        return root;
    }

    public ComboBox<String> getLanguageSelector() {
        return languageSelector;
    }

    public void updateTestParagraph(TextFlow testTextFlow, String paragraph) {
        Platform.runLater(() -> {
            testTextFlow.getChildren().clear();
            Text textNode = new Text(paragraph);
            textNode.setFill(Color.GRAY);
            testTextFlow.getChildren().add(textNode);

            controller.setCurrentTestParagraph(paragraph);
        });
    }


    public void clearUserInput() {
        userInputField.clear();
    }

    public TextFlow getTestTextFlow() {
        return testTextFlow;
    }

    public TextField getUserInputField() {
        return userInputField;
    }

    //converts seconds to a minutes
    public void updateTimerDisplay(int secondsRemaining) {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);
    }

    public void applyGlobalKeyListeners(Scene scene) {
        final boolean[] tabPressed = {false};

        scene.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.TAB) {
                tabPressed[0] = true;
            } else if (tabPressed[0] && event.getCode() == KeyCode.ENTER) {
                System.out.println("Tab + Enter pressed");
                controller.restartTest();
                tabPressed[0] = false; // Reset the flag
            }

            if (event.getCode() == KeyCode.P && event.isControlDown() && event.isShiftDown()) {
                System.out.println("Ctrl + Shift + P pressed");
                controller.pauseTest();
            }

            if (event.getCode() == KeyCode.ESCAPE) {
                System.out.println("Esc pressed");
                controller.endTest();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                tabPressed[0] = false;
            }
        });
    }

    public Button getStartTestButton() {
        return startTestButton;
    }
}

