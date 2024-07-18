package pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.model;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.util.stream.Stream;


public class MainModel {
    private HashMap<String, List<String>> dictionaries;
    private List<String> availableLanguages;
    private static final int WORDS_IN_PARAGRAPH = 3;
    private String selectedLanguage;
    private List<WordStatistic> wordStatistics;
    private int totalKeyPresses;
    private int correctKeyPresses;
    private List<Double> rawWpmData;
    private List<Double> adjustedWpmData;
    private List<Integer> errorsOverTime;
    private int totalWordsTyped;
    private int totalErrors;


    public MainModel() {
        dictionaries = new HashMap<>();
        availableLanguages = loadAvailableLanguages();
        loadAllDictionaries();
        wordStatistics = new ArrayList<>();
        rawWpmData = new ArrayList<>();
        adjustedWpmData = new ArrayList<>();
        errorsOverTime = new ArrayList<>();
        totalKeyPresses = 0;
        correctKeyPresses = 0;
        totalWordsTyped = 0;
        totalErrors = 0;
    }

    private List<String> loadAvailableLanguages() {
        try (Stream<Path> paths = Files.list(Paths.get("dictionary"))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString().replaceFirst("[.][^.]+$", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private void loadAllDictionaries() {
        for (String language : availableLanguages) {
            loadDictionary(language);
        }
    }

    private void loadDictionary(String language) {
        try {
            List<String> words = Files.readAllLines(Paths.get("dictionary/" + language + ".txt"));
            dictionaries.put(language, words);
        } catch (IOException e) {
            e.printStackTrace();
            dictionaries.put(language, List.of());
        }
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public String getRandomParagraph() {
        if (selectedLanguage == null || !dictionaries.containsKey(selectedLanguage)) {
            return "Please select a language.";
        }
        List<String> words = dictionaries.get(selectedLanguage);
        Random random = new Random();
        return Stream.generate(() -> words.get(random.nextInt(words.size())))
                .limit(WORDS_IN_PARAGRAPH)
                .collect(Collectors.joining(" "));
    }

    public void setSelectedLanguage(String language) {
        this.selectedLanguage = language;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void updateWordStatistic(String word, long timeTaken, boolean isCorrect) {
        wordStatistics.add(new WordStatistic(word, timeTaken, isCorrect));
    }


    // Inner class to represent statistics for each word
    public static class WordStatistic {
        private String word;
        private long timeTaken;
        private boolean isCorrect;

        public WordStatistic(String word, long timeTaken, boolean isCorrect) {
            this.word = word;
            this.timeTaken = timeTaken;
            this.isCorrect = isCorrect;
        }

        public String getWord() { return word; }
        public long getTimeTaken() { return timeTaken; }
        public boolean isCorrect() { return isCorrect; }
    }

    public double calculateWPMForWord(String word, long timeTakenMillis) {
        double timeTakenMinutes = timeTakenMillis / 60000.0;
        return (word.length() / 5.0) / timeTakenMinutes; // general formula
    }

    public void recordWordTyped(String word, long timeTakenMillis) {
        double wpmForWord = calculateWPMForWord(word, timeTakenMillis);
        rawWpmData.add(wpmForWord);

        double adjustedWpmForWord = Math.max(0, wpmForWord - totalErrors);
        adjustedWpmData.add(adjustedWpmForWord);

        totalWordsTyped++;
    }

    public List<Double> getRawWpmData() {
        return rawWpmData;
    }

    public List<Double> getAdjustedWpmData() {
        return adjustedWpmData;
    }

    public List<Integer> getErrorsOverTime() {
        return errorsOverTime;
    }

    public void recordKeyPress(boolean isCorrect) {
        totalKeyPresses++;
        if (isCorrect)
            correctKeyPresses++;
    }

    public double calculateAccuracy() {
        if (totalKeyPresses == 0) {
            return 0; // Prevent division by zero
        }
        return (double) correctKeyPresses / totalKeyPresses * 100;
    }

    public void resetStatistics() {
        rawWpmData.clear();
        adjustedWpmData.clear();
        totalWordsTyped = 0;
        totalErrors = 0;
        totalKeyPresses = 0;
        correctKeyPresses = 0;
    }

    public void generateResultFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "TypingTestResult_" + timestamp + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (WordStatistic stat : wordStatistics) {
                double wpm = calculateWPMForWord(stat.getWord(), stat.getTimeTaken());
                String line = stat.getWord() + " -> " + String.format("%.2f", wpm) + "wpm\n";

                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCorrectKeyPresses() {
        return correctKeyPresses;
    }

    public int getIncorrectKeyPresses() {
        return totalKeyPresses - correctKeyPresses;
    }

    public double calculateAverageRawWPM() {
        if (rawWpmData.isEmpty()) {
            return 0.0;
        }
        double totalRawWPM = 0.0;
        for (double wpm : rawWpmData) {
            totalRawWPM += wpm;
        }
        return totalRawWPM / rawWpmData.size();
    }

    public double calculateAverageAdjustedWPM() {
        if (adjustedWpmData.isEmpty()) {
            return 0.0;
        }
        double totalAdjustedWPM = 0.0;
        for (double wpm : adjustedWpmData) {
            totalAdjustedWPM += wpm;
        }
        return totalAdjustedWPM / adjustedWpmData.size();
    }


}




