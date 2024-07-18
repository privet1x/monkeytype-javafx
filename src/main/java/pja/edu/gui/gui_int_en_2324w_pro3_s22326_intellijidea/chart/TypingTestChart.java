package pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.chart;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import java.util.List;

public class TypingTestChart {
    private final LineChart<Number, Number> lineChart;
    private final XYChart.Series<Number, Number> rawWpmSeries;
    private final XYChart.Series<Number, Number> adjustedWpmSeries;
    private final XYChart.Series<Number, Number> errorsSeries;

    public TypingTestChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);

        rawWpmSeries = new XYChart.Series<>();
        rawWpmSeries.setName("Raw WPM");

        adjustedWpmSeries = new XYChart.Series<>();
        adjustedWpmSeries.setName("Adjusted WPM");

        errorsSeries = new XYChart.Series<>();
        errorsSeries.setName("Errors");

        lineChart.getData().addAll(rawWpmSeries, adjustedWpmSeries, errorsSeries);
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    public void updateChartData(List<Double> rawWpmData, List<Double> adjustedWpmData, List<Integer> errorsData) {
        rawWpmSeries.getData().clear();
        adjustedWpmSeries.getData().clear();
        errorsSeries.getData().clear();

        // Iterates over the raw WPM data.
        for (int i = 0; i < rawWpmData.size(); i++) {
            rawWpmSeries.getData().add(new XYChart.Data<>(i, rawWpmData.get(i)));
            adjustedWpmSeries.getData().add(new XYChart.Data<>(i, adjustedWpmData.get(i)));

            if (errorsData.contains(i)) {
                errorsSeries.getData().add(new XYChart.Data<>(i, rawWpmData.get(i)));
                errorsSeries.getData().get(errorsSeries.getData().size() - 1).setNode(createErrorIndicator());
            }
        }
    }

    private Node createErrorIndicator() {
        StackPane stackPane = new StackPane();
        Line line1 = new Line(-5, -5, 5, 5);
        Line line2 = new Line(-5, 5, 5, -5);
        stackPane.getChildren().addAll(line1, line2);
        return stackPane;
    }
}
