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
 * Représente L'inventaire d'objets dans la Page de Lecture.
 * Contient une liste d'objets
 */
public class ObjectInventory implements Serializable {

	//Attributs
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
	public ObjectInventory(ArrayList<BookObject> objects) {
		this.allObjects = objects;
	}

	/**
	 * Le 2ème Constructeur de la classe ObjectCollection qui crée la liste.
	 */
	public ObjectInventory() {
		this(new ArrayList<BookObject>());
	}

	//Getter
	/**
	 * Retourne la liste d'objets de l'inventaire.
	 *
	 * @return la liste d'objets.
	 */
	public List<BookObject> getObjects() {
		return allObjects;
	}
	/**
	 * Methode pour ajouter un objet à l'inventaire.
	 * @param bookObject l'objet à ajouter à l'inventaire.
	 */
	public void addObject(BookObject bookObject) {
		if (bookObject != null) {
			if (!this.allObjects.contains(bookObject)) {
				this.allObjects.add(bookObject);
			}
		}
	}

	/**
	 * Methode pour supprimer un objet de l'inventaire.
	 * @param bookObject l'objet à supprimer de l'inventaire.
	 */
	public void removeObject(BookObject bookObject) {
		if (bookObject != null) {
			for(int i=0; i<allObjects.size(); i++) {
				if(allObjects.get(i).equals(bookObject))
					allObjects.remove(bookObject);
			}
		}
	}
	/**
	 * Methode pour supprimer tous les objets de l'inventaire.
	 */
	public void clear() {
		allObjects.clear();
	}

	/**
	 * Methode pour vérifier si un objet est dans l'inventaire.
	 * @param obj l'objet dont on veut vérifier l'existence dans l'inventaire.
	 */
	public boolean containsObject(BookObject obj) {
		if (obj != null) {
			for (BookObject classObj: this.allObjects) {
				if (classObj.equals(obj)) {
					return true;
				}
			}
		}
		return false;
	}
}
