/*-----------------------------
 Date : 10/03/2025
 Auteur : Asmaou Baldé
 Début implémentation de la classe PageLink
 Version 1.0
----------------------------- */

package org.l2o1.myink.model.util;

import java.io.Serializable;

/**
 * Class représentant un lien entre deux pages
 * */

public class PageLink implements Serializable {

    /** Numéro de version pour la sérialisation*/
    private static final long serialVersionUID = 1L;
    /** Texte affiché sur le lien*/
    private String text;
    /** Représente le noeud de destination*/
    private PageNode destination;
    /** Intervient lorsqu'un objet est nécessaire pour le lien*/
    private BookObject besoinObjet;

    /**
     * Constructeur à trois paramètres
     * Crée un lien avec un texte, une destination et un objet nécessaire
     * @param text Le text affiché sur le lien
     * @param besoinObjet L'objet nécessaire pour activer le lien
     * @param destination La page de destination du lien
     * */
    public PageLink(String text, PageNode destination, BookObject besoinObjet) {
        this.text = text;
        this.destination = destination;
        this.besoinObjet = besoinObjet;
    }

    /**
     * Constructeur à deux paramètres de PageLink
     * Crée un lien avec une page de destination et un objet nécessaire
     * @param destination La page de destination du lien
     * @param besoinObjet L'objet nécessaire pour activer le lien
     * */
    public PageLink(PageNode destination, BookObject besoinObjet) {
        this("", destination, besoinObjet);
    }

    /**
     * Constructeur à un paramètre de PageLink
     * Crée un lien avec une page de destination
     * @param destination La page de destination du lien
     * */
    public PageLink(PageNode destination) {
        this(destination, null);
    }


    /**
     * Méthode permettant d'obtenir le text du lien
     * @return Le texte du lien
     * */
    public String getText() {
        return text;
    }

    /**
     * Méthode permettant d'obtenir la page de destination du lien
     * @return La page de destination du lien
     * */
    public PageNode getDestination()
    {
        return destination;
    }

    /**
     * Méthode permettant d'obtenir l'objet nécessaire pour l'activation du lien
     * @return L'objet nécessaire
     * */
    public BookObject getBesoinObjet()
    {
        return besoinObjet;
    }

    /**
     * Méthode permettant de définir le texte du lien
     * @param text Le nouveau texte du lien
     * */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Méthode permettant de définir la page de destination du lien
     * @param destination La nouvelle page de destination du lien
     * */
    public void setDestination(PageNode destination)
    {
        this.destination = destination;
    }

    /**
     * Méthode permettant de définir l'objet nécessaire pour l'activation du lien
     * @param besoinObjet Le nouvel objet nécessaire
     * */
    public void setBesoinObjet(BookObject besoinObjet)
    {
        this.besoinObjet = besoinObjet;
    }

    /**
     * Méthode retournant une réprésentation sous forme de chaine de caractères de l'objet PageLink
     * @return Une chaine de caractères représentant l'objet
     * */
    @Override
    public String toString()
    {
        return "PageLink{" +
                "destination=" + destination +
                ", besoinObjet=" + besoinObjet +
                '}';
    }
}
