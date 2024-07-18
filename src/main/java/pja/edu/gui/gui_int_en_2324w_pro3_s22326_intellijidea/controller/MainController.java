package pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.util.Pair;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.chart.StatisticsDashboard;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.model.MainModel;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.view.MainView;


import java.util.List;
import java.util.Random;

public class MainController {
    private MainView view;
    private MainModel model;
    private List<String> paragraphs;
    private Timeline timer;
    private int timeRemaining;
    private boolean isTestActive;
    private boolean isTestPaused;
    private TextFlow testTextFlow;
    private TextField userInputField;
    private String selectedLanguage;
    private int selectedTestDuration;
    private String currentTestParagraph;
    private long startTime;
    private int lastSpaceIndex;
    private int lastWordEndIndex;
    private StatisticsDashboard statisticsDashboard;
    private int nextCharIndex = 0;


    public MainController(MainView view, TextFlow testTextFlow, TextField userInputField) {
        this.view = view;
        this.model = new MainModel();
        this.testTextFlow = testTextFlow;
        this.userInputField = userInputField;
        this.isTestActive = false;
        this.isTestPaused = false;
        lastSpaceIndex = -1;
        lastWordEndIndex = 0;

        setupLanguageSelection();
        setupListeners();
        setupStartButtonAnimation(view.getStartTestButton());
    }

    private void setupLanguageSelection() {
        ComboBox<String> languageSelector = view.getLanguageSelector();
        languageSelector.setItems(FXCollections.observableArrayList(model.getAvailableLanguages()));

        if (model.getAvailableLanguages().contains("english")) {
            languageSelector.getSelectionModel().select("english");
            model.setSelectedLanguage("english");
        } else {
            String defaultLanguage = model.getAvailableLanguages().get(0);
            model.setSelectedLanguage(defaultLanguage);
            languageSelector.getSelectionModel().selectFirst();
        }
    }

    public void populateLanguageDropdown(ComboBox<String> dropdown) {
        List<String> availableLanguages = model.getAvailableLanguages();
        dropdown.getItems().setAll(availableLanguages);
        dropdown.getSelectionModel().select(model.getSelectedLanguage());
    }

    public void populateDurationDropdown(ComboBox<Integer> dropdown) {
        Integer[] durations = {15, 20, 45, 60, 90, 120, 300};
        dropdown.getItems().addAll(durations);
        selectedTestDuration = durations[0];
        dropdown.getSelectionModel().selectFirst();
    }

    private void setupListeners() {
        handleUserInput(userInputField, testTextFlow);
    }

    public void initializeTestParagraph(TextFlow testTextFlow) {
        testTextFlow.getChildren().clear(); // Clear any previous text nodes

        String paragraph;
        if (paragraphs == null || paragraphs.isEmpty()) {
            paragraph = model.getRandomParagraph(); // Fetch a random paragraph from the model
            if (paragraph.isEmpty()) {
                paragraph = "No paragraphs available"; // Fallback message
            }
        } else {
            Random rand = new Random();
            paragraph = paragraphs.get(rand.nextInt(paragraphs.size()));
        }

        currentTestParagraph = paragraph;

        Text paragraphText = new Text(paragraph);
        paragraphText.setFill(Color.GRAY); // Default text color
        testTextFlow.getChildren().add(paragraphText);
    }

    public void handleUserInput(TextField userInputField, TextFlow testTextFlow) {
        userInputField.textProperty().addListener((obs, oldText, newText) -> {

            updateParagraphDisplay(testTextFlow, newText);

            // Wave animation 1
            //String currentTypingWord = getCurrentTypingWord(newText, currentTestParagraph);
            //applyWaveAnimationToCurrentWord(testTextFlow, currentTypingWord);

            // Wave animation 2
            Pair<String, String> typingWordAndInput = getCurrentTypingWordAndInput(newText, currentTestParagraph);
            applyWaveAnimationToTypedLetters(testTextFlow, typingWordAndInput.getKey(), typingWordAndInput.getValue());

            // For accuracy
            if (newText.length() > oldText.length()) {
                char typedChar = newText.charAt(newText.length() - 1);
                char expectedChar = getCurrentExpectedChar();

                //System.out.println("Typed: " + typedChar + ", Expected: " + expectedChar + ", Index: " + nextCharIndex);

                boolean isCorrect = (typedChar == expectedChar);
                model.recordKeyPress(isCorrect);

                if (isCorrect) {
                    nextCharIndex++; // Update the index for the next expected character
                }

            }


            // Check for word completion (space or end of paragraph)
            if (newText.length() > lastWordEndIndex && (newText.endsWith(" ") || newText.length() == currentTestParagraph.length())) {
                String lastWordTyped = getLastWordTyped(newText);
                long timeTaken = calculateTimeForWord(lastWordTyped);
                boolean isWordCorrect = checkWordCorrectness(lastWordTyped, currentTestParagraph);

                model.recordWordTyped(lastWordTyped, timeTaken);
                model.updateWordStatistic(lastWordTyped, timeTaken, isWordCorrect);

                lastWordEndIndex = newText.length();
                startTime = System.currentTimeMillis();
            }

            // Check for paragraph completion
            if (checkParagraphCompletion(newText, currentTestParagraph)) {
                currentTestParagraph = model.getRandomParagraph();
                updateParagraphAfterCompletion(testTextFlow, currentTestParagraph);
                userInputField.clear();

                lastWordEndIndex = 0;
                startTime = System.currentTimeMillis();
            }
        });
    }
    private char getCurrentExpectedChar() {
        if (nextCharIndex < currentTestParagraph.length()) {
            return currentTestParagraph.charAt(nextCharIndex);
        } else {
            return '\0';
        }
    }


    private void updateParagraphDisplay(TextFlow testTextFlow, String userInput) {
        testTextFlow.getChildren().clear();

        String[] wordsFromParagraph = currentTestParagraph.split("\\s+");
        String[] wordsFromUserInput = userInput.split("\\s+", -1);

        int paragraphWordIndex = 0;
        int userInputWordIndex = 0;

        while (paragraphWordIndex < wordsFromParagraph.length) {
            String paragraphWord = wordsFromParagraph[paragraphWordIndex];
            String userInputWord = userInputWordIndex < wordsFromUserInput.length ? wordsFromUserInput[userInputWordIndex] : "";

            int skippedCharacters = 0;
            int paragraphCharIndex = 0;
            int userInputCharIndex = 0;

            while (paragraphCharIndex < paragraphWord.length()) {
                char paragraphChar = paragraphWord.charAt(paragraphCharIndex);
                Text textNode = new Text(String.valueOf(paragraphChar));
                if (userInputCharIndex < userInputWord.length()) {
                    char userInputChar = userInputWord.charAt(userInputCharIndex);

                    if (userInputChar == paragraphChar) {
                        textNode.setFill(Color.GREEN);
                        userInputCharIndex++;
                    } else if (skippedCharacters < 2 && paragraphCharIndex + 1 < paragraphWord.length() && userInputChar == paragraphWord.charAt(paragraphCharIndex + 1)) {
                        textNode.setFill(Color.BLACK); // Skipped character
                        skippedCharacters++;
                    } else {
                        textNode.setFill(Color.RED);
                        userInputCharIndex++;
                    }
                } else {
                    textNode.setFill(Color.GRAY);
                }

                testTextFlow.getChildren().add(textNode);
                paragraphCharIndex++;
            }

            // Add remaining characters from the user input if they have typed past the current word
            while (userInputCharIndex < userInputWord.length()) {
                char extraChar = userInputWord.charAt(userInputCharIndex);
                Text extraTextNode = new Text(String.valueOf(extraChar));
                extraTextNode.setFill(Color.ORANGE);
                testTextFlow.getChildren().add(extraTextNode);
                userInputCharIndex++;
            }

            if (paragraphWordIndex < wordsFromParagraph.length - 1) {
                testTextFlow.getChildren().add(new Text(" "));
            }

            paragraphWordIndex++;
            userInputWordIndex++;
        }
    }

    public void updateParagraphAfterCompletion(TextFlow testTextFlow, String paragraph) {
        testTextFlow.getChildren().clear();
        Text paragraphText = new Text(paragraph);
        paragraphText.setFill(Color.GRAY);
        testTextFlow.getChildren().add(paragraphText);
    }

    private boolean checkParagraphCompletion(String userInput, String testParagraph) {
        String userInputNoSpaces = userInput.replaceAll("\\s+", "");
        String paragraphNoSpaces = testParagraph.replaceAll("\\s+", "");

        if (userInputNoSpaces.length() > paragraphNoSpaces.length()) {
            userInputNoSpaces = userInputNoSpaces.substring(0, paragraphNoSpaces.length());
        }

        return userInputNoSpaces.length() >= paragraphNoSpaces.length();
    }

    public void restartTest() {
        if (!isTestActive) {
            System.out.println("Test is not active, cannot restart.");
            return;
        }

        if (timer != null) {
            timer.stop();
        }
        timeRemaining = selectedTestDuration;
        startTest(timeRemaining);

        currentTestParagraph = model.getRandomParagraph();
        Platform.runLater(() -> {
            testTextFlow.getChildren().clear();
            view.updateTestParagraph(testTextFlow, currentTestParagraph);
            userInputField.clear();
            view.updateTimerDisplay(timeRemaining);
        });

        lastSpaceIndex = -1;
        startTime = System.currentTimeMillis();
        System.out.println("Test restarted.");
    }

    public void pauseTest() {
        if (isTestActive && !isTestPaused) {
            isTestPaused = true;
            timer.pause();
        } else if (isTestPaused) {
            isTestPaused = false;
            timer.play();
        }
    }

    public void endTest() {
        isTestActive = false;
        isTestPaused = false;
        timer.stop();

        Platform.runLater(() -> view.updateTimerDisplay(selectedTestDuration));

        Platform.runLater(() -> {
            displayStatisticsDashboard();
        });

        model.generateResultFile();
    }

    private void displayStatisticsDashboard() {

        List<Double> rawWpmData = model.getRawWpmData();
        List<Double> adjustedWpmData = model.getAdjustedWpmData();
        List<Integer> errorsData = model.getErrorsOverTime();

        double averageRawWPM = model.calculateAverageRawWPM();
        double averageAdjustedWPM = model.calculateAverageAdjustedWPM();
        int correctChars = model.getCorrectKeyPresses();
        int incorrectChars = model.getIncorrectKeyPresses();
        int redundantChars = model.getIncorrectKeyPresses();
        int missedChars = model.getIncorrectKeyPresses();
        double accuracy = model.calculateAccuracy();

        Platform.runLater(() -> {
            if (statisticsDashboard == null) {
                statisticsDashboard = new StatisticsDashboard();
            }
            statisticsDashboard.getTypingTestChart().updateChartData(rawWpmData, adjustedWpmData,errorsData);
            statisticsDashboard.displayFinalResults(
                    averageRawWPM,
                    averageAdjustedWPM,
                    accuracy,
                    correctChars,
                    incorrectChars,
                    redundantChars,
                    missedChars,
                    getSelectedTestDuration(),
                    model.getSelectedLanguage()
            );
        });
    }

    public void onLanguageSelectionChanged(String newLanguage) {
        model.setSelectedLanguage(newLanguage);
        currentTestParagraph = model.getRandomParagraph();
        Platform.runLater(() -> {
            view.clearUserInput();
            view.updateTestParagraph(view.getTestTextFlow(), currentTestParagraph);
            view.clearUserInput();
        });
    }


    public void onDurationSelectionChanged(int newDuration) {
        selectedTestDuration = newDuration;
    }

    public void setCurrentTestParagraph(String paragraph) {
        this.currentTestParagraph = paragraph;
    }

    private void alertActiveTest() {
        if (isTestActive) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please wait till test is finished.");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    public void startTest(int durationSeconds) {
        timeRemaining = durationSeconds;
        isTestActive = true;
        model.resetStatistics();
        statisticsDashboard = null;
        nextCharIndex = 0;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            Platform.runLater(() -> view.updateTimerDisplay(timeRemaining));

            if (timeRemaining <= 0) {
                timer.stop();
                endTest();
            }
        }));
        timer.setCycleCount(durationSeconds);
        timer.play();

        lastSpaceIndex = -1;
        startTime = System.currentTimeMillis();
    }


    public void startTestFromButton() {
        if (selectedTestDuration <= 0) {
            // Show an error message if no test duration is selected
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a test duration.");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        if(isTestActive)
            alertActiveTest();
        else
            startTest(selectedTestDuration);
    }

    private String getCurrentTypingWord(String userInput, String paragraph) {
        String[] wordsFromParagraph = paragraph.split("\\s+");
        String[] wordsFromUserInput = userInput.split("\\s+", -1);

        int lastWordIndex = wordsFromUserInput.length - 1;
        if (lastWordIndex < 0 || lastWordIndex >= wordsFromParagraph.length) {
            return "";
        }

        return wordsFromParagraph[lastWordIndex];
    }

    // Wave animation 1
    private void applyWaveAnimationToCurrentWord(TextFlow textFlow, String currentWord) {
        int startIndex = currentTestParagraph.indexOf(currentWord);
        int endIndex = startIndex + currentWord.length();

        if (startIndex == -1) return; // Word not found in the paragraph

        for (int i = startIndex; i < endIndex; i++) {
            if (i < textFlow.getChildren().size()) {
                Node node = textFlow.getChildren().get(i);
                if (node instanceof Text) {
                    Text textNode = (Text) node;
                    TranslateTransition jumpUp = new TranslateTransition(Duration.millis(300), textNode);
                    jumpUp.setByY(-10);
                    jumpUp.setAutoReverse(true);
                    jumpUp.setCycleCount(2);
                    jumpUp.play();
                }
            }
        }
    }


    private Pair<String, String> getCurrentTypingWordAndInput(String userInput, String paragraph) {
        String[] wordsFromParagraph = paragraph.split("\\s+");
        String[] wordsFromUserInput = userInput.split("\\s+", -1);

        int lastWordIndex = wordsFromUserInput.length - 1;
        if (lastWordIndex < 0 || lastWordIndex >= wordsFromParagraph.length) {
            return new Pair<>("", "");
        }

        String currentTypingWord = wordsFromParagraph[lastWordIndex];
        String currentUserInputForWord = wordsFromUserInput[lastWordIndex];
        return new Pair<>(currentTypingWord, currentUserInputForWord);
    }

    // Wave animation 2
    private void applyWaveAnimationToTypedLetters(TextFlow textFlow, String currentWord, String userInputForWord) {
        int startIndex = currentTestParagraph.indexOf(currentWord);
        int endIndex = startIndex + userInputForWord.length();
        if (startIndex == -1) return;

        for (int i = startIndex; i < endIndex; i++) {
            if (i < textFlow.getChildren().size()) {
                Node node = textFlow.getChildren().get(i);

                if (node instanceof Text) {
                    Text textNode = (Text) node;

                    TranslateTransition jumpUp = new TranslateTransition(Duration.millis(150), textNode);
                    jumpUp.setByY(-10);

                    jumpUp.setAutoReverse(true);
                    jumpUp.setCycleCount(2);
                    jumpUp.play();
                }
            }
        }
    }


    public void setupLanguageComboBoxAnimation(ComboBox<String> languageDropdown) {
        languageDropdown.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                fadeIn(languageDropdown);
            } else {
                fadeOut(languageDropdown);
            }
        });
    }


    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void fadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.25);
        ft.play();
    }



    private void setupStartButtonAnimation(Button startButton) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), startButton);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.play();
    }

    private int getSelectedTestDuration() {
        return selectedTestDuration;
    }

    // --------------------------- STATISTICS -------------------------------------

    private String getLastWordTyped(String text) {
        String trimmedText = text.trim();
        int lastSpaceIndex = trimmedText.lastIndexOf(' ');
        return lastSpaceIndex == -1 ? trimmedText : trimmedText.substring(lastSpaceIndex + 1);
    }

    private long calculateTimeForWord(String word) {
        return System.currentTimeMillis() - startTime;
    }

    private boolean checkWordCorrectness(String word, String paragraph) {
        String[] words = paragraph.split("\\s+");
        int wordIndex = Math.min(lastWordEndIndex, words.length - 1);
        return words[wordIndex].equals(word);
    }












}


