package com.svalero.examen;

import com.svalero.examen.controller.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {launch();}

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        AppController controller = new AppController();

        loader.setLocation(getClass().getResource("/ui/interfaz_examen.fxml"));
        loader.setController(controller);

        Parent root = loader.load();

        Scene escena = new Scene(root);
        stage.setScene(escena);
        stage.show();
    }
}
