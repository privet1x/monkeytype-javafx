package pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.controller.MainController;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.model.MainModel;
import pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea.view.MainView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        MainModel mainModel = new MainModel();
        MainView mainView = new MainView(mainModel);
        MainController mainController = new MainController(mainView,mainView.getTestTextFlow(),mainView.getUserInputField());
        Scene scene = new Scene(mainView.getRoot(), 800, 600); // Initial window size

        mainView.applyGlobalKeyListeners(scene);
        mainView.applyStyles(scene);
        primaryStage.setTitle("Typing Test Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);



    }

    public static void main(String[] args) {
        launch(args);
    }
}

