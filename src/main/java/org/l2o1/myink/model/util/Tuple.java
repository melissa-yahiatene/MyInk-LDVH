/*-----------------------------
Date : 24/03/2025
Auteur : Asmaou Baldé
Version 1 de la classe Tuple<T1,T2>
----------------------------- */

package org.l2o1.myink.model.util;

import java.io.Serializable;

/**
 * Représente un couple de valeurs de types génériques T1 et T2
 * Classe permettant d'associer deux objets de types différents dans une seule structure
 * @param <T1> Le type du premier élément du tuple
 * @param <T2> Le type du second élément du tuple
 * */

public class Tuple<T1,T2> implements Serializable {

    /** Numero de version pour la sérialisation*/
    private static final long serialVersionUID = 1L;
    /** Premier élément du tuple*/
    private T1 e1;
    /** Second élément du tuple*/
    private T2 e2;

    /**
     * Constructeur de la class Tuple
     * Initialise un tuple avec deux valeurs
     * @param e1 Le premier élément du tuple
     * @param e2 Le second élément du tuple
     * */
    public Tuple(T1 e1, T2 e2)
    {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Méthode retournant le premier élément du tuple
     * @return Le premier élément du tuple
     * */
    public T1 getE1()
    {
        return e1;
    }

    /**
     * Méthode permettant d'obtenir le second élément du tuple
     * @return Le second élément du tuple
     * */
    public T2 getE2()
    {
        return e2;
    }

    /**
     * Méthode permettant de définir le premier élément du tuple
     * @param e1 Le premier élément du tuple
     * */
    public void setE1(T1 e1)
    {
        this.e1 = e1;
    }

    /**
     * Méthode permettant de définir le second élément du tuple
     * @param e2 Le second élément du tuple
     * */
    public void setE2(T2 e2)
    {
        this.e2 = e2;
    }


    /**
     * Retourne une réprésentation sous forme de chaine de caractères du tuple
     * @return la représentation textuelle du tuple
     * */
    @Override

    public String toString()
    {
        return "<" + e1.toString() + ", " + e2.toString() + ">";
    }
}

