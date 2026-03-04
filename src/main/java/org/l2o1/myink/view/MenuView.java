/*-----------------------------
 Date : 25/03/2025
 Auteur : Arnaud MIRALLES
 Classe MenuView
 Version 1.0 : View complète mais certaines fonctionnalités non fonctionnelles (PDF, Supprimer, ...)
----------------------------- */

package org.l2o1.myink.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import org.l2o1.myink.model.util.BookNode;
import org.l2o1.myink.presenter.MenuPresenter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class MenuView {

    // --- Attributs de la View ---

    private static final int MAX_PER_GRID = 4;
    private static final double GRID_HEIGHT = 350;

    @FXML
    private Pane appPane;

    @FXML
    private Pane mainContainer;

    @FXML
    private GridPane initialGrid;

    private MenuPresenter presenter;
    private GridPane currentGrid;
    private int currentColumn = 1; // Grid qui a encore de la place
    private int gridCount = 1;

    private Pane openedMenu = null; // Menu contextuel du livre actuellement ouvert

    // --- Méthodes FXML (onClick, Event Listeners, ...) ---

    /**
     * Méthode appelée automatiquement par JavaFX lors de l'initialisation du FXML
     */
    @FXML
    private void initialize() {
        System.out.println("[MenuView.java] initialize(Menu)");

        // On instencie la view au presenter
        this.presenter = new MenuPresenter(this);

        this.currentGrid = this.initialGrid;
        this.currentColumn = 1;
        this.gridCount = 1;
        this.openedMenu = null;

        // Event Listener pour clic de la souris
        this.appPane.setOnMouseClicked(e -> {
            // On ferme le menu
            if (this.openedMenu != null) {
                this.openedMenu.setVisible(false);
                this.openedMenu = null;
            }
        });

        // Affichage de tous les livres
        // TODO: Ralentissement/crash si trop de Panes sur la fenêtre --> Créer des pages 1-2-3-...
        ArrayList<BookNode> books = this.presenter.getAllBooksInfos();
        if (books != null) {
            // On trie la liste par ordre de modification/lecture la plus récente
            books.sort(Comparator.comparing(BookNode::getDateLastModif).reversed());

            for (BookNode book: books) {
                Pane pane = createBookPane(book.getTitle(), book.getAuthor(), book.getIsValid());
                addBookPaneToGrid(pane);
            }
        }
    }

    /**
     * Exécuté lors d'un clic sur le Pane nouveau livre
     * @param e Event
     * @throws IOException Si le FXML édition est incorrect
     */
    @FXML
    private void createNewBook(MouseEvent e) throws IOException {
        System.out.println("[MenuView.java] createNewBook()");

        String title = this.presenter.createNewBookFiles();
        goEditing(title, e);
    }

    // --- Méthodes utilitaires ---

    /**
     * Méthode permettant de refraîchir l'UI (Duplication/Suppression de livres)
     */
    private void refreshUI() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/l2o1/myink/view/MenuViewTemplate.fxml"));
        Parent root = loader.load();

        this.appPane.getChildren().setAll(root);
    }

    // Méthodes changements Scene

    /**
     * Charge la page de lecture
     * @param title Titre du livre
     * @param e Event
     * @throws IOException Si le FXML de la lecture est incorrect
     */
    private void goReading(String title, MouseEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/l2o1/myink/view/ReadingViewTemplate.fxml"));
        Parent root = loader.load(); // root = balise englobante principale

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow(); // Stage actuel
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // On récupère le controlleur de la scène pour ajouter le titre du livre (pour le titre en haut de la page)
        ReadingView controller = loader.getController();
        controller.loadData(title);

        stage.show();
    }

    /**
     * Charge la page d'édition
     * @param title Titre du livre
     * @param e Event
     * @throws IOException Si le FXML de l'édition est incorrect
     */
    private void goEditing(String title, MouseEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/l2o1/myink/view/EditingViewTemplate.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // On récupère le controlleur de la scène pour ajouter le titre du livre
        EditingView controller = loader.getController();
        controller.loadData(title);

        stage.show();
    }

    // Méthodes GridPane

    /**
     * Ajoute une Pane dans la Grid actuelle
     * @param bookPane le Pane à ajouter
     */
    private void addBookPaneToGrid(Pane bookPane) {
        // Si ligne déjà remplie
        if (this.currentColumn == MAX_PER_GRID) {
            this.createNewGrid();
        }

        this.currentGrid.add(bookPane, this.currentColumn, 0); // Colonne / Ligne
        this.currentColumn++;
    }

    /**
     * Ajoute une nouvelle Grid si l'autre est pleine
     */
    private void createNewGrid() {
        // Création du nouveau GridPane
        this.currentGrid = new GridPane();
        this.currentGrid.setPrefSize(1200, GRID_HEIGHT);
        this.currentGrid.setHgap(50);
        this.currentGrid.setVgap(50);
        this.currentGrid.setPadding(new Insets(0, 100, 0, 100));

        // Contraintes des colonnes (4 colonnes)
        for (int i = 0; i < MAX_PER_GRID; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(10);
            col.setPrefWidth(100);
            col.setHgrow(Priority.SOMETIMES);
            this.currentGrid.getColumnConstraints().add(col);
        }

        // Contrainte de ligne (1 seule ligne)
        RowConstraints row = new RowConstraints();
        row.setMinHeight(10);
        row.setPrefHeight(30);
        row.setVgrow(Priority.SOMETIMES);
        this.currentGrid.getRowConstraints().add(row);

        // On positionne ce GridPane en dessous des précédents
        this.currentGrid.setLayoutY(this.gridCount * GRID_HEIGHT);
        this.gridCount++;

        // On ajoute du GridPane au conteneur principal
        this.mainContainer.getChildren().add(this.currentGrid);
        this.currentColumn = 0;

        // On met à jour la hauteur du conteneur pour permettre le scroll
        this.mainContainer.setPrefHeight(this.gridCount * GRID_HEIGHT);
    }

    // Méthodes carte de livre

    /**
     * Créé une Pane pour un livre
     * @param title Titre du livre
     * @param author Auteur du livre
     * @param isValid Graphe du livre valide ?
     * @return Pane construit
     */
    private Pane createBookPane(String title, String author, boolean isValid) {
        // Chemin image de couverture
        String imagePath = this.presenter.getCoverPath(title);

        // Pane principale
        Pane root = new Pane();
        root.setPrefSize(212, 300);

        // Pane image + menu
        Pane imagePane = new Pane();
        imagePane.setPrefSize(212, 260);
        // Event pour le clic sur la couverture (lecture ou écriture selon si le livre est valide)
        imagePane.setOnMouseClicked(e -> {
            if (isValid) {
                System.out.println("[MenuView.java] goReading(" + title + ")");
                try {
                    goReading(title, e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.println("[MenuView.java] goEditing(" + title + ")");
                try {
                    goEditing(title, e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Image de couverture
        ImageView cover = new ImageView(new Image(new File(imagePath).toURI().toString()));
        cover.setFitWidth(212);
        cover.setFitHeight(260);
        cover.setPickOnBounds(true);

        // Menu (masqué)
        Pane menu = new Pane();
        menu.setLayoutY(145);
        menu.setPrefSize(212, 75);
        menu.setOpacity(0.85);
        menu.setStyle("-fx-background-color: #FFFFFF;");
        menu.setVisible(false);

        // Éléments du menu
        if (isValid) {
            menu.getChildren().add(
                    this.createMenuItem(getClass().getResource("/org/l2o1/myink/img/Menu/export.png").toExternalForm(), "Exporter en PDF", 0, null, () -> {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        directoryChooser.setTitle("Dossier d'enregistrement");

                        File selectedDir = directoryChooser.showDialog(menu.getScene().getWindow());
                        // Vérifier si un dossier a été choisi
                        if (selectedDir != null) {
                           boolean success = this.presenter.exportToPDF(title, selectedDir.getAbsolutePath());
                            System.out.println("[MenuView.java] ExportPDF(" + title + ", " + selectedDir.getAbsolutePath() + ")");
                            Alert alert;
                            if(success) {
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Export PDF réussi");
                                alert.setHeaderText(null);
                                alert.setContentText("Le livre a bien été exporté en PDF dans :\n" + selectedDir.getAbsolutePath());
                            }
                            else {
                                alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur d'export PDF");
                                alert.setHeaderText(null);
                                alert.setContentText("Une erreur est survenue pendant l'export du livre.");
                            }
                            alert.showAndWait();

                            try {
                                this.refreshUI();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    })
            );
        }

        menu.getChildren().add(
                this.createMenuItem(getClass().getResource("/org/l2o1/myink/img/Menu/duplicate.png").toExternalForm(), "Dupliquer", 25, null, () -> {
                    System.out.println("[MenuView.java] DuplicateBook(" + title + ")");
                    this.presenter.duplicateBookFiles(title);
                    try {
                        refreshUI();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        menu.getChildren().add(
                this.createMenuItem(getClass().getResource("/org/l2o1/myink/img/Menu/delete.png").toExternalForm(), "Supprimer", 50, "#FF0000", () -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce livre ?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Suppression de livre");

                    if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                        System.out.println("[MenuView.java] DeleteBook(" + title + ")");
                        this.presenter.deleteBookFiles(title);
                        try {
                            refreshUI();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
        );

        // Bouton ...
        ImageView moreBtn = new ImageView(new Image(getClass().getResource("/org/l2o1/myink/img/Menu/more.png").toExternalForm()));
        moreBtn.setFitWidth(40);
        moreBtn.setFitHeight(40);
        moreBtn.setLayoutY(220);
        moreBtn.setCursor(Cursor.HAND);

        // Listener qui affiche le menu si on clique sur le bouton ...
        moreBtn.setOnMouseClicked(e -> {
            // Si un autre menu est ouvert, on le ferme
            if (this.openedMenu != null && this.openedMenu != menu) {
                this.openedMenu.setVisible(false);
            }

            // Toggle du menu courant
            boolean visible = !menu.isVisible();
            menu.setVisible(visible);

            // Mémorise le menu ouvert ou null si on vient de le fermer
            this.openedMenu = visible ? menu : null;

            // Empêche le clic de se propager jusqu’à la scène
            e.consume();
        });

        // Autres icônes
        ImageView editIcon = new ImageView(new Image(getClass().getResource("/org/l2o1/myink/img/Menu/edit.png").toExternalForm()));
        editIcon.setFitWidth(40);
        editIcon.setFitHeight(40);
        editIcon.setOnMouseClicked(e -> {
            System.out.println("[MenuView.java] goEditing(" + title + ")");
            try {
                goEditing(title, e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.consume();
        });

        ImageView invalidIcon = new ImageView(new Image(getClass().getResource("/org/l2o1/myink/img/Menu/invalid.png").toExternalForm()));
        invalidIcon.setFitWidth(40);
        invalidIcon.setFitHeight(40);
        invalidIcon.setLayoutX(172);
        if (isValid) {
            invalidIcon.setVisible(false);
        } else {
            Tooltip tooltip = new Tooltip("Graphe invalide\nLecture impossible");
            Tooltip.install(invalidIcon, tooltip);
        }

        // Ajout des enfants dans le Pane image
        imagePane.getChildren().addAll(cover, menu, editIcon, invalidIcon, moreBtn);
        imagePane.setCursor(Cursor.HAND);

        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setPrefSize(212, 20);
        titleLabel.setLayoutY(260);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-background-color: #FFFFFF;");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        // Auteur
        Label authorLabel = new Label(author);
        authorLabel.setPrefSize(212, 20);
        authorLabel.setLayoutY(280);
        authorLabel.setAlignment(Pos.CENTER);
        authorLabel.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #989898;");
        authorLabel.setFont(Font.font("Arial", 11));

        // Ajout final
        root.getChildren().addAll(imagePane, titleLabel, authorLabel);

        return root;
    }

    /**
     * Créé une ligne pour le menu contextuel d'un livre
     * @param iconPath Lien vers l'icône de la ligne
     * @param labelText Texte de la ligne
     * @param layoutY Position Y dans le menu de la ligne
     * @param textColor Couleur du texte
     * @param action Action à exécuter lors du clic
     * @return Pane de la ligne
     */
    private Pane createMenuItem(String iconPath, String labelText, double layoutY, String textColor, Runnable action) {
        Pane pane = new Pane();
        pane.setPrefSize(212, 25);
        pane.setLayoutY(layoutY);
        pane.getStylesheets().add(getClass().getResource("/org/l2o1/myink/css/grey-hover.css").toExternalForm());
        pane.getStyleClass().add("hover-item"); // Dans le fichier CSS

        // Images
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitWidth(25);
        icon.setFitHeight(25);
        icon.setLayoutX(15);
        icon.setPickOnBounds(true);
        icon.setPreserveRatio(true);

        // Labels
        Label label = new Label(labelText);
        label.setLayoutX(55);
        label.setPrefSize(142, 25);
        label.setFont(Font.font("Arial", 13));
        if (textColor != null) {
            label.setStyle("-fx-text-fill: " + textColor + ";");
        }

        // Event Listener
        pane.setOnMouseClicked(e -> {
            action.run();
            e.consume();
        });

        pane.getChildren().addAll(icon, label);
        return pane; // Ligne de menu
    }
}
