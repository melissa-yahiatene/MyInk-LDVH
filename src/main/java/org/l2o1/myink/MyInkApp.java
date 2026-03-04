/*-----------------------------
 Date : 25/03/2025
 Auteur : Arnaud MIRALLES
 Classe MyInkApp (main)
 Version 1.0 : Génération de base par IntelliJ + quelques détails (nom, size)
----------------------------- */

package org.l2o1.myink;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MyInkApp extends Application {
    /**
     * Méthode qui redéfinie start()
     * @param stage Stage application principale
     * @throws IOException Si le FXML ne peut pas être chargé
     */
    @Override
    public void start(Stage stage) throws IOException {
        // On commence dans le Menu
        FXMLLoader fxmlLoader = new FXMLLoader(MyInkApp.class.getResource("view/MenuViewTemplate.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        stage.setTitle("MyInk");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResource("/org/l2o1/myink/img/Menu/logo.png").toExternalForm()));


        stage.show();
    }

    /**
     * Méthode lançant le launcher JavaFX
     * @param args Arguments ligne de commande
     */
    public static void main(String[] args) {
        launch();
    }
}