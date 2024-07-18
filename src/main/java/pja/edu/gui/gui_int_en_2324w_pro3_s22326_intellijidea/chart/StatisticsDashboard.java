package pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.chart;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class StatisticsDashboard {
    private final TypingTestChart typingTestChart;
    private final Label rawWpmLabel;
    private final Label adjustedWpmLabel;
    private final Label accuracyLabel;
    private final Label charStatsLabel;
    private final Label testTypeLabel;

    public StatisticsDashboard() {
        typingTestChart = new TypingTestChart();

        rawWpmLabel = new Label("Raw WPM: 0");
        adjustedWpmLabel = new Label("Adjusted WPM: 0");
        accuracyLabel = new Label("Accuracy: 0%");
        charStatsLabel = new Label("Characters: 0/0");
        testTypeLabel = new Label("Test Type: 0s English");

        VBox layout = new VBox(10, rawWpmLabel, adjustedWpmLabel, accuracyLabel, charStatsLabel, testTypeLabel, typingTestChart.getLineChart());
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("dashboard");

        Scene scene = new Scene(layout, 800, 600);
        applyCSS(scene);

        Stage stage = new Stage();
        stage.setTitle("Typing Test Statistics");
        stage.setScene(scene);
        stage.show();
    }

    public void applyCSS(Scene scene) {
        URL stylesheetURL = getClass().getResource("/stats.css");
        if (stylesheetURL != null) {
            scene.getStylesheets().add(stylesheetURL.toExternalForm());
        } else {
            System.err.println("Could not load stats.css");
        }
    }

    public void displayFinalResults(double rawWpm, double adjustedWpm, double accuracy, int correctChars, int incorrectChars, int redundantChars, int missedChars, int testTime, String language) {
        rawWpmLabel.setText(String.format("Raw WPM: %.2f", rawWpm));
        adjustedWpmLabel.setText(String.format("Adjusted WPM: %.2f", adjustedWpm));
        accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
        charStatsLabel.setText(String.format("Characters: %d/%d/%d/%d", correctChars, incorrectChars, redundantChars, missedChars));
        testTypeLabel.setText(String.format("Test Type: %ds %s", testTime, language));
    }

    public TypingTestChart getTypingTestChart() {
        return typingTestChart;
    }
}

