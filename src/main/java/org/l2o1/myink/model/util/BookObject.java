/*-----------------------------
 Date : 10/03/2025
 Auteur : Asmaou Baldé
 Modif classe BookObject, suppression de l'attribut image
----------------------------- */

package org.l2o1.myink.model.util;

import java.io.Serializable;

/**
 * Class représentant un objet du livre
 */
public class BookObject implements Serializable {

    /** Numero de version pour la sérialisation*/
    private static final long serialVersionUID = 1L;

    /** Nom de l'objet*/
    private String nom;


    /** Constructeur avec un paramètre
     * @param nom Le nom de l'objet
     * */
    public BookObject(String nom)
    {
        this.nom = nom;

    }

    /** Constructeur par défaut
     * Initialise l'objet avec le nom objet*/
    public BookObject(){
        this("objet");
    }

    /** Définit le nom de l'objet
     * @param nom Le nouveau nom de l'objet */
    public void setNom(String nom)
    {
        this.nom = nom;
    }

    /** Retourne le nom de l'objet
     * @return Le nom de l'objet
     * */
    public String getNom()
    {
        return nom;
    }

    /**
     * Retourne une représentation textuelle de l'objet
     * @return une chaîne de caractères représentant l'objet
     * */
    @Override
    public String toString()
    {
        return "Objet : [nom=" + nom + "]";
    }

    /**
     * Vérifie si deux objets BookObject sont égaux en comparant leurs noms
     * @param object L'objet à comparer
     * @return true si les noms des objets sont identiques, false sinon
     * */
    @Override
    public boolean equals(Object object) {
        return this.nom.equals(((BookObject) object).nom);
    }

    /**
     * Retourne le code de hachage basé sur le nom de l'objet
     * @return La valeur de hachage de l'objet
     * */
    @Override
    public int hashCode() {
        return this.nom.hashCode();
    }
}
