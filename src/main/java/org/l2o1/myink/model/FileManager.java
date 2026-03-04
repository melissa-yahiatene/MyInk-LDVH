/*-----------------------------
Date : 06/04/2025
Auteurs : Yanis Nessah et Arnaud Miralles
Ajout méthodes pour cohérence des fichiers
----------------------------- */

package org.l2o1.myink.model;

import org.l2o1.myink.model.util.BookNode;
import org.l2o1.myink.model.util.BookObject;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class FileManager {
    /*COMMENT CHARGER UN OBJET EN JAVA */
    /*import java.io.FileWriter;

    public class FileSaver {
        // Méthode pour sauvegarder du texte dans un fichier //
        public static void saveTextToFile(String fileName, String content) throws Exception {
            FileWriter writer = new FileWriter(fileName); // Ouvre (ou crée) le fichier //
            writer.write(content); // Écrit le texte dedans //
            writer.close();
        }

    */

    /*COMMENT SERIALISER UN OBJET */
    /*
    import java.io.Serializable; // Permet de rendre l'objet "sérialisable" (convertible en fichier)//

    public class Person implements Serializable {
    private String name; // Nom de la personne //
    private int age; // Âge de la personne  //

    // Constructeur pour initialiser l'objet Person//
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Méthode pour afficher l'objet sous forme de texte//

    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
*/

    private static final String DIRECTORY = System.getProperty("user.dir") + "/MyInkBooks/"; // Dossier où seront stockés les fichiers //
    private static final String DEFAULT_POSTER_PATH = "/org/l2o1/myink/img/general/default-poster.png";
    private static final String KEY_DIRECTORY = "/org/l2o1/myink/img/general/keys/";

    //Yanis 25 03 2025 & Arnaud 29/03/2025 //

    private static boolean createNewFile(String filePath) {
        filePath = getAvailableFilename(filePath);
        try {
            File file = new File(filePath);
            file.createNewFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static Object deepCopy(Object objet) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objet);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Pour les sources hors ressources
    public static boolean copyFile(File source, File destination) {
        boolean result;
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);
            result = startCopy(in, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    // Pour les sources en ressources
    public static boolean copyFile(String source, File destination) {
        boolean result;
        try {
            InputStream in = FileManager.class.getResourceAsStream(source);
            OutputStream out = new FileOutputStream(destination);
            result = startCopy(in, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private static boolean startCopy(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[8192];
            int longueur;
            while ((longueur = in.read(buffer)) > 0) {
                out.write(buffer, 0, longueur);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean copyDirectory(File source, File dest) {
        if (!source.isDirectory()) {
            throw new IllegalArgumentException("La source doit être un dossier !");
        }

        if (!dest.exists()) {
            dest.mkdirs();
        }

        File[] content = source.listFiles();
        if (content != null) {
            for (File file : content) {
                File newDest = new File(dest, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, newDest);
                } else {
                    copyFile(file, newDest);
                }
            }
        }
        return true;
    }

    // METHODE PRIVATE POUR SUPPRIMER UN DOSSIER ET TOUT SON CONTENU //
    private static boolean deleteDirectory(File directory) {
        // LISTE DES FICHIERS OU DES DOSSIERS DEDANS //
        File[] files = directory.listFiles();
        if (files != null) { //VERIFIE S'IL Y A DES FICHIERS //
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); // SI C'EST UN DOSSIER ON LE SUPPRIME AUSSI //
                } else {
                    file.delete(); // SUPPRIME LE FICHIER //
                }
            }
        }
        // SUPPRIME LE DOSSIER UNE FOIS VIDE //
        return directory.delete();
    }

    // METHODE PRIVATE POUR TROUVER UN NOM LIBRE ( COMME LIVRE , LIVRE(1) ETC .. //
    private static String getAvailableFilename(String filePath) {
        int count = 0;
        String newPath = filePath;

        while (new File(newPath).exists()) {
            count++;
            newPath = filePath + " (" + count + ")";
        }
        return newPath;
    }

    private static void saveObject(Object object, File filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object loadObject(File filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // RECUPERER LA LISTE DES LIVRES ENREGISTREES //
    public static ArrayList<BookNode> getAllBooksInfos() {
        File folder = new File(DIRECTORY); // REPRESENTE LE DOSSIER OU LES LIVRES SONT ENREGISTREES //
        // VERIFIE SI LE DOSSIER N EXISTE PAS OU SI N EST PAS UN FICHIER //
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
            return new ArrayList<>();
            // RETOURNE UNE LISTE VIDE SI AUCUN LIVRE N'A ETE TROUVE //
        }
        // RECUPERE LA LISTE DES DOSSIERS OU DES LIVRES SOUS FORME DE LISTE //

        return getBookInfos(folder);
    }

    private static ArrayList<BookNode> getBookInfos(File folder) {
        ArrayList<BookNode> resultat = new ArrayList<>();

        if (folder.isDirectory()) {
            File[] sousDossiers = folder.listFiles(File::isDirectory);

            if (sousDossiers != null) {
                for (File dossier : sousDossiers) {
                    File bookInfoPath = new File(dossier, "BookInfo.mi");
                    if (bookInfoPath.exists() && bookInfoPath.isFile()) {
                        BookNode bookInfo = (BookNode) loadObject(bookInfoPath);
                        resultat.add(bookInfo);
                    }
                }
            }
        }

        return resultat;
    }

    // RECUPERER LE CHEMIN D'UNE COUVERTURE D'UN LIVRE //
    public static String getCoverPath(String title) {
        File imageFile = new File(DIRECTORY + title + "/assets/cover.png");
        // VERIFIE SI LE L'IMAGE EXISTE //
        if (!imageFile.exists()) {
            if (copyFile(DEFAULT_POSTER_PATH, imageFile)) {
                return imageFile.getAbsolutePath();
            } else {
                return DEFAULT_POSTER_PATH;
            }
        }
        //RETOURNE LE CHEMIN ABSOLU DE L'IMAGE SOUS FORME DE CHAINE DE CARACTERES //
        return imageFile.getAbsolutePath();
    }

    //SUPPRIMER UN LIVRE ET TOUT SON CONTENU //
    public static boolean deleteBookFiles(String title) {
        // DOSSIER DU LIVRE //
        File bookFolder = new File(DIRECTORY + title);
        if (!bookFolder.exists()) {
            // RETOURNE FALSE S'IL N'EXISTE PAS //
            return false;
        }
        // APPELLE LA FONCTION QUI SUPPRIME TOUT //
        return deleteDirectory(bookFolder);
    }

    // DUPLIQUER UN LIVRE EN AJOUTANT 1 2 3 //
    public static boolean duplicateBook(String title) {
        //CREE LE CHEMIN DU FICHIER SOURCE AVEC LE TITRE D'ORIGINE //
        File bookFolder = new File(DIRECTORY + title);

        //VERIFIE SI LE FICHIER SOURCE EXISTE //
        if (!bookFolder.exists()) {
            return false;
        }

        //TROUVER UN TITRE UNIQUE POUR LE LIVRE DUPLIQUE //
        String newTitlePath = getAvailableFilename(bookFolder.getAbsolutePath());

        //CREE LE FICHIER DESTINATION AVEC LE NOUVEAU TITRE //
        File destinationFile = new File(newTitlePath);

        //COPIE LE FICHIER SOURCE AU FICHIER DESTINATION //
        boolean result = copyDirectory(bookFolder, destinationFile);

        if (result) {
            // On modifie la valeur de title
            String newTitle = destinationFile.getName();
            NodePool pool = getNodePool(newTitle);

            pool.getBookGraph().setTitle(newTitle);
            saveBook(newTitle, pool);

            return true;
        } else {
            return false;
        }
    }

    // CREE UN DOSSIER AVEC BOOKINFO.MI , NODEPOOL.MI , ASSETS/COVER.PNG //
    public static String createNewBookFiles() {
        String bookPath = getAvailableFilename(DIRECTORY + "Nouveau livre");

        //CREATION DU DOSSIER DU LIVRE //
        File bookFolder = new File(bookPath);
        String title = bookFolder.getName();

        //CREATION DU DOSSIER PRINCIPAL //
        bookFolder.mkdirs();

        // CREATION DES FICHIERS NECESSAIRES DANS LE DOSSIER DU LIVRE //

        // Assets + cover.png
        File assetsFolder = new File(bookFolder, "assets");
        File coverFile = new File(assetsFolder, "cover.png");
        assetsFolder.mkdirs();
        copyFile(DEFAULT_POSTER_PATH, coverFile);

        // assets/objects + keys
        File objectsFolder = new File(assetsFolder, "objects");
        objectsFolder.mkdirs();
        initializeObjects(title);

        // NodePool.mi + BookInfo.mi
        NodePool pool = new NodePool(title);
        saveBook(title, pool);

        // RETOURNE LE NOM DU LIVRE SI TOUT C'EST BIEN PASSE //
        return title;
    }

    // Sauvegarde les fichiers régulièrement
    public static void saveBook(String title, NodePool pool) {
        File nodePoolFile = new File(DIRECTORY + title, "NodePool.mi");
        File bookInfoFile = new File(DIRECTORY + title, "BookInfo.mi");

        // On ajoute une nouvelle date de sauvegarde
        pool.getBookGraph().setDateLastModif(LocalDateTime.now());

        // NodePool.mi + BookInfo.mi
        BookNode bookInfo = (BookNode) deepCopy(pool.getBookGraph());
        bookInfo.clearChoix();

        saveObject(pool, nodePoolFile);
        saveObject(bookInfo, bookInfoFile);
    }

    public static NodePool getNodePool(String title) {
        File nodePoolFile = new File(DIRECTORY + title, "NodePool.mi");
        return (NodePool) loadObject(nodePoolFile);
    }

    private static void initializeObjects(String title) {
        String[] keyNames =  {
                "blue-key.png",
                "green-key.png",
                "pink-key.png",
                "red-key.png",
                "yellow-key.png",
        };

        for (String keyName: keyNames) {
            String path = KEY_DIRECTORY + keyName;
            addObjectImage(title, path);
        }
    }

    private static String addObjectImage(String title, File source) {
        File dest = new File(getAvailableFilename(DIRECTORY + title + "/assets/objects/" + source.getName()));
        copyFile(source, dest);

        return dest.getName();
    }

    private static String addObjectImage(String title, String source) {
        File dest = new File(getAvailableFilename(DIRECTORY + title + "/assets/objects/" + new File(source).getName()));
        copyFile(source, dest);

        return dest.getName();
    }

    public static ArrayList<BookObject> getBookObjects(String title) {
        ArrayList<BookObject> bookObjects = new ArrayList<>();
        File[] bookObjectFiles = new File(DIRECTORY + title + "/assets/objects/").listFiles();

        if (bookObjectFiles != null) {
            for (File file: bookObjectFiles) {
                bookObjects.add(new BookObject(file.getName()));
            }
        }

        return bookObjects;
    }

    public static String getBookObjectPath(String title, String nom) {
        File bookObjectFile = new File(DIRECTORY + title + "/assets/objects/" + nom);

        if (!bookObjectFile.exists()) {
            return null;
        }
        return bookObjectFile.getAbsolutePath();
    }

    public static String getAvailableFilenameByTitle(String title) {
        return getAvailableFilename(DIRECTORY + title);
    }

    public static File titleToPath(String title) {
        return new File(DIRECTORY + title);
    }

    public static boolean renameDirectory(File oldDir, File newDir) {
        return oldDir.renameTo(newDir);
    }
}

class FileManagerTest {
    public static void main(String[] args) {
        ArrayList<BookNode> books = FileManager.getAllBooksInfos();

        for (BookNode book: books) {
            FileManager.deleteBookFiles(book.getTitle());
        }

        String title = FileManager.createNewBookFiles();
        ArrayList<BookObject> objs = FileManager.getBookObjects(title);
    }
}

