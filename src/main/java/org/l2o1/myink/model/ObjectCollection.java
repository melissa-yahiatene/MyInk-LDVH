/*-----------------------------
 Date : 12/03/2025
 Auteur : Melissa YAHIATENE

 Date : 03/04/2025 : Ajout de la javadoc
----------------------------- */

package org.l2o1.myink.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.l2o1.myink.model.util.BookObject;

/**
 * Représente La collection d'objets dans la page d'Edition.
 * Contient une liste d'objets
 */
public class ObjectCollection implements Serializable {

	//Attributs
	/**
	 * Numéro de version pour la sérialiation
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * La liste qui contiendra les objets
	 */
	private List<BookObject> allObjects;

	/**
	 * Constructeur de la classe ObjectCollection.
	 *
	 * @param objects une liste d'objets.
	 */
	public ObjectCollection(List<BookObject> objects) {
		this.allObjects = objects;
	}

	/**
	 * Le 2ème Constructeur de la classe ObjectCollection qui crée la liste.
	 */
	public ObjectCollection() {
		this(new ArrayList<BookObject>());
	}

	/**
	 * Methode pour ajouter un objet à la collection.
	 * @param bookObject l'objet à ajouter à la collection.
	 */
	public void addObject(BookObject bookObject) {
		allObjects.add(bookObject);
	}

	/**
	 * Methode pour supprimer un objet de la collection.
	 * @param bookObject l'objet à supprimer de la collection.
	 */
	public void removeObject(BookObject bookObject) {
		for(int i=0; i<allObjects.size(); i++) {

			if(allObjects.get(i)==bookObject)  //Vérifier si l'objet que l'on veut supprimer existe bien dans la collection
				allObjects.remove(bookObject);

			else
				System.out.println("L'objet n'existe pas dans la collection\n");
		}
	}
}
