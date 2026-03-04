/*-----------------------------
 Date : 12/03/2025
 Auteur : Melissa YAHIATENE

 Date Modification : 26/03/2025
 Suppression de l'attribut coverPath
 Ajout des attributs isValid et dateLastModif
 Modifications des constructeurs
 Ajout de setters et getters

 Date Modification : 02/04/2025
 Modification de l'attribut DateLastModif, de son setter et getter, en ajoutant un formatage de la date
----------------------------- */

package org.l2o1.myink.model.util;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente le premier noeud du livre qui correspond à la première page du livre.
 * Ce nœud contient le nom du livre et son auteur.
 * La date de sa dernière modification et une indication si le graphe du livre est valide ou pas.
 */

public class BookNode extends PageNode implements Serializable {

	//Attributs
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Le titre du livre.
	 */
	private String title;

	/**
	 * L'auteur du livre.
	 */
	private String author;

	/**
	 * indique si le graphe du livre est valide ou pas.
	 */
	private boolean isValid;

	/**
	 * date de la derniere modification du livre.
	 */
	private LocalDateTime dateLastModif;

	/**
	 * Constructeur de la classe BookNode.
	 * @param title Le titre du livre.
	 */
	public BookNode(String title) {
		super(false, 0);
		this.title=title;
		this.author="Auteur";
		isValid=false;
		/* Utilisation de la methode now() de la classe LocalDateTime qui renvoie la date et heure actuelles */
		dateLastModif= LocalDateTime.now();
	}

	//Getters
	/**
	 * Retourne le titre du livre.
	 *
	 * @return le titre du livre.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Retourne l'auteur du livre.
	 *
	 * @return l'auteur du livre.
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * Retourne la date de la derniere modification du livre.
	 *
	 * @return la date de la derniere modification du livre.
	 */
	public LocalDateTime getDateLastModif() {
		return dateLastModif;
	}

	/**
	 * Retourne un boolean indiquant si le graphe du livre et valide ou pas.
	 *
	 * @return true si le graphe du livre est valide, false sinon.
	 */
	public boolean getIsValid() {
		return isValid;
	}

	//Setters
	/**
	 * Modifie le titre du livre.
	 *
	 * @param title Le nouveau titre à définir.
	 */
	public void setTitle(String title) {
		this.title=title;
	}

	/**
	 * Modifie l'auteur du livre.
	 *
	 * @param author Le nouveau nom d'auteur à définir.
	 */
	public void setAuthor(String author) {
		this.author=author;
	}

	/**
	 * Modifie la date de la derniere modification du livre.
	 */
	public void setDateLastModif(LocalDateTime date) {
		dateLastModif = date;
	}

	/**
	 * Modifie la validité du livre.
	 *
	 * @param isValid la nouvelle valeur indiquant si le graphe est valide ou pas.
	 */
	public void setIsValid(boolean isValid){
		this.isValid=isValid;
	}

}

