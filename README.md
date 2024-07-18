# Typing Efficiency Application

## Overview

This application is a one-window typing efficiency tool inspired by "Monkeytype". It allows users to test their typing speed and precision, displaying detailed statistics and offering various functionalities such as language selection, test duration selection, and keyboard shortcuts.

## Features

- **Language Selection:** Users can choose from a dynamically generated list of languages based on provided text files.
- **Test Duration Selection:** Options include 15, 20, 45, 60, 90, 120, or 300 seconds.
- **Typing Test:** Users type a displayed paragraph of 30 randomly selected words from the chosen language's dictionary.
- **Real-time Feedback:**
    - Gray for untyped characters.
    - Green for correctly typed characters.
    - Red for incorrectly typed characters.
    - Orange for extra characters.
    - Black for missed characters.
- **Keyboard Shortcuts:**
    - `Tab + Enter`: Restart test.
    - `Ctrl + Shift + P`: Pause test.
    - `Esc`: End test.
- **Animation:** Custom animations including jumping letters to create a 'wave' effect and two additional animations on window elements.
- **Statistics:** Display of current and average Words Per Minute (WPM) broken down into successive seconds.
- **Data Export:** After each test, a file is created in the project directory containing words and their corresponding WPM for that test session.
- **Exception Handling:** Graceful handling of exceptions with user-friendly error messages.
- **Scalability:** Designed with scalability of the application window in mind.
- **Technology:** Implemented using JavaFX and following the MVC (Model-View-Controller) architectural pattern.

## Usage

- Clone the repository and open it in an IDE that supports JavaFX.
- Build and run the application.
- Select a language and test duration.
- Type the displayed paragraph, observing color-coded feedback.
- Use keyboard shortcuts to control the test (Tab + Enter to restart, Ctrl + Shift + P to pause, Esc to end).
- Review detailed statistics and export results after completion.

## Dependencies

- JavaFX (included with JDK 8 and later)


## License

This project is licensed under the [MIT License](LICENSE).
