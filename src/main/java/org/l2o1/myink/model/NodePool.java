/*-----------------------------
Date : 24/03/2025
Auteurs : Asmaou Baldé et Melissa Yahiatene
Début de l'implémentation
----------------------------- */

package org.l2o1.myink.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import org.l2o1.myink.model.FileManager;
import org.l2o1.myink.model.util.*;


public class NodePool implements Serializable {

    // Attributs
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * La liste de tous les noeuds du graphe du livre
     */
    private List<Tuple<PageNode, Tuple<Integer, Integer>>> allNodes;

    /**
     * Le premier noeud du graphe du livre
     */
    private BookNode bookGraph;

    /**
     * La collection d'objets du livre
     */
    private ObjectCollection objectsCollection;

    /**
     * Compteur pour incrémenter le numero de page à chaque création d'une nouvelle page
     */
    private int pageNumberGen = 0;
    private Set<Integer> freedPageNumbers = new HashSet<>();

    /**
     * Constructeur de la classe NodePool.
     * Crée le premier noeud et le deuxieme noeud du graphe du livre
     * @param title le titre du livre.
     */
    public NodePool(String title) {
        this.allNodes = new ArrayList<>();

        // Premier noeud
        BookNode firstNode = new BookNode(title);
        this.bookGraph = firstNode;
        this.allNodes.add(new Tuple<>(firstNode, new Tuple<>(312, 52)));

        // Second noeud
        PageNode secondNode = this.addPageNode(false, 312, 148);
        this.addLink(firstNode, secondNode);

        // Objets utilisés
        List<BookObject> objects = FileManager.getBookObjects(title);
        this.objectsCollection = new ObjectCollection(objects);
    }

    /**
     * Méthode statique qui renvoie un NodePool contenant tous les cas d'utilisations possibles
     * @return NodePool complet et fonctionnel pour les Tests
     */
    public static NodePool getTestNodePool(String title) {
        // Page 0
        org.l2o1.myink.model.NodePool pool = new org.l2o1.myink.model.NodePool(title);
        PageNode sourceNode, destNode;

        // Page 1
        sourceNode = pool.allNodes.get(1).getE1();
        sourceNode.setText("Voici la page 1 !\n\nVous avez obtenu une clé rouge !");
        pool.addObjectNode(sourceNode, new BookObject("red-key.png"));

        // Page 2
        destNode = pool.addPageNode(false, 312, 244);
        destNode.setText("Voici la page 2 !\n\nFaites vos choix...");
        pool.addLink(sourceNode, destNode);

        // Page 3, 4, 5, 6
        sourceNode = destNode;
        for (int i=0; i<4; i++) {
            destNode = pool.addPageNode(true, 125*(i+1), 340);
            destNode.setText("Page " + (i + 3) + "\n\nFin !");
            PageLink link = pool.addLink(sourceNode, destNode);
            link.setText("Choix " + (i + 1));
            if (i == 1) {
                pool.addObjectLink(sourceNode, link, new BookObject("red-key.png"));
            }
            if (i == 2) {
                pool.addObjectLink(sourceNode, link, new BookObject("yellow-key.png"));
            }
        }

        return pool;
    }

    // Getters
    /**
     * Retourne la liste des noeuds du graphe du livre.
     *
     * @return la liste de tous les noeuds appartenant au graphe du livre.
     */
    public List<Tuple<PageNode, Tuple<Integer, Integer>>> getAllNodes()
    {
        return allNodes;
    }

    /**
     * Retourne le premier noeud du livre.
     *
     * @return Le premier noeud désignant le graphe du livre.
     */
    public BookNode getBookGraph() {
        return bookGraph;
    }

    /**
     * Methode pour ajouter un nouveau noeud.
     * @param end qui indique si le nouveau neoud à ajouter est un noeud de fin.
     * @param x,y les coordonnées de l'emplacement du nouveau noeud dans le graphe.
     * @return le noeud créé
     */
    public PageNode addPageNode(boolean end, int x, int y) {
        // Trouver le plus petit numéro libre, soit dans freedPageNumbers, soit en incrémentant
        int newPageNumber = findSmallestAvailablePageNumber();

        PageNode page = new PageNode(end, newPageNumber); // Créer le nouveau noeud
        Tuple<Integer, Integer> tuple = new Tuple<>(x, y); // Créer le tuple des coordonnées du nouveau noeud
        allNodes.add(new Tuple<>(page, tuple)); // Ajouter le tuple du nouveau noeud avec ses coordonnées à l'ensemble des noeuds du graphe

        return page;
    }
    /**
     * Méthode privée qui détermine le plus petit numéro de page disponible dans le graphe.
     * @return le plus petit numéro de page non utilisé, qu’il provienne des numéros libérés
     * ou de l’incrémentation à partir de 1.
     */
    private int findSmallestAvailablePageNumber() {
        // Créer un set des numéros de page déjà utilisés
        Set<Integer> usedPageNumbers = new HashSet<>();
        for (Tuple<PageNode, Tuple<Integer, Integer>> node : allNodes) {
            usedPageNumbers.add(node.getE1().getPageNumber());
        }

        int newPageNumber = -1;

        // Vérifier les numéros libérés triés pour trouver le plus petit non utilisé
        if (!freedPageNumbers.isEmpty()) {
            List<Integer> sortedFreed = new ArrayList<>(freedPageNumbers);
            Collections.sort(sortedFreed);

            for (Integer freed : sortedFreed) {
                if (!usedPageNumbers.contains(freed) && newPageNumber == -1) {
                    newPageNumber = freed; // on garde le premier disponible
                }
            }
        }

        // Si aucun freedPageNumber n'est disponible, on fait une incrémentation classique
        if (newPageNumber == -1) {
            newPageNumber = 1;
            while (usedPageNumbers.contains(newPageNumber)) {
                newPageNumber++;
            }
        }

        return newPageNumber;
    }


//    public PageNode addPageNode(boolean end, int x, int y){
//        PageNode page = new PageNode(end, ++pageNumberGen); //créer le nouveau noeud
//        Tuple<Integer, Integer>tuple = new Tuple<>(x,y); //créer le tuple des coordonnées du nouveau noeud
//        allNodes.add(new Tuple<>(page, tuple)); //ajouter le tuple du nouveau noeud avec ses coordonnées à l'ensemble des noeud du graphe
//
//        return page;
//    }

    /**
     * Methode pour supprimer un noeud indiqué du graphe.
     * @param pageNode le noeud à supprimer.
     */
    public void deletePageNode(PageNode pageNode){
        boolean trouve=false;
        for(int i=0; i<allNodes.size() && !trouve; i++) {

            //Vérifier si le noeud que l'on veut supprimer existe bien dans le graphe (dans l'ensemble des noeuds du graphe)
            if (allNodes.get(i).getE1().equals(pageNode)) {
                trouve = true;

                //Supprimer tous les liens le reliant vers les autres noeuds
                for (PageLink link : pageNode.getChoix()) {
                    PageNode dest = link.getDestination();
                    dest.deleteReference(pageNode);
                }
                for (PageNode ref : pageNode.getReferencedBy()) {
                    ref.supprimerChoix(pageNode);
                }

                freedPageNumbers.add(allNodes.get(i).getE1().getPageNumber()); //ajoute le numéro de page du noeud à supprimer à la liste des numéros libérés
                allNodes.remove(allNodes.get(i)); //supprimer le noeud de l'ensemble des noeuds
            }
        }
        if(!trouve){
            System.out.println("Le noeud selectionné n'existe pas dans l'ensemble des noeuds");
        }

    }

    /**
     * Methode pour créer un lien entre deux noeuds.
     * @param sourceNode le noeud source.
     * @param destNode le noeud destination.
     * @return le lien créé
     */
    public PageLink addLink(PageNode sourceNode, PageNode destNode)
    {
        // Si il y a moins d'1 choix pour le BookNode et moins de 4 choix pour un node normal, on peut ajouter un lien
        // Le sourceNode ne doit pas être une page de fin
        if (!sourceNode.getEnd() && (((sourceNode instanceof BookNode) && (sourceNode.getChoix().size() < 1)) || (!(sourceNode instanceof BookNode) && (sourceNode.getChoix().size() < 4)))) {

            // Si les deux noeuds sont valides
            if(sourceNode == null || destNode == null )
                return null;

            // Création d'un lien
            PageLink link = new PageLink("Choix " + (sourceNode.getChoix().size() + 1), destNode, null);

            // Ajout du lien au noeud source
            sourceNode.ajouterChoix(link);

            // Ajout de la référence au destNode
            destNode.addReference(sourceNode);

            return link;
        }
        return null;
    }

    /**
     * Methode pour supprimer un lien de la liste des choix d'un noeud.
     * @param sourceNode le noeud source.
     * @param link le lien à supprimer.
     */
    public void deleteLink(PageNode sourceNode, PageLink link){
        // Si le noeud ou le lien invalide on sort de la fonction
        if(sourceNode == null || link == null)
            return;
        // Sinon on supprime le lien
        sourceNode.supprimerChoix(link);

        // Et on dé-référence le noeud pointé par le lien
        link.getDestination().deleteReference(sourceNode);
    }

    /**
     * Methode pour ajouter la nécessité d'un objet à un choix d'un noeud donné.
     * @param sourceNode le noeud en question.
     * @param link le lien qui nécessite l'objet en question.
     * @param object l'objet nécessaire à ajouter à ce choix.
     */
    public boolean addObjectLink(PageNode sourceNode, PageLink link, BookObject object){

        // Si on a 1 lien ou moins on ne peux pas ajouter d'objet sur le lien
        if (!(sourceNode.getChoix().size() <= 1)) {
            if(sourceNode == null || link == null || object == null)
                return false;
            // Sinon on modifie le lien pour qu'il nécessite l'objet
            link.setBesoinObjet(object);
            return true;
        }
        return false;
    }

    /**
     * Methode pour supprimer la nécessité d'un objet à un choix d'un noeud donné.
     * @param sourceNode le noeud en question.
     * @param link le lien qui nécessite un objet.
     */
    public void deleteObjectLink(PageNode sourceNode, PageLink link){
        if(sourceNode == null || link == null)
            return;
        // Sinon on supprime l'objet requis pour le lien
        link.setBesoinObjet(null);
    }

    /**
     * Methode pour ajouter un objet à un noeud.
     * @param pageNode le noeud à qui on veut ajouter un objet.
     * @param object l'object à reçevoir.
     */
    public void addObjectNode(PageNode pageNode, BookObject object){
        boolean trouve=false;

        // On peut ajouter un objet sur un noeud si c'est pas un noeud de debut ni de fin
        if (!(pageNode.getEnd()) && !(pageNode instanceof BookNode)) {
            for(int i=0; i<allNodes.size() && !trouve; i++){
                //Vérifier si le noeud existe bien dans le graphe (dans l'ensemble des noeuds du graphe)
                if((allNodes.get(i).getE1()).equals(pageNode)){

                    (allNodes.get(i).getE1()).setObjectReceived(object);  //Ajouter l'object reçu
                }
            }
        }
    }

    /**
     * Methode pour supprimer l'object qu'a reçu noeud.
     * @param node le noeud pour qui on veut supprimer l'objet reçu.
     */
    public void deleteObjectNode(PageNode node){
        boolean trouve=false;
        for(int i=0; i<allNodes.size() && !trouve; i++){
            if((allNodes.get(i).getE1()).equals(node)){
                trouve=true;
                (allNodes.get(i).getE1()).setObjectReceived(null);
            }
        }
    }

    /**
     * Methode pour déterminer si le graphe du livre est valide.
     * @retun une liste des noeuds invalides.
     */
    public List<PageNode> checkValidity(){
        List<PageNode> list = new ArrayList<>();  //La liste qui va récuperer tous les noeuds invalides
        for(int i=0; i<allNodes.size(); i++){

            PageNode node = allNodes.get(i).getE1();

            // Vérifier si un noeud n'est pas référencé (sauf pour la première page)
            if (node.getReferencedBy().isEmpty() && !(node instanceof BookNode)) {
                bookGraph.setIsValid(false); //Indiquer que le graphe est invalide car contient un noeud invalide
                list.add(node); //Ajouter le noeud invalide à la liste
            }

            //Vérifier si un noeud n'a pas de liens vers d'autres noeuds et qu'il n'est pas un noeud de fin
            else if(node.getChoix().isEmpty() && !node.getEnd()){
                bookGraph.setIsValid(false); //Indiquer que le graphe est invalide car contient un noeud invalide
                list.add(node); //Ajouter le noeud invalide à la liste
            }
        }

        if (list.isEmpty()){
            bookGraph.setIsValid(true);
        }
        return list;
    }

    /**
     * Méthode permettant d'obtenir les positions X et Y d'un noeud
     * @param node Le noeud dont on veut les coordonnées
     * @return Tuple de coordonnées X et Y
     */
    public Tuple<Integer, Integer> getNodePos(PageNode node) {
        for (Tuple<PageNode, Tuple<Integer, Integer>> nodesPos: this.allNodes) {
            if (nodesPos.getE1() == node) {
                return nodesPos.getE2();
            }
        }
        return null;
    }


    /**
     * Méthode permettant de saisir les positions X et Y d'un noeud
     * @param node Le noeud dont on veut saisir les coordonnées
     * @param newX nouvelle coordonnée X
     * @param newY nouvelle coordonnée Y
     */
    public void setNodePos(PageNode node, int newX, int newY) {
        for (Tuple<PageNode, Tuple<Integer, Integer>> nodesPos: this.allNodes) {
            if (nodesPos.getE1() == node) {
                nodesPos.setE2(new Tuple<Integer, Integer>(newX, newY));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Tuple<PageNode, Tuple<Integer, Integer>> t: this.allNodes) {
            builder.append(t.toString());
            builder.append(" | ");
        }
        return builder.toString();
    }
}
