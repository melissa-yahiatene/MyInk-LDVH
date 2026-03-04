/*-----------------------------
 Date : 25/03/2025
 Auteur : Arnaud MIRALLES
 Classe ReadingView
 Version 1.0 : Squelette de base + bouton retour Menu
----------------------------- */

package org.l2o1.myink.view;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.l2o1.myink.model.util.*;
import org.l2o1.myink.presenter.EditingPresenter;
import org.l2o1.myink.view.util.Arrow;
import org.l2o1.myink.view.util.PathTextField;

import java.io.*;
import java.util.*;

public class EditingView
{
    // --- Attributs de la View ---

    /** Pane principal de l'application, contenant tous les éléments graphiques. */
    @FXML
    private Pane appPane;

    /** Pane situé à droite de l'écran, contenant les outils ou options supplémentaires. */
    @FXML
    private Pane rightPane;

    /** Label affichant le titre du livre actuellement édité. */
    @FXML
    private Label titleLabel;

    /** Image affichant si le graphe est valide ou non. */
    @FXML
    private ImageView graphValidityImage;

    /** Label décrivant la validité du graphe (valide/invalide). */
    @FXML
    private Label graphValidityLabel;

    /** Pane contenant l'affichage de la page en cours (contenu textuel). */
    @FXML
    private Pane pageContainer;

    /** Pane représentant la "pool" de nœuds, c’est-à-dire la zone où tous les nœuds sont affichés. */
    @FXML
    private Pane nodePoolPane;

    /** Pane utilisée pour les actions de déplacement ou "drag" d’un nœud. */
    @FXML
    private Pane dragPane;

    /** Pane déclenchant la création d’une nouvelle page classique. */
    @FXML
    private Pane newPagePane;

    /** Pane déclenchant la création d’une nouvelle page de fin. */
    @FXML
    private Pane newEndPagePane;

    /** Pane permettant de créer un nouveau lien entre deux nœuds. */
    @FXML
    private Pane newLinkPane;

    /** Pane activant le mode suppression (nœuds, liens, objets). */
    @FXML
    private Pane deletePane;

    /** Pane associée à l’ajout d’un objet "clé rouge" à un nœud ou un lien. */
    @FXML
    private Pane redKeyPane;

    /** Pane associée à l’ajout d’un objet "clé bleue" à un nœud ou un lien. */
    @FXML
    private Pane blueKeyPane;

    /** Pane associée à l’ajout d’un objet "clé verte" à un nœud ou un lien. */
    @FXML
    private Pane greenKeyPane;

    /** Pane associée à l’ajout d’un objet "clé rose" à un nœud ou un lien. */
    @FXML
    private Pane pinkKeyPane;

    /** Pane associée à l’ajout d’un objet "clé jaune" à un nœud ou un lien. */
    @FXML
    private Pane yellowKeyPane;

    /**Zone de texte avec prise en charge du style CSS en ligne, utilisée pour l'édition du contenu de la page.*/
    @FXML
    private InlineCssTextArea pageContent;

    /**Bouton permettant d'appliquer un style en gras au texte sélectionné dans la zone de texte.*/
    @FXML
    private Button boldButton;

    /**Bouton permettant d'appliquer un style en italique au texte sélectionné dans la zone de texte. */
    @FXML
    private Button italicButton;

    /** Menu déroulant permettant de sélectionner une couleur pour le texte ou le fond dans la zone de texte.*/
    @FXML
    private ComboBox<String> colorPicker;

    /** Menu déroulant permettant de sélectionner la police de caractère pour le texte dans la zone de texte. */
    @FXML
    public ComboBox<String> fontPicker;




    /** Présenteur associé à cette vue, gérant la logique métier de l'édition. */
    private EditingPresenter presenter;
    /** Titre du livre actuellement en cours d'édition. */
    private String title = null;
    /** Indique si une minuterie de sauvegarde automatique est active. */
    private boolean isSaveTimerActive = false;
    /** Image de fond représentant un motif pointillé (pour l’éditeur de graphe). */
    private ImageView dottedBackground;
    /** Label affichant les coordonnées actuelles de la caméra dans l’espace du graphe. */
    private Label posLabel;
    /** Liste des nœuds considérés comme invalides (par exemple, sans lien de sortie). */
    private List<PageNode> invalidNodes;
    /** Pane représentant l'action sélectionnée actuellement (drag, suppression, lien, etc.). */
    private Pane selectedActionPane;
    /** Représente le panneau actuellement sélectionné pour un mode actif dans l'interface utilisateur.*/
    private Pane currentlySelectedModePane;


    // Attributs visibilité du plan

    /** Largeur de la vue affichant le graphe. */
    private static final int WIDTH = 675;
    /** Hauteur de la vue affichant le graphe. */
    private static final int HEIGHT = 700;
    /** Taille (diamètre) d’un nœud affiché sur la vue. */
    private static final int NODE_SIZE = 50;
    /** Position X actuelle du "décalage caméra" dans le graphe. */
    private double currentX = 0;
    /** Position Y actuelle du "décalage caméra" dans le graphe. */
    private double currentY = 0;
    /** Coordonnée X de la souris au moment où l’utilisateur commence un drag. */
    private double mouseAnchorX;
    /** Coordonnée Y de la souris au moment où l’utilisateur commence un drag. */
    private double mouseAnchorY;
    /** Position X initiale du nœud déplacé, en coordonnées absolues du graphe. */
    private int draggedNodeStartX;
    /** Position Y initiale du nœud déplacé, en coordonnées absolues du graphe. */
    private int draggedNodeStartY;
    /** Indique si un nœud est actuellement en cours de déplacement. */
    private boolean isDraggingNode = false;
    /** Nœud actuellement déplacé par l’utilisateur. */
    private PageNode draggedPageNode;
    /** Pane graphique associé au nœud actuellement déplacé. */
    private Pane draggedPane;


    // Attributs pour la gestion de création et/ou suppression de liens, noeuds, etc.
    private PageNode firstSelectedNode=null;

    // --- Méthodes FXML (onClick, Event Listeners, ...) ---

    /**
     * Méthode appelée automatiquement par JavaFX lors de l'initialisation du FXML
     */
    @FXML
    private void initialize() {
        System.out.println("[EditingView.java] initialize(Editing)");
    }

    // Méthodes changements Scene

    /**
     * Charge la page de menu
     * @param e Event
     * @throws IOException Si le FXML du menu est incorrect
     */
    @FXML
    private void goMenu(MouseEvent e) throws IOException {
        System.out.println("[EditingView.java] goMenu()");

        // On sauvegarde avant
        this.presenter.saveBook();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/l2o1/myink/view/MenuViewTemplate.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    /**
     * Centre automatiquement l’affichage sur le premier nœud invalide du graphe.
     */
    @FXML
    private void seeInvalidNode() {
        if (!this.invalidNodes.isEmpty()) {
            System.out.println("[EditingView.java] seeInvalidNode()");

            PageNode invalidNode = this.invalidNodes.getFirst();
            Tuple<Integer, Integer> invalidPos = this.presenter.pool.getNodePos(invalidNode);
            int x = invalidPos.getE1();
            int y = invalidPos.getE2();
            this.currentX = x - (WIDTH / 2.0) + 25;
            this.currentY = y - (HEIGHT / 2.0) + 25;
            this.displayNodePool();
        }
    }

    // --- Méthodes utilitaires ---
    /**
     * Affiche visuellement tous les nœuds présents dans le graphe du livre.
     * Affiche aussi les liens (flèches) entre les pages, les objets, et les erreurs.
     */
    private void displayNodePool() {
        System.out.println("[EditingView.java] displayNodePool()");

        this.nodePoolPane.getChildren().clear();
        this.nodePoolPane.getChildren().add(this.dottedBackground);

        // On regarde si notre graphe est valide, et on récupère la liste des noeuds à problème
        this.invalidNodes = this.presenter.pool.checkValidity();
        this.updateValidity();

        for (Tuple<PageNode, Tuple<Integer, Integer>> nodeTuple: this.presenter.pool.getAllNodes()) {
            PageNode node = nodeTuple.getE1();
            Tuple<Integer, Integer> pos = nodeTuple.getE2();

            // On modifie X et Y par rapport aux coordonnées actuelles du plan
            double x = pos.getE1() - this.currentX;
            double y = pos.getE2() - this.currentY;

            // On vérifie si le noeud est visible (dans le plan)
            if ((x > -NODE_SIZE && x < WIDTH) && (y > -NODE_SIZE && y < HEIGHT)) {

                // On ajoute le noeud
                this.nodePoolPane.getChildren().add(createNodePane(node, x, y));

                // S'il y a un ou des choix, on ajoute la ou les flèches
                for (PageLink link: node.getChoix()) {
                    PageNode destNode = link.getDestination();
                    BookObject needObject = link.getBesoinObjet();
                    Tuple<Integer, Integer> destPos = this.presenter.pool.getNodePos(destNode);
                    double destX = destPos.getE1() - this.currentX;
                    double destY = destPos.getE2() - this.currentY;

                    this.nodePoolPane.getChildren().add(createArrow(node, link, needObject, x, y, destX, destY, node.getChoix().size() > 1));
                }
            }
        }

        // On ajoute le Label des coordonnées
        this.posLabel.setText("X = " + (int) this.currentX + " Y = " + (int) this.currentY);
        this.nodePoolPane.getChildren().add(this.posLabel);
    }
    /**
     * Charge la page actuelle dans la zone de lecture/édition.
     */
    private void loadPage() {

        this.pageContainer.getChildren().clear();

        // Page 1
        if (this.presenter.currentNode instanceof BookNode) {
            this.pageContainer.getChildren().add(this.createFirstPage());

        } else { // Autres pages
            this.pageContainer.getChildren().add(this.createPagePane());

        }
    }
    /**
     * Affiche une fenêtre d'information contenant un message à l’utilisateur.
     *
     * @param message Le texte à afficher dans la boîte de dialogue.
     */
    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Erreur !");
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Crée et retourne un Pane représentant un nœud (page) dans le graphe.
     *
     * @param node Le nœud à représenter.
     * @param x Position X (relative à la vue).
     * @param y Position Y (relative à la vue).
     * @return Un Pane contenant la représentation graphique du nœud.
     */
    private Pane createNodePane(PageNode node, double x, double y) {
        Pane nodePane = new Pane();
        nodePane.setLayoutX(x);
        nodePane.setLayoutY(y);
        nodePane.setPrefSize(50.0, 50.0);
        nodePane.setCursor(Cursor.HAND);

        // Cercle
        Circle circle = new Circle(25.0);
        circle.setLayoutX(25.0);
        circle.setLayoutY(25.0);
        circle.setFill(Color.WHITE);
        circle.setStrokeType(StrokeType.INSIDE);
        if (node == this.firstSelectedNode) { // Si c'est le premier noeud pour relier un lien
            circle.setStroke(Color.GREENYELLOW);
        } else if (node == this.presenter.currentNode) { // Noued qu'on focus
            circle.setStroke(Color.web("#388eff"));
        } else if (this.invalidNodes.contains(node)) { // Si le noeud est problématique
            circle.setStroke(Color.RED);
        } else {
            circle.setStroke(Color.web("#737373"));
        }

        // Image
        ImageView flagIcon = new ImageView();
        flagIcon.setFitWidth(30.0);
        flagIcon.setFitHeight(30.0);
        flagIcon.setLayoutX(10.0);
        flagIcon.setLayoutY(5.0);
        flagIcon.setPickOnBounds(true);
        flagIcon.setPreserveRatio(true);
        if (node instanceof BookNode) {
            flagIcon.setImage(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/green-flag.png").toExternalForm()));

        } else if (node.getEnd()) { // Si c'est la fin du livre
            flagIcon.setImage(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/red-flag.png").toExternalForm()));

        } else if (node.getObjectReceived() != null) {
            String objectPath = this.presenter.getBookObjectPath(this.presenter.title, node.getObjectReceived().getNom());
            flagIcon.setImage(new Image(new File(objectPath).toURI().toString()));
            flagIcon.setOnMouseClicked(e -> {
                e.consume();

                if (this.selectedActionPane == this.deletePane) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cet objet ?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Suppression de l'objet");
                    if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                        this.presenter.pool.deleteObjectNode(node);
                        this.presenter.saveBook();

                        this.selectedActionPane = this.dragPane;
                        this.updateToolbar();

                        this.displayNodePool();
                    }

                } else if (this.selectedActionPane == this.newLinkPane) {
                    this.newLinkEventContent(node);
                }
            });
        }

        // Label numéro de page
        Label label = new Label(String.valueOf(node.getPageNumber()));
        label.setAlignment(Pos.CENTER);
        label.setLayoutY(35.0);
        label.setPrefWidth(50.0);
        label.setPrefHeight(10.0);
        label.setFont(Font.font("Arial", 11.0));

        // Ajout dans le Pane
        nodePane.getChildren().addAll(circle, flagIcon, label);

        // Events Listeners
        nodePane.setOnMouseClicked(e -> {
            e.consume();

            // Clic sur le noeud en mode drag (affichage du noeud dans la page)
            if (this.selectedActionPane == this.dragPane) {
                this.presenter.currentNode = node;
                this.loadPage();
                this.displayNodePool();
            }

            else if (this.selectedActionPane == this.deletePane) {
                circle.setStroke(Color.RED); //  Bordure rouge en mode suppression
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce nœud ?", ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Suppression de nœud");
                if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    if(node instanceof BookNode)
                        showMessage("Impossible de supprimer cette page.");
                    else{
                        this.presenter.pool.deletePageNode(node);
                        this.presenter.saveBook();
                    }
                }

                this.displayNodePool();

            } else if (this.selectedActionPane == this.newLinkPane) {

                this.newLinkEventContent(node);

            } else if (this.selectedActionPane == this.redKeyPane || this.selectedActionPane == this.blueKeyPane || this.selectedActionPane == this.greenKeyPane || this.selectedActionPane == this.pinkKeyPane || this.selectedActionPane == this.yellowKeyPane) {
                if (this.selectedActionPane == this.redKeyPane) {
                    this.presenter.pool.addObjectNode(node, new BookObject("red-key.png"));

                } else if (this.selectedActionPane == this.blueKeyPane) {
                    this.presenter.pool.addObjectNode(node, new BookObject("blue-key.png"));

                } else if (this.selectedActionPane == this.greenKeyPane) {
                    this.presenter.pool.addObjectNode(node, new BookObject("green-key.png"));

                } else if (this.selectedActionPane == this.pinkKeyPane) {
                    this.presenter.pool.addObjectNode(node, new BookObject("pink-key.png"));

                } else if (this.selectedActionPane == this.yellowKeyPane) {
                    this.presenter.pool.addObjectNode(node, new BookObject("yellow-key.png"));

                }

                this.presenter.saveBook();

                this.displayNodePool();
            }
        });

        // Début du drag sur un noeud
        nodePane.setOnMousePressed(e -> {
            e.consume();

            if (this.selectedActionPane == this.dragPane) {

                // D'abord on affiche le noeud dans la page
                this.presenter.currentNode = node;
                this.loadPage();

                this.mouseAnchorX = e.getSceneX();
                this.mouseAnchorY = e.getSceneY();

                // On stocke la position réelle du nœud
                Tuple<Integer, Integer> realPos = this.presenter.pool.getNodePos(node);
                this.draggedNodeStartX = realPos.getE1();
                this.draggedNodeStartY = realPos.getE2();

                nodePane.setCursor(Cursor.CLOSED_HAND);

                this.isDraggingNode = true;
                this.draggedPageNode = node;
                this.draggedPane = nodePane;
            }
        });

        return nodePane;
    }
    /**
     * Crée une flèche entre deux nœuds du graphe.
     *
     * @param sourceNode Nœud de départ.
     * @param link Lien à représenter.
     * @param needObject Objet requis pour suivre le lien (ou null).
     * @param sourceX Position X du départ.
     * @param sourceY Position Y du départ.
     * @param destX Position X de destination.
     * @param destY Position Y de destination.
     * @param isMultipleChoice Vrai si le nœud a plusieurs choix (changera la couleur).
     * @return Un Group JavaFX contenant la flèche et éventuellement une icône.
     */
    public Group createArrow(PageNode sourceNode, PageLink link, BookObject needObject, double sourceX, double sourceY, double destX, double destY, boolean isMultipleChoice) {
        // Centre du cercle de départ
        double fromCenterX = sourceX + 25.0;
        double fromCenterY = sourceY + 25.0;

        // Centre du cercle d'arrivée
        double toCenterX = destX + 25.0;
        double toCenterY = destY + 25.0;

        // Vecteur direction
        double dx = toCenterX - fromCenterX;
        double dy = toCenterY - fromCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) distance = 0.0001; // Évite la division par zéro

        // Rayon du cercle
        double radius = 25.0;

        // Point de sortie depuis le premier cercle
        double startX = fromCenterX + (dx / distance) * radius;
        double startY = fromCenterY + (dy / distance) * radius;

        // Point d'entrée dans le deuxième cercle
        double endX = toCenterX - (dx / distance) * radius;
        double endY = toCenterY - (dy / distance) * radius;

        // Création de la flèche
        Group arrowGroup = new Group();
        Arrow arrow = new Arrow(startX, startY, endX, endY);
        arrowGroup.getChildren().add(arrow);

        // Epaisseur de la flèche
        arrow.setStrokeWidth(5);

        // Couleur de la flèche selon si il existe plusieurs choix pour ce noeud
        if (isMultipleChoice) {
            arrow.setFill(Color.web("#7949FF"));
        } else {
            arrow.setFill(Color.web("#267DFF"));
        }

        // Event Listener pour la flèche
        arrow.setOnMouseClicked(e -> {
            e.consume();

            // Suppression de la flèche
            if (this.selectedActionPane == this.deletePane) {
                arrow.setFill(Color.RED);
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce lien ?", ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Suppression de lien");
                if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    this.presenter.pool.deleteLink(sourceNode, link);

                    this.presenter.saveBook();
                }


                this.displayNodePool();
            }

            // Ajout d'objet sur la flèche
            else if (this.selectedActionPane == this.redKeyPane || this.selectedActionPane == this.blueKeyPane || this.selectedActionPane == this.greenKeyPane || this.selectedActionPane == this.pinkKeyPane || this.selectedActionPane == this.yellowKeyPane) {
                boolean ajout;
                if (this.selectedActionPane == this.redKeyPane) {
                    ajout = this.presenter.pool.addObjectLink(sourceNode, link, new BookObject("red-key.png"));

                } else if (this.selectedActionPane == this.blueKeyPane) {
                    ajout = this.presenter.pool.addObjectLink(sourceNode, link, new BookObject("blue-key.png"));

                } else if (this.selectedActionPane == this.greenKeyPane) {
                    ajout = this.presenter.pool.addObjectLink(sourceNode, link, new BookObject("green-key.png"));

                } else if (this.selectedActionPane == this.pinkKeyPane) {
                    ajout = this.presenter.pool.addObjectLink(sourceNode, link, new BookObject("pink-key.png"));

                } else {
                    ajout = this.presenter.pool.addObjectLink(sourceNode, link, new BookObject("yellow-key.png"));

                }

                if (!ajout) {
                    showMessage("Impossible d'ajouter un objet à un lien unique !");
                }

                this.presenter.saveBook();

                this.displayNodePool();
            }
        });

        // Objet au milieu de la flèche
        if (needObject != null) {
            // Centre de la flèche
            double midX = (startX + endX) / 2;
            double midY = (startY + endY) / 2;

            // Création de l’image
            String objectPath = this.presenter.getBookObjectPath(this.presenter.title, needObject.getNom());
            ImageView icon = new ImageView(new Image(new File(objectPath).toURI().toString()));
            icon.setFitWidth(40.0);
            icon.setFitHeight(40.0);

            // Décalage pour centrer l’image sur le point
            icon.setLayoutX(midX - 20.0);
            icon.setLayoutY(midY - 20.0);

            // Event Listener pour l'image
            icon.setOnMouseClicked(e -> {
                e.consume(); // empêche la flèche elle-même de réagir à ce clic

                if (this.selectedActionPane == this.deletePane) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cet objet ?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Suppression de l'objet");
                    if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {

                        this.presenter.pool.deleteObjectLink(sourceNode, link);

                        this.presenter.saveBook();

                        this.selectedActionPane = this.dragPane;
                        this.updateToolbar();

                        this.displayNodePool();
                    }
                }
            });

            arrowGroup.getChildren().add(icon);
        }

        // Curseur en mode main
        arrowGroup.setCursor(Cursor.HAND);

        return arrowGroup;
    }
    /**
     * Met à jour l'affichage de la validité du graphe du livre.
     */
    private void updateValidity() {
        if (this.presenter.pool.getBookGraph().getIsValid()) {
            graphValidityImage.setImage(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/valid.png").toExternalForm()));
            graphValidityLabel.setText("Graphe valide");
        } else {
            graphValidityImage.setImage(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/invalid.png").toExternalForm()));
            graphValidityLabel.setText("Graphe invalide");
            Tooltip.install(graphValidityLabel, new Tooltip("Un graphe est invalide s'il contient\ndes noeuds inatteignables,\nou des noeuds sans choix\nqui ne sont pas des\nnoeuds de fin\nSinon il est valide."));
        }
    }
    /**
     * Active ou désactive un mode en fonction du panneau passé en paramètre,en mettant à jour l'interface utilisateur et le mode sélectionné.
     * @param paneToActivate Le panneau à activer comme mode actuel.
     */
    private void setMode(Pane paneToActivate) {
        // On met le firstSelectedNode à null
        this.firstSelectedNode = null;

        // Si le mode est déjà actif, on le désactive
        if (currentlySelectedModePane == paneToActivate) {
            // Désactivation du mode
            currentlySelectedModePane.getStyleClass().remove("disabled");
            currentlySelectedModePane = null;
            selectedActionPane = dragPane; // Retour au mode normal
            updateToolbar();
        } else {
            // Sinon, on active le nouveau mode et on désactive l'ancien mode
            if (currentlySelectedModePane != null) {
                currentlySelectedModePane.getStyleClass().remove("disabled");
            }

            currentlySelectedModePane = paneToActivate;
            currentlySelectedModePane.getStyleClass().add("disabled"); // Marquer comme désactivé
            selectedActionPane = paneToActivate; // Mettre à jour le mode sélectionné
            updateToolbar();
        }

        // On met à jour l'affichage du NodePool
        this.displayNodePool();
    }


    /**
     * Initialise les événements associés aux outils de la barre d’outils.
     * Gère les clics sur les différentes actions disponibles (déplacement, création, suppression, etc.).
     */
    private void initializeToolbarEvents() {
        // Les actions classiques
        this.dragPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.dragPane;
            setMode(dragPane);
            //this.updateToolbar();
        });
        this.newPagePane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.newPagePane;
            setMode(newPagePane);
            //this.updateToolbar();
        });
        this.newEndPagePane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.newEndPagePane;
            setMode(newEndPagePane);
            //this.updateToolbar();
        });
        this.newLinkPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.newLinkPane;
            setMode(newLinkPane);
            this.firstSelectedNode = null;
            //this.updateToolbar();
        });
        this.deletePane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.deletePane;
            setMode(deletePane);
            //this.updateToolbar();
        });

        // Les actions ajout objets
        this.redKeyPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.redKeyPane;
            setMode(redKeyPane);
            //this.updateToolbar();
        });
        this.blueKeyPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.blueKeyPane;
            setMode(blueKeyPane);
            //this.updateToolbar();
        });
        this.greenKeyPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.greenKeyPane;
            setMode(greenKeyPane);
            //this.updateToolbar();
        });
        this.pinkKeyPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.pinkKeyPane;
            setMode(pinkKeyPane);
            //this.updateToolbar();
        });
        this.yellowKeyPane.setOnMouseClicked(e -> {
            //this.selectedActionPane = this.yellowKeyPane;
            setMode(yellowKeyPane);
            //this.updateToolbar();
        });

        Tooltip.install(deletePane, new Tooltip("Supprimer un Noeud,\nun Lien ou une Clé"));
        Tooltip.install(newLinkPane, new Tooltip("Ajouter un Lien"));
        Tooltip.install(newPagePane, new Tooltip("Ajouter un Noeud"));
        Tooltip.install(newEndPagePane, new Tooltip("Ajouter un Noeud de Fin"));
        Tooltip.install(dragPane, new Tooltip("Mode Curseur"));
        Tooltip.install(yellowKeyPane, new Tooltip("Ajouter une Clé Jaune"));
        Tooltip.install(blueKeyPane, new Tooltip("Ajouter une Clé Bleue"));
        Tooltip.install(redKeyPane, new Tooltip("Ajouter une Clé Rouge"));
        Tooltip.install(greenKeyPane, new Tooltip("Ajouter une Clé Verte"));
        Tooltip.install(pinkKeyPane, new Tooltip("Ajouter une Clé Rose"));
    }

    /**
     * Met à jour l’apparence des outils dans la barre d’outils en fonction de l’outil sélectionné.
     * Ajoute ou retire la classe CSS "selected" pour chaque outil.
     */
    private void updateToolbar() {
        Pane[] toolPanes = {this.dragPane, this.newPagePane, this.newEndPagePane, this.newLinkPane, this.deletePane,
                this.redKeyPane, this.blueKeyPane, this.greenKeyPane, this.pinkKeyPane, this.yellowKeyPane};

        for (Pane toolPane: toolPanes) {
            if (toolPane == this.selectedActionPane) {
                if (!toolPane.getStyleClass().contains("selected")) {
                    toolPane.getStyleClass().add("selected");
                }
            } else {
                toolPane.getStyleClass().remove("selected");
            }
        }
    }

    /**
     * Crée l’interface de la première page du livre, incluant l’image de couverture,
     * les champs de modification du titre et de l’auteur.
     *
     * @return Pane contenant les éléments de la première page.
     */
    private Pane createFirstPage() {
        System.out.println("[EditingView.java] createFirstPage()");

        Pane innerPane = new Pane();
        innerPane.setLayoutX(14);
        innerPane.setLayoutY(25);
        innerPane.setPrefSize(497, 700);
        innerPane.setStyle("-fx-background-color: #FFFFFF;");

        // Image principale
        ImageView imageView = new ImageView(new Image(new File(this.presenter.getCover()).toURI().toString()));
        imageView.setFitHeight(399);
        imageView.setFitWidth(325);
        imageView.setLayoutX(86);
        imageView.setLayoutY(50);
        imageView.setPickOnBounds(true);
        innerPane.getChildren().add(imageView);

        // Bouton "Modifier couverture"
        Pane buttonPane = new Pane();
        buttonPane.setLayoutX(150);
        buttonPane.setLayoutY(479);
        buttonPane.setPrefSize(196, 42);
        buttonPane.setStyle("-fx-background-radius: 20;");
        buttonPane.getStyleClass().add("button-pane");
        buttonPane.getStylesheets().add(getClass().getResource("/org/l2o1/myink/css/choice-button.css").toExternalForm());
        buttonPane.setCursor(Cursor.HAND);
        buttonPane.setOnMouseClicked(e -> {
            // Création du sélecteur de fichiers
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Fichier de couverture");

            // Ajouter des filtres de types de fichiers
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );

            // Afficher la fenêtre système de sélection de fichier
            File selectedFile = fileChooser.showOpenDialog(((Node) e.getSource()).getScene().getWindow());

            // Vérifier si un fichier a été choisi
            if (selectedFile != null) {
                this.presenter.copyFile(selectedFile, new File(this.presenter.getCover()));
                try {
                    this.refreshUI();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Label buttonLabel = new Label("Modifier couverture");
        buttonLabel.setAlignment(javafx.geometry.Pos.CENTER);
        buttonLabel.setLayoutX(42);
        buttonLabel.setPrefSize(125, 42);
        buttonLabel.setStyle("-fx-text-fill: FFFFFF;");
        buttonLabel.setFont(new Font("Arial", 10));
        buttonPane.getChildren().add(buttonLabel);
        innerPane.getChildren().add(buttonPane);

        // Titre du livre
        Pane titlePane = new Pane();
        titlePane.setLayoutX(98);
        titlePane.setLayoutY(551);

        Label titleLabel = new Label("Titre du livre :");
        titleLabel.setAlignment(javafx.geometry.Pos.CENTER);
        titleLabel.setPrefSize(125, 25);

        PathTextField titleField = new PathTextField(this.presenter.pool.getBookGraph().getTitle());
        titleField.setAlignment(javafx.geometry.Pos.CENTER);
        titleField.setLayoutX(150);
        titleField.setPrefSize(125, 25);
        titleField.setFont(new Font("Arial", 10));

        ImageView validTitle = new ImageView(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/save.png").toExternalForm()));
        validTitle.setFitWidth(25);
        validTitle.setFitHeight(25);
        validTitle.setLayoutX(280);
        validTitle.setPickOnBounds(true);
        validTitle.setPreserveRatio(true);
        validTitle.setCursor(Cursor.HAND);
        validTitle.setOnMouseClicked(e -> {

            if (!this.presenter.title.equals(titleField.getText())) {
                File newPath = new File(this.presenter.getAvailableFilename(titleField.getText()));
                String newTitle = newPath.getName();

                this.presenter.pool.getBookGraph().setTitle(newTitle);

                this.presenter.remaneDir(this.presenter.title, newTitle);
                this.presenter.title = newTitle;
                this.presenter.saveBook();
                try {
                    this.refreshUI();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        titlePane.getChildren().addAll(titleLabel, titleField, validTitle);
        innerPane.getChildren().add(titlePane);

        // Auteur
        Pane authorPane = new Pane();
        authorPane.setLayoutX(98);
        authorPane.setLayoutY(606);

        Label authorLabel = new Label("Auteur :");
        authorLabel.setAlignment(javafx.geometry.Pos.CENTER);
        authorLabel.setPrefSize(125, 25);

        TextField authorField = new TextField(this.presenter.pool.getBookGraph().getAuthor());
        authorField.setAlignment(javafx.geometry.Pos.CENTER);
        authorField.setLayoutX(150);
        authorField.setPrefSize(125, 25);
        authorField.setFont(new Font("Arial", 10));

        ImageView validAuthor = new ImageView(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/save.png").toExternalForm()));
        validAuthor.setFitWidth(25);
        validAuthor.setFitHeight(25);
        validAuthor.setLayoutX(280);
        validAuthor.setPickOnBounds(true);
        validAuthor.setPreserveRatio(true);
        validAuthor.setCursor(Cursor.HAND);
        validAuthor.setOnMouseClicked(e -> {
            this.presenter.pool.getBookGraph().setAuthor(authorField.getText());
            this.presenter.saveBook();
        });

        authorPane.getChildren().addAll(authorLabel, authorField, validAuthor);
        innerPane.getChildren().add(authorPane);

        // Label compteur
        Label counterLabel = new Label(String.valueOf(this.presenter.currentNode.getPageNumber()));
        counterLabel.setLayoutX(236);
        counterLabel.setLayoutY(670);
        counterLabel.setAlignment(javafx.geometry.Pos.CENTER);
        counterLabel.setPrefSize(25, 25);
        counterLabel.setFont(new Font("Arial", 11));
        innerPane.getChildren().add(counterLabel);

        return innerPane;
    }
    /**
     * Applique un style à la sélection de texte dans le composant de texte, si une sélection existe, permettant de modifier l'apparence du texte sélectionné.
     * @param style Le style à appliquer à la sélection (par exemple, une couleur ou une police).
     */
    private void applyStyleToSelection(String style) {
        IndexRange selection = pageContent.getSelection();
        if (selection.getLength() == 0) return;

        pageContent.setStyle(selection.getStart(), selection.getEnd(), style);
    }

    /**
     * Crée une interface d’une page standard (hors première page)avec le contenu texte et les différents choix menant vers d'autres pages.
     *
     * @return Pane contenant les éléments de la page.
     */
    private Pane createPagePane() {
        System.out.println("[EditingView.java] createPagePane()");

        // Conteneur principal
        Pane pagePane = new Pane();
        pagePane.setLayoutX(0);
        pagePane.setLayoutY(0);
        pagePane.setPrefHeight(700.0);
        pagePane.setPrefWidth(497.0);
        pagePane.setStyle("-fx-background-color: #FFFFFF;");

        // Zone de texte
        this.pageContent = new InlineCssTextArea();

        pageContent.setLayoutX(25.0);
        pageContent.setLayoutY(42.0);
        pageContent.setPrefHeight(500.0);
        pageContent.setPrefWidth(453.0);

        pageContent.setWrapText(true);
        pageContent.getStyleClass().add("text-area");
        pageContent.setStyle("-fx-font-size: 13px; -fx-font-family: Arial; -fx-border-color: #cac6c6; -fx-padding: 10px");
        pageContent.replaceText(this.presenter.currentNode.getText());
        pageContent.setOnKeyTyped(e -> {
            this.presenter.currentNode.setText(pageContent.getText());
            this.saveBookWithCooldown();
        });

        StyleSpans<String> loadedStyles = deserializeStyleSpans(((PageNode) this.presenter.currentNode).getStyleData());
        if (loadedStyles != null) {
            pageContent.setStyleSpans(0, loadedStyles);
        }

        HBox formatBar = new HBox();
        formatBar.setLayoutX(13.0);
        formatBar.setLayoutY(3.0);
        formatBar.setSpacing(20.0);
        formatBar.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 4px; -fx-border-color: #eae6e6; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Création des boutons
        boldButton = new Button("B");
        boldButton.setStyle("-fx-font-weight: bold;");

        italicButton = new Button("I");
        italicButton.setStyle("-fx-font-style: italic;");

        colorPicker = new ComboBox<>();
        colorPicker.getItems().addAll("black", "red", "blue", "green", "purple", "cyan", "magenta", "yellow", "brown", "gray", "silver", "pink", "orange", "gold", "royalblue", "greenyellow");
        colorPicker.setValue("black");

        //Affciher la couleur devant le nom de la couleur
        colorPicker.setCellFactory(listView -> new ListCell<String>() {
            private final HBox cellContent = new HBox(10);
            private final Rectangle colorRectangle = new Rectangle(12,12);
            private final Label nameLabel = new Label();

            {
                cellContent.getChildren().addAll(colorRectangle, nameLabel);
                cellContent.setAlignment(Pos.CENTER_LEFT);

            }

            @Override
            protected void updateItem(String colorName, boolean empty) {
                super.updateItem(colorName, empty);
                if (empty || colorName == null) {
                    setGraphic(null);
                } else {
                    colorRectangle.setFill(Paint.valueOf(colorName));
                    nameLabel.setText(colorName);
                    setGraphic(cellContent);
                }
            }
        });

        colorPicker.setButtonCell(new ListCell<String>() {
            private final HBox cellContent = new HBox(10);
            private final Rectangle colorRectangle = new Rectangle(12,12);
            private final Label nameLabel = new Label();

            {
                nameLabel.setTextFill(Color.BLACK);
                cellContent.getChildren().addAll(colorRectangle, nameLabel);
                cellContent.setAlignment(Pos.CENTER_LEFT);

            }

            @Override
            protected void updateItem(String colorName, boolean empty) {
                super.updateItem(colorName, empty);
                if (empty || colorName == null) {
                    setGraphic(null);
                } else {
                    colorRectangle.setFill(Paint.valueOf(colorName));
                    nameLabel.setText(colorName);
                    setGraphic(cellContent);
                }
            }
        });

        ComboBox<String> fontPicker = new ComboBox<>();
        fontPicker.getItems().addAll("Arial", "Courier New", "Times New Roman", "helvetica", "Constantia", "Comic Sans MS", "Calibri", "French Script MT", "Georgia", "Algerian");
        fontPicker.setValue("Arial"); // valeur par défaut


        ComboBox<String> fontSizePicker = new ComboBox<>();
        fontSizePicker.getItems().addAll("10", "12", "14", "16", "18", "20", "24");
        fontSizePicker.setValue("12");
        fontSizePicker.setPrefWidth(60);


        // Ajout des boutons dans la barre
        formatBar.getChildren().addAll(boldButton, italicButton, colorPicker, fontPicker, fontSizePicker);

        //  Liens des actions avec la zone de texte stylisée
        boldButton.setOnAction(e -> {
            toggleStyle("-fx-font-weight: bold;");
            saveTextAndStyle();
        });

        italicButton.setOnAction(e ->{
            toggleStyle("-fx-font-style: italic;");
            saveTextAndStyle();
        });
        colorPicker.setOnAction(e -> {
            String color = colorPicker.getValue();
            toggleStyle("-fx-fill: " + color + ";");
            saveTextAndStyle();
        });
        fontPicker.setOnAction(e -> {
            String font = fontPicker.getValue();
            toggleStyle("-fx-font-family: '" + font + "'");
            saveTextAndStyle();
        });

        fontSizePicker.setOnAction(e -> {
            String size = fontSizePicker.getValue();
            toggleStyle("-fx-font-size: " + size + "px");
            saveTextAndStyle();
        });


        /// Pane contenant les choix
        Pane choicesPane = new Pane();
        choicesPane.setLayoutX(25.0);
        choicesPane.setLayoutY(550.0);
        choicesPane.setPrefHeight(125.0);
        choicesPane.setPrefWidth(453.0);

        // Génération des choix
        Pane[] choicePanes = new Pane[this.presenter.currentNode.getChoix().size()];
        double[] xPositions = {15, 241, 15, 241};
        double[] yPositions = {10, 10, 72, 72};

        for (int i = 0; i < Math.min(choicePanes.length, 4); i++) {
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

            // Texte de choix (modifiable)
            TextField choiceField = new TextField();
            choiceField.setAlignment(Pos.CENTER);
            choiceField.setLayoutX(42.0);
            choiceField.setLayoutY(8.0);
            choiceField.setPrefHeight(25.0);
            choiceField.setPrefWidth(125.0);
            choiceField.setFont(Font.font("Arial", 10.0));
            choiceField.setText(choix.getText());
            choiceField.setOnKeyTyped(e -> {
                choix.setText(choiceField.getText());
                this.saveBookWithCooldown();
            });

            // Cercle + numéro
            Pane circlePane = new Pane();
            circlePane.setLayoutX(170.0);
            circlePane.setLayoutY(11.0);
            circlePane.setPrefSize(20.0, 20.0);

            Circle circle = new Circle(10.0);
            circle.setFill(javafx.scene.paint.Color.WHITE);
            circle.setStroke(javafx.scene.paint.Color.web("#737373"));
            circle.setStrokeType(StrokeType.INSIDE);
            circle.setLayoutX(10.0);
            circle.setLayoutY(10.0);

            Label numberLabel = new Label();
            numberLabel.setPrefSize(20.0, 20.0);
            numberLabel.setAlignment(Pos.CENTER);
            numberLabel.setFont(Font.font("Arial", 11.0));
            numberLabel.setText(String.valueOf(choix.getDestination().getPageNumber()));
            circlePane.getChildren().addAll(circle, numberLabel);

            choicePane.getChildren().addAll(icon, choiceField, circlePane);
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

        pagePane.getChildren().addAll(formatBar, pageContent, choicesPane, pageNumberLabel);


        return pagePane;
    }

    // --- Events Listeners ---
    /**
     * Gère l’événement de création de lien entre deux nœuds.
     * Vérifie si le lien est valide .
     *
     * @param node Le nœud sélectionné pour créer un lien.
     */
    private void newLinkEventContent(PageNode node) {
        // Si on a pas encore choisi le noeud de départ
        if (this.firstSelectedNode == null) {

            this.firstSelectedNode = node;
            this.displayNodePool();

        } else {

            if (this.firstSelectedNode == node) {
                showMessage("Impossible de créer un lien vers le même nœud.");
            } else {
                PageLink link = this.presenter.pool.addLink(this.firstSelectedNode, node);
                if (link == null) {
                    showMessage("Impossible de créer un nouveau lien\n -Max 4 liens par nœud\n -Pas de liens depuis les nœuds de fin\n -Un seul lien depuis le nœud de début");
                }
            }
            // Réinitialisation
            this.firstSelectedNode = null;
            this.displayNodePool();
            this.loadPage();
            this.presenter.saveBook();

        }
    }
    /**
     * Initialise les événements de souris pour le déplacement dans le plan et le repositionnement des nœuds dans le nodePoolPane.
     */
    private void setNodePoolPaneEvents() {
        // Events Listeners sur nodePoolPane

        // Début du drag
        this.nodePoolPane.setOnMousePressed(e -> {
            if (this.selectedActionPane == this.dragPane) {
                this.mouseAnchorX = e.getSceneX();
                this.mouseAnchorY = e.getSceneY();
                this.nodePoolPane.setCursor(Cursor.CLOSED_HAND);
            }
        });

        // Pendant le drag
        this.nodePoolPane.setOnMouseDragged(e -> {
            if (this.selectedActionPane == this.dragPane) {

                // Si on drag le plan
                if (!this.isDraggingNode) {

                    double deltaX = e.getSceneX() - this.mouseAnchorX;
                    double deltaY = e.getSceneY() - this.mouseAnchorY;

                    this.mouseAnchorX = e.getSceneX();
                    this.mouseAnchorY = e.getSceneY();

                    this.currentX -= deltaX;
                    this.currentY -= deltaY;

                    this.displayNodePool();

                } else { // Si on drag un noeud

                    double deltaX = e.getSceneX() - this.mouseAnchorX;
                    double deltaY = e.getSceneY() - this.mouseAnchorY;

                    int newX = (int) (this.draggedNodeStartX + deltaX);
                    int newY = (int) (this.draggedNodeStartY + deltaY);

                    this.presenter.pool.setNodePos(this.draggedPageNode, newX, newY);

                    this.presenter.saveBook();

                    this.displayNodePool();

                }
            }
        });

        // Fin de drag
        this.nodePoolPane.setOnMouseReleased(e -> {
            if (this.selectedActionPane == this.dragPane) {

                // Si on drag le plan
                if (!this.isDraggingNode) {

                    nodePoolPane.setCursor(Cursor.DEFAULT);

                } else { // Si on drag un noeud

                    this.nodePoolPane.setCursor(Cursor.DEFAULT);
                    this.draggedPane.setCursor(Cursor.DEFAULT);
                    this.isDraggingNode = false;

                }
            }
        });
    }

    /**
     * Enregistre automatiquement le livre après un court délai
     */
    private void saveBookWithCooldown() {
        if (!isSaveTimerActive) {
            System.out.println("[EditingView.java] saveBookWithCooldown()");
            isSaveTimerActive = true;
            PauseTransition pause = new PauseTransition(Duration.seconds(3));

            pause.setOnFinished(event -> {
                System.out.println("[EditingView.java] saveBookWithCooldown() --> Saving");
                this.presenter.saveBook();
                isSaveTimerActive = false;
            });
            pause.play();
        }
    }

    /**
     * Méthode permettant de refraîchir l'UI (Changement de titre)
     */
    private void refreshUI() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/l2o1/myink/view/EditingViewTemplate.fxml"));
        Parent root = loader.load();

        EditingView controller = loader.getController();
        controller.loadData(this.presenter.title);

        this.appPane.getChildren().setAll(root);
    }

    /**
     * Charge toutes les données nécessaires pour initialiser l'interface d’édition du livre, à partir de son titre.
     *
     * @param title Le titre du livre à charger.
     */
    public void loadData(String title) {
        System.out.println("[EditingView.java] loadData(" + title + ")");

        this.setTitle(title);

        // On instencie la view au presenter
        this.presenter = new EditingPresenter(this, this.title);

        // On charge le background du plan
        this.dottedBackground = new ImageView();
        this.dottedBackground.setFitHeight(700.0);
        this.dottedBackground.setFitWidth(675.0);
        this.dottedBackground.setPickOnBounds(true);
        this.dottedBackground.setPreserveRatio(false);
        this.dottedBackground.setImage(new Image(getClass().getResource("/org/l2o1/myink/img/Editing/editing-background.png").toExternalForm()));

        // On charge le label de la position actuelle
        this.posLabel = new Label();
        this.posLabel.setLayoutX(515.0);
        this.posLabel.setPrefWidth(160.0);
        this.posLabel.setPrefHeight(30.0);
        this.posLabel.setOpacity(0.73);
        this.posLabel.setStyle("-fx-background-color: #737373; -fx-text-fill: #FFFFFF;");
        this.posLabel.setAlignment(Pos.CENTER);
        this.posLabel.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14.0));

        // On met le nodePoolPane en dessous de tous les autres Panes
        appPane.getChildren().remove(rightPane);
        appPane.getChildren().addFirst(rightPane);
        rightPane.getChildren().remove(nodePoolPane);
        rightPane.getChildren().addFirst(nodePoolPane);

        // On choisi l'action par défaut
        this.selectedActionPane = this.dragPane;

        // Event Listeners du nodePoolPane (pour tout se qui est déplacement plan/noeud)
        this.setNodePoolPaneEvents();
        //Event Listener sur nodePoolPane pour l'ajout de page
        this.nodePoolPane.setOnMouseClicked(e->{
            if(this.selectedActionPane == this.newPagePane) {
                double x = e.getX() + this.currentX - 25;
                double y = e.getY() + this.currentY - 25;

                // Limiter les coordonnées x et y dans les limites de la scène
                x = Math.max(0, Math.min(WIDTH - NODE_SIZE, x));
                y = Math.max(0, Math.min(HEIGHT - NODE_SIZE, y));

                // Ajout de la page
                this.createNewPagePane((int)x, (int)y);
            }
            else if(this.selectedActionPane == this.newEndPagePane) {
                double x = e.getX() + this.currentX - 25;
                double y = e.getY() + this.currentY - 25;

                // Limiter les coordonnées x et y dans les limites de la scène
                x = Math.max(0, Math.min(WIDTH - NODE_SIZE, x));
                y = Math.max(0, Math.min(HEIGHT - NODE_SIZE, y));

                // Ajout de la page de fin
                this.createEndPage((int)x, (int)y);
            }
        });

        // Quand tous les traitements sont faits (à la place de initialize)
        // Le premier noeud est dans this.presenter.currentNode
        this.initializeToolbarEvents();
        //this.updateToolbar();
        this.displayNodePool();
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

    /**
     * Crée une nouvelle page normale à la position spécifiée et l’ajoute au graphe.
     *
     * @param x La position horizontale du nouveau noeud
     * @param y La position verticale du nouveau noeud
     * @return Le nouveau {@link PageNode} créé
     */
    private PageNode createNewPagePane(int x, int y) {
        System.out.println("[EditingView.java] createNewPagePane()");

        // Ajout du nouveau noeud dans le pool
        PageNode newPage = this.presenter.pool.addPageNode(false, x, y);

        // Sauvegarde des modifs
        this.presenter.saveBook();

        // On met à jour currentNode
        this.presenter.currentNode = newPage;

        //On charge la page
        this.loadPage();

        // Mise à jour de l'affichage des noeuds pour rendre visible la nouvelle page
        this.displayNodePool();

        return newPage;
    }

    /**
     * Crée une page de fin (fin possible du livre) à la position donnée.
     * L'ajoute au graphe, met à jour l'affichage, et sélectionne cette page.
     *
     * @param x La position horizontale du noeud de fin
     * @param y La position verticale du noeud de fin
     * @return Le nouveau {@link PageNode} de fin créé
     */
    private PageNode createEndPage(int x, int y) {
        System.out.println("[EditingView.java] createEndPage()");

        PageNode endPage = this.presenter.pool.addPageNode(true, x, y);
        this.presenter.saveBook();

        this.presenter.currentNode = endPage;

        this.loadPage();

        this.displayNodePool();

        return endPage;
    }

    /**
     * Sérialise les styles d’un texte en une chaîne
     * Cela permet de sauvegarder les styles (gras, italique, etc.) dans un format persistant.
     *
     * @param styleSpans Les styles à sérialiser.
     * @return Une chaîne représentant la liste des styles, ou null en cas d’erreur.
     */
    private String serializeStyleSpans(StyleSpans<String> styleSpans) {
        List<Pair<Integer, String>> serializableList = new ArrayList<>();
        for (StyleSpan<String> span : styleSpans) {
            serializableList.add(new Pair<>(span.getLength(), span.getStyle()));
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(serializableList);
            oos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Désérialise une chaîne Base64 représentant des styles en un objet StyleSpans<String>.
     * Permet de restaurer les styles appliqués à un texte précédemment sauvegardé.
     *
     * @param base64 La chaîne Base64 à désérialiser.
     * @return L’objet StyleSpans<String> correspondant, ou null en cas d’erreur ou de chaîne vide.
     */
    public static StyleSpans<String> deserializeStyleSpans(String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            List<Pair<Integer, String>> list = (List<Pair<Integer, String>>) ois.readObject();
            StyleSpansBuilder<String> builder = new StyleSpansBuilder<>();
            for (Pair<Integer, String> pair : list) {
                builder.add(pair.getValue(), pair.getKey());
            }
            return builder.create();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void toggleStyle(String toggledStyle) {
        IndexRange range = pageContent.getSelection();
        if (range.getLength() == 0) return;

        String cleanToggled = toggledStyle.trim().replace(";", "");
        boolean isColor = cleanToggled.startsWith("-fx-fill:");

        // Récupérer le style existant de la première position de la sélection
        String existing = pageContent.getStyleOfChar(range.getStart());

        // Si le style est une couleur, on veut d'abord supprimer l'ancienne couleur
        if (isColor) {
            existing = existing == null ? "" : existing;
            existing = existing.replaceAll("(?<=^|;)\\s*-fx-fill:[^;]*", "");  // Supprimer l'ancienne couleur
        }

        // Si le style est déjà appliqué, on le retire, sinon on l'ajoute
        if (existing != null && existing.contains(cleanToggled)) {
            existing = existing.replace(cleanToggled, "").trim();  // Retirer le style
        } else {
            existing = mergeStyle(existing, toggledStyle);  // Ajouter le nouveau style
        }

        // Appliquer le nouveau style à tous les caractères de la sélection
        for (int i = range.getStart(); i < range.getEnd(); i++) {
            pageContent.setStyle(i, i + 1, existing);
        }

        saveTextAndStyle();
    }

    /**
     * Fusionne un style existant avec un nouveau, en évitant les doublons.
     *
     * @param existing Le style déjà appliqué.
     * @param added Le style à ajouter.
     * @return Une chaîne représentant la combinaison des deux styles.
     */
    private String mergeStyle(String existing, String added) {
        Set<String> styles = new LinkedHashSet<>();
        if (existing != null && !existing.isBlank()) {
            styles.addAll(Arrays.asList(existing.trim().split(";")));
        }
        styles.add(added.trim().replace(";", "")); // éviter duplication
        return String.join("; ", styles) + ";";
    }

    /**
     * Sauvegarde le texte et ses styles associés pour la page en cours.
     * Les styles sont sérialisés en Base64 pour être stockés avec le contenu.
     */
    private void saveTextAndStyle() {
        String text = pageContent.getText();
        StyleSpans<String> styles = pageContent.getStyleSpans(0, text.length());
        ((PageNode) this.presenter.currentNode).setText(text);
        ((PageNode) this.presenter.currentNode).setStyleData(serializeStyleSpans(styles));
        this.saveBookWithCooldown();
    }

}
