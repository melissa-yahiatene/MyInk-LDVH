/*-----------------------------
 Date : 25/03/2025
 Auteur : Arnaud MIRALLES
 Classe MenuPresenter
 Version 1.0 : Squelette pour les futures fonctions à implémenter
----------------------------- */

package org.l2o1.myink.presenter;

import org.l2o1.myink.model.Export;
import org.l2o1.myink.model.FileManager;
import org.l2o1.myink.model.util.BookNode;
import org.l2o1.myink.view.MenuView;

import java.util.ArrayList;

public class MenuPresenter {

    // --- Attributs de la View ---

    private final MenuView view;

    public MenuPresenter(MenuView view) {
        this.view = view;
    }

    // --- Méthodes de communication ---

    /**
     * Méthode retournant la liste des livres sauvegardés dans le dossier utilisateur
     * @return Liste de BookNode
     */
    public ArrayList<BookNode> getAllBooksInfos() {
        return FileManager.getAllBooksInfos();
    }

    /**
     * Méthode créant un nouveau livre
     * @return Nom du nouveau livre
     */
    public String createNewBookFiles() {
        return FileManager.createNewBookFiles();
    }

    /**
     * Méthode permettant d'avoir le lien vers la couverture du livre
     * @param title Titre du livre
     * @return Lien formaté vers la couverture du livre
     */
    public String getCoverPath(String title) {
        return FileManager.getCoverPath(title);
    }

    /**
     * Fonction permettant d'exporter en PDF le livre
     * @param title Titre du livre
     * @return Pop-up enregistrement PDF + booléen réussite
     */
    public boolean exportToPDF(String title, String destPath) {
        return Export.exportPDF(title, destPath);
    }

    /**
     * Duplique le livre
     * @param title Titre du livre
     * @return Livre dupliqué dans le menu + booléen réussite
     */
    public boolean duplicateBookFiles(String title) {
        return FileManager.duplicateBook(title);
    }

    /**
     * Supprime le livre
     * @param title Titre du livre
     * @return Livre supprimé du menu + booléen réussite
     */
    public boolean deleteBookFiles(String title) {
        return FileManager.deleteBookFiles(title);
    }
}
