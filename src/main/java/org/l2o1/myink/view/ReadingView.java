/*-----------------------------
 Date : 25/03/2025
 Auteur : Arnaud MIRALLES
 Classe ReadingView
 Version 1.0 : Squelette de base + bouton retour Menu
----------------------------- */

package org.l2o1.myink.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.l2o1.myink.model.util.BookNode;
import org.l2o1.myink.model.util.BookObject;
import org.l2o1.myink.model.util.PageLink;
import org.l2o1.myink.model.util.PageNode;
import org.l2o1.myink.presenter.ReadingPresenter;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.List;

public class ReadingView {

    // --- Attributs de la View ---

    @FXML
    private Label titleLabel;

    @FXML
    private Pane pageContainer;

    @FXML
    private Label nextPage;

    @FXML
    private ImageView redKeyImage;

    @FXML
    private ImageView blueKeyImage;

    @FXML
    private ImageView greenKeyImage;

    @FXML
    private ImageView pinkKeyImage;

    @FXML
    private ImageView yellowKeyImage;

    private ReadingPresenter presenter;
    private String title = null;

    // --- Méthodes FXML (onClick, Event Listeners, ...) ---

    /**
     * Méthode appelée automatiquement par JavaFX lors de l'initialisation du FXML
     */
    @FXML
    private void initialize() {
        System.out.println("[ReadingView.java] initialize(Reading)");
    }

    @FXML
    private void goNextPage(MouseEvent e) throws IOException {
        System.out.println("[ReadingView.java] goNextPage()");

        if (this.presenter.currentNode.getChoix().isEmpty()) {
            goMenu(e);
        } else {
            // Pas de "besoin d'objet pour les pages sans choix"
            PageLink link = this.presenter.currentNode.getChoix().getFirst();
            this.presenter.currentNode = link.getDestination();
            loadPage();
        }
    }

    // Méthodes changements Scene

    /**
     * Charge la page de menu
     * @param e Event
     * @throws IOException Si le FXML du menu est incorrect
     */
    @FXML
    private void goMenu(MouseEvent e) throws IOException {
        System.out.println("[ReadingView.java] goMenu()");

        // On sauvegarde avant
        this.presenter.saveBook();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/l2o1/myink/view/MenuViewTemplate.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    // --- Méthodes utilitaires ---

    private void loadPage() {
        // On met a jour l'inventaire
        this.presenter.inventory.addObject(this.presenter.currentNode.getObjectReceived());
        this.updateInventoryPane();

        // Page 1
        if (this.presenter.currentNode instanceof BookNode) {
            nextPage.setVisible(true);

            pageContainer.getChildren().clear();
            pageContainer.getChildren().add(this.createCoverPage());


        } else { // Autres pages
            // Page à 1 choix
            if (this.presenter.currentNode.getChoix().size() <= 1) {
                nextPage.setVisible(true);

                pageContainer.getChildren().clear();
                pageContainer.getChildren().add(this.createPagePane());

            } else { // Page à 2, 3, 4 choix
                nextPage.setVisible(false);

                pageContainer.getChildren().clear();
                pageContainer.getChildren().add(this.createPagePane());
            }
        }
    }

    private void updateInventoryPane() {
        this.redKeyImage.setOpacity(0.15);
        this.blueKeyImage.setOpacity(0.15);
        this.greenKeyImage.setOpacity(0.15);
        this.pinkKeyImage.setOpacity(0.15);
        this.yellowKeyImage.setOpacity(0.15);

        if (presenter.inventory.containsObject(new BookObject("red-key.png"))) {
            this.redKeyImage.setOpacity(1);
        }

        if (presenter.inventory.containsObject(new BookObject("blue-key.png"))) {
            this.blueKeyImage.setOpacity(1);
        }

        if (presenter.inventory.containsObject(new BookObject("green-key.png"))) {
            this.greenKeyImage.setOpacity(1);
        }

        if (presenter.inventory.containsObject(new BookObject("pink-key.png"))) {
            this.pinkKeyImage.setOpacity(1);
        }

        if (presenter.inventory.containsObject(new BookObject("yellow-key.png"))) {
            this.yellowKeyImage.setOpacity(1);
        }
    }

    private Pane createCoverPage() {
        Pane pagePane = new Pane();
        pagePane.setLayoutX(0);
        pagePane.setLayoutY(0);
        pagePane.setPrefHeight(700.0);
        pagePane.setPrefWidth(497.0);
        pagePane.setStyle("-fx-background-color: #FFFFFF;");

        // ImageView
        ImageView posterView = new ImageView();
        posterView.setFitHeight(399.0);
        posterView.setFitWidth(325.0);
        posterView.setLayoutX(86.0);
        posterView.setLayoutY(50.0);
        posterView.setPickOnBounds(true);

        // Image de couverture
        Image posterImage = new Image(new File(this.presenter.getCover()).toURI().toString());
        posterView.setImage(posterImage);

        // Label : titre du livre
        Label titleLabel = new Label(this.presenter.title);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(86.0);
        titleLabel.setLayoutY(479.0);
        titleLabel.setPrefHeight(42.0);
        titleLabel.setPrefWidth(325.0);
        titleLabel.setWrapText(true);
        titleLabel.setFont(Font.font("Arial Bold", 24.0));

        // Label : auteur
        Label authorLabel = new Label(((BookNode) this.presenter.currentNode).getAuthor());
        authorLabel.setAlignment(Pos.CENTER);
        authorLabel.setLayoutX(86.0);
        authorLabel.setLayoutY(524.0);
        authorLabel.setPrefHeight(42.0);
        authorLabel.setPrefWidth(325.0);
        authorLabel.setWrapText(true);
        authorLabel.setFont(Font.font("Arial", 18.0));
        authorLabel.setStyle("-fx-text-fill: #989898;");

        // Label : pageNumberLabel
        Label pageNumberLabel = new Label(String.valueOf(this.presenter.currentNode.getPageNumber()));
        pageNumberLabel.setAlignment(Pos.CENTER);
        pageNumberLabel.setLayoutX(236.0);
        pageNumberLabel.setLayoutY(670.0);
        pageNumberLabel.setPrefHeight(25.0);
        pageNumberLabel.setPrefWidth(25.0);
        pageNumberLabel.setFont(new Font("Arial", 11.0));

        // Ajout des éléments au whitePane
        pagePane.getChildren().addAll(posterView, pageNumberLabel, titleLabel, authorLabel);

        return pagePane;
    }

    private Pane createPagePane() {
        // Conteneur principal
        Pane pagePane = new Pane();
        pagePane.setLayoutX(0);
        pagePane.setLayoutY(0);
        pagePane.setPrefHeight(700.0);
        pagePane.setPrefWidth(497.0);
        pagePane.setStyle("-fx-background-color: #FFFFFF;");

        InlineCssTextArea pageContent = new InlineCssTextArea();
        pageContent.setEditable(false);
        pageContent.setLayoutX(25.0);
        pageContent.setLayoutY(25.0);
        pageContent.setPrefHeight(500.0);
        pageContent.setPrefWidth(453.0);
        pageContent.setWrapText(true);

        pageContent.getStyleClass().add("text-area");

        //Appliquer le texte
        pageContent.getStylesheets().add(getClass().getResource("/org/l2o1/myink/css/text-area-no-style.css").toExternalForm());
        pageContent.replaceText(this.presenter.currentNode.getText());

        // Appliquer le style
        // Appliquer les styles CSS si présents
        String serializedStyles = this.presenter.currentNode.getStyleData();
        if (serializedStyles != null && !serializedStyles.isEmpty()) {
            StyleSpans<String> styleSpans = EditingView.deserializeStyleSpans(serializedStyles);
            if (styleSpans != null) {
                pageContent.setStyleSpans(0, styleSpans);
            }
        }

        // Pane contenant les choix
        Pane choicesPane = new Pane();
        choicesPane.setLayoutX(25.0);
        choicesPane.setLayoutY(550.0);
        choicesPane.setPrefHeight(125.0);
        choicesPane.setPrefWidth(453.0);

        // Génération des choix
        Pane[] choicePanes = new Pane[this.presenter.currentNode.getChoix().size()];
        double[] xPositions = {15, 241, 15, 241};
        double[] yPositions = {10, 10, 72, 72};

        for (int i = 0; choicePanes.length > 1 && i < Math.min(choicePanes.length, 4); i++) {
            PageLink choix = this.presenter.currentNode.getChoix().get(i);

            Pane choicePane = new Pane();
            choicePane.setLayoutX(xPositions[i]);
            choicePane.setLayoutY(yPositions[i]);
            choicePane.setPrefHeight(42.0);
            choicePane.setPrefWidth(196.0);
            choicePane.setStyle("-fx-background-radius: 20;");
            choicePane.getStyleClass().add("button-pane");
            choicePane.getStylesheets().add(getClass().getResource("/org/l2o1/myink/css/choice-button.css").toExternalForm());
            choicePane.setCursor(Cursor.HAND);

            if (choix.getBesoinObjet() == null || this.presenter.inventory.containsObject(choix.getBesoinObjet())) {
                choicePane.setOnMouseClicked(e -> {
                    this.presenter.inventory.removeObject(choix.getBesoinObjet());
                    try {
                        this.goNextPage(e, choix.getDestination());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } else {
                // Si on n'a pas l'objet dans son inventaire
                choicePane.setOpacity(0.15);
            }

            ImageView icon = new ImageView();
            icon.setFitHeight(30.0);
            icon.setFitWidth(30.0);
            icon.setLayoutX(10.0);
            icon.setLayoutY(6.0);
            icon.setPickOnBounds(true);
            icon.setPreserveRatio(true);

            // Si on a besoin d'un objet, mettre l'image de l'objet
            if (choix.getBesoinObjet() != null) {
                icon.setImage(new Image(new File(this.presenter.getBookObjectPath(this.presenter.title, choix.getBesoinObjet().getNom())).toURI().toString()));
            }

            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setLayoutX(42.0);
            label.setPrefHeight(42.0);
            label.setPrefWidth(125.0);
            label.setStyle("-fx-text-fill: FFFFFF;");
            label.setFont(Font.font("Arial", 10.0));
            label.setWrapText(true);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setText(choix.getText());

            choicePane.getChildren().addAll(icon, label);
            choicesPane.getChildren().add(choicePane);
            choicePanes[i] = choicePane;
        }

        // Label numéro de page
        Label pageNumberLabel = new Label(String.valueOf(this.presenter.currentNode.getPageNumber()));
        pageNumberLabel.setLayoutX(236.0);
        pageNumberLabel.setLayoutY(670.0);
        pageNumberLabel.setPrefHeight(25.0);
        pageNumberLabel.setPrefWidth(25.0);
        pageNumberLabel.setAlignment(Pos.CENTER);
        pageNumberLabel.setFont(Font.font("Arial", 11.0));

        // Ajout des enfants au conteneur principal
        pagePane.getChildren().addAll(pageContent, choicesPane, pageNumberLabel);

        // Se positionner tout en haut de la page
        Platform.runLater(() -> {
            pageContent.moveTo(0);
            pageContent.requestFollowCaret();
        });

        return pagePane;
    }

    private void goNextPage(MouseEvent e, PageNode page) throws IOException {
        System.out.println("[ReadingView.java] goNextPage()");

        if (this.presenter.currentNode.getChoix().isEmpty()) {
            goMenu(e);
        } else {
            this.presenter.currentNode = page;
            loadPage();
        }
    }

    public void loadData(String title) {
        System.out.println("[ReadingView.java] loadData(" + title + ")");

        this.setTitle(title);

        // On instancie la view au presenter
        this.presenter = new ReadingPresenter(this, this.title);

        // Quand tous les traitements sont faits (à la place de initialize)
        // Le premier noeud est dans this.presenter.currentNode
        this.loadPage();
    }

    /**
     * Setter de title (utilisé lors du changement de Scene
     * @param title nouvelle valeur de title
     */
    public void setTitle(String title) {
        this.title = title;
        this.titleLabel.setText(this.title);
    }

}
