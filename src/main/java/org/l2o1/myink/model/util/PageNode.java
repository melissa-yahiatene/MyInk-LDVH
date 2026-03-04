/*-----------------------------
 Date : 12/03/2025
 Auteur : Melissa YAHIATENE

 Date modification : 26/03/2025
 Modification des constructeurs

 Date 02/03/2025 : La javadoc
----------------------------- */

package org.l2o1.myink.model.util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Représente un nœud d'une page dans le livre.
 * Chaque nœud contient un numéro de page, un objet reçu, du texte,
 * une liste de choix(liens vers d'autres noeuds), une indication s'il s'agit d'une page de fin,
 *
 */
public class PageNode implements Serializable {

	//Attributs
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Le texte de la page.
	 */
	private String text;

	/**
	 * Le numero de la page.
	 */
	private int pageNumber;

	/**
	 * L'objet qu'a reçu une page.
	 */
	private BookObject objectReceived;

	/**
	 * La liste des choix possibles à partir de cette page, correspondant aux liens vers d'autres noeuds.
	 */
	private List<PageLink> choix;

	/**
	 * Indique si le noeud est un noeud de fin.
	 */
	private boolean end;

	/**
	 *
	 */
	private List<PageNode> referencedBy;

	private String styleData;

	/**
	 * Constructeur de la classe PageNode.
	 *
	 * @param pageNumber Le numéro de la page.
	 * @param end Indique si cette page est un nœud de fin.
	 */
	public PageNode(boolean end, int pageNumber) {
		this.pageNumber=pageNumber;
		this.end=end;
		this.objectReceived=null;
		this.choix= new ArrayList<PageLink>();
		this.referencedBy = new ArrayList<PageNode>();
		this.text="";
	}

	/**
	 * 2ème Constructeur de la classe PageNode.
	 *
	 * @param pageNumber Le numéro de la page.
	 */
	public PageNode(int pageNumber){
		this(false, pageNumber);
	}

	// Getters
	/**
	 * Retourne le texte de la page.
	 *
	 * @return Le texte de la page.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Retourne le numéro de la page.
	 *
	 * @return Le numéro de la page.
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * Retourne l'objet qu'a reçu la page.
	 *
	 * @return l'objet qu'a reçu la page.
	 */
	public BookObject getObjectReceived() {
		return objectReceived;
	}

	/**
	 * Retourne la liste des choix associés à ce noeud.
	 *
	 * @return La liste des choix disponibles à partir de ce noeud.
	 */
	public List<PageLink> getChoix() {
		return choix;
	}

	/**
	 * Retourne un boolean qui indique si le noeud est un noeud de fin.
	 *
	 * @return true si c'est un noeud de fin, false sinon.
	 */
	public boolean getEnd() {
		return end;
	}

	/**
	 * Retourne .
	 *
	 * @return .
	 */
	public List<PageNode> getReferencedBy() {
		return referencedBy;
	}

	public String getStyleData() {
		return styleData;
	}

	//Setters
	/**
	 * Modifie le texte de la page.
	 *
	 * @param text le nouveau texte de la page.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Modifie l'objet reçu par ce nœud.
	 *
	 * @param objectReceived Le nouvel objet à associer à ce nœud.
	 */
	public void setObjectReceived(BookObject objectReceived) {
		this.objectReceived=objectReceived;
	}

	/**
	 * Modifie la valeur indiquant s'il s'agit d'un noeud de fin.
	 *
	 * @param end la nouvelle valeur indiquant s'il s'agit d'un noeud de fin.
	 */
	public void setEnd(boolean end) {
		this.end=end;
	}

	/**
	 * Modifie le numero de la page.
	 *
	 * @param pageNumber Le nouveau numero de la page.
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber=pageNumber;
	}

	public void setStyleData(String styleData) {
		this.styleData = styleData;
	}

	/**
	 *
	 */
	public void addReference(PageNode node) {
		if (!this.referencedBy.contains(node)) {
			this.referencedBy.add(node);
		}
	}

	/**
	 *
	 */
	public void deleteReference(PageNode node) {
		this.referencedBy.remove(node);
	}

	/**
	 * Methode pour ajouter un choix( un lien vers un autre noeud ) à la liste des choix.
	 * @param lien le lien à ajouter à la liste.
	 */
	public void ajouterChoix(PageLink lien) {
		choix.add(lien);
	}

	/**
	 * Methode pour supprimer un choix( un lien vers un autre noeud ) de la liste des choix.
	 * @param lien le lien à supprimer de la liste.
	 */
	public void supprimerChoix(PageLink lien) {
		choix.remove(lien);
	}

	/**
	 * Methode pour supprimer un choix( un lien vers un autre noeud ) par rapport à un noeud de destination, de la liste des choix.
	 * @param node le noeud de destination du lien à supprimer de la liste.
	 */
	// Supprimer un choix par rapport à un noeud de destination
	public void supprimerChoix(PageNode node) {
		boolean trouve = false;
		for (int i = 0; i < this.choix.size() && !trouve; i++) {
			if (this.choix.get(i).getDestination() == node) {
				supprimerChoix(this.choix.get(i));
				trouve = true;
			}
		}
	}

	/**
	 * Methode pour vider la liste des choix : supprimer tous les liens vers les autres noeuds.
	 */
	public void clearChoix() {
		choix.clear();
	}

	/**
	 * Redefinition de la methode equals pour comparer deux pages.
	 * @param pageNode la page à comparer avec la page en question
	 * @return true si les deux pages sont les memes, faux sinon
	 */
	public boolean equals(PageNode pageNode){
		return this == pageNode;
	}

	@Override
	public String toString() {
		return "PageNode{" +
				"pageNumber=" + pageNumber +
				'}';
	}
}
