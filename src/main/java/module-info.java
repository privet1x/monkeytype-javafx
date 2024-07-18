module pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea to javafx.fxml;
    exports pja.edu.gui.gui_int_en_2324w_pro3_s22326_intellijidea;
}