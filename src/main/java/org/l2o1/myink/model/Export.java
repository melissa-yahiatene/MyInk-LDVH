/*-----------------------------
 Date : 25/03/2025
 Auteur : Asmaou Baldé
 Fin du développement de la classe Export
 Ajout de la gestion de style (couleur, font, etc.)
 Gestion du débordement du texte sur les autres pages
----------------------------- */

package org.l2o1.myink.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.fxmisc.richtext.model.StyleSpan;
import org.l2o1.myink.model.util.BookNode;
import org.l2o1.myink.model.util.PageLink;
import org.l2o1.myink.model.util.PageNode;
import org.l2o1.myink.model.util.Tuple;

import java.awt.*;
import java.io.*;

import javafx.util.Pair;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.List;


/**
 *  Classe permettant d'exporter un livre en format PDF
 *  Elle récupère le livre depuis un {@link NodePool},
 *  ajoute la couverture, les pages et les liens internes.
 */

public class Export
{
    static final float ESPACE_LINE = 1.8f;
    static final float MAX_LINE_WIDTH = 520f;
    // Marge de bas de page
    static final float MARGIN_BOTTOM = 150f;
    static final float TOP_MARGIN = 100;


    /**
     * Exporte un livre en format PDF.
     *
     * @param title    Le titre du livre à exporter.
     * @param destPath Le chemin de destination où sauvegarder le PDF.
     * @return true si l'export a réussi, false sinon.
     */
    public static boolean exportPDF(String title, String destPath)
    {
        try
        {
            // On récupère le NodePool du livre
            NodePool np = FileManager.getNodePool(title);

            // vérification de l'existence du livre
            if(np == null)
            {
                System.out.println("Erreur : le livre " + title + " n'existe pas");
                return false;
            }
            // On récupère l'image de couverture du livre
            String filePath = FileManager.getCoverPath(title);

            // On vérifie si le graphe est valide
            if(!np.checkValidity().isEmpty()){
                System.out.println("Erreur : le graphe du livre " + title + "est invalide");
                return false;
            }

            // Si tout est bon, on crée un document
            PDDocument doc = new PDDocument();

            // On ajoute l'image de couverture
            addCoverPage(doc,filePath,np);

            // On ajoute enfin le contenu du livre
            addContent(doc,np); // Ligne 90

            // On sauvegarde
            try
            {
                doc.save(destPath + "/" + title + ".pdf");
                doc.close();
                System.out.println("Export du livre " + title + " réussi");
                System.out.println("Fichier sauvegardé à : " +destPath + "/" + title + ".pdf");
                return true;
            }
            catch (IOException e){
                System.out.println("Erreur lors de la sauvegardage du fichier " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }catch (IOException e){
            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajoute une page de titre contenant le nom du livre et de l'auteur.
     *
     * @param doc  Le document PDF.
     * @param p    La page à compléter.
     * @param node Le NodePool contenant les informations du livre.
     * @throws IOException En cas d'erreur d'écriture.
     */
    private static void addTitleNAuthor(PDDocument doc, PDPage p, NodePool node)throws IOException
    {
        try(PDPageContentStream cs = new PDPageContentStream(doc, p, PDPageContentStream.AppendMode.APPEND, true))
        {
            float pageWidth = p.getMediaBox().getWidth();

            // Position titre
            cs.setFont(PDType1Font.HELVETICA_BOLD, 24);
            cs.beginText();
            float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(node.getBookGraph().getTitle()) / 1000 * 24;
            float titleX = (pageWidth - titleWidth) / 2;
            float titleY = 200;
            cs.newLineAtOffset(titleX, titleY);
            cs.showText(node.getBookGraph().getTitle());
            cs.endText();

            // Affichage du nom de l'auteur
            cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 16);
            cs.beginText();
            float authorWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(node.getBookGraph().getAuthor()) / 1000 * 16;
            float authorX = (pageWidth - authorWidth) / 2;
            float authorY = titleY - 60;
            cs.newLineAtOffset(authorX, authorY);
            cs.showText(node.getBookGraph().getAuthor());
            cs.endText();
        }
    }

    /**
     * Ajoute une page de couverture avec image, titre et auteur.
     *
     * @param doc      Le document PDF.
     * @param filePath Chemin vers l'image de couverture.
     * @param node     Le NodePool du livre.
     * @throws IOException En cas de problème d'écriture ou de chargement de l'image.
     */
    private static void addCoverPage(PDDocument doc, String filePath, NodePool node)throws IOException
    {
        // On crée une nouvelle page qu'on ajoute ensuite au document
        PDPage cover = new PDPage();
        doc.addPage(cover);

        // Chargement de l'image de couverture
        PDImageXObject img = PDImageXObject.createFromFile(filePath, doc);

        // On essaie de créer un flux de contenu pour dessiner l'image sur la page du doc
        try(PDPageContentStream cs = new PDPageContentStream(doc, cover))
        {
            // Taille de la page
            float pageWidth = cover.getMediaBox().getWidth();
            float pageHeight = cover.getMediaBox().getHeight();

            float imageWidth = 300;
            float imageHeight = 400;
            float imageX = (pageWidth - imageWidth) / 2;
            float imageY = pageHeight - imageHeight - 100;

            cs.drawImage(img, imageX, imageY, imageWidth, imageHeight);
        }

        // On ajoute le litre + l'auteur
        addTitleNAuthor(doc,cover,node);
    }

    /**
     * Insère le contenu textuel et interactif du livre dans le document PDF.
     * Chaque page du graphe (hors {@link BookNode}) est ajoutée avec :
     * Le texte stylisé, les objets reçus (si présents),les choix cliquables menant à d'autres pages,
     * une indication de fin si applicable et le numéro de page
     *
     * @param doc  Le document PDF cible.
     * @param node Le {@link NodePool} contenant toutes les pages du livre.
     * @throws IOException En cas d'erreur d'écriture ou de création de liens.
     */
    private static void addContent(PDDocument doc, NodePool node) throws IOException {
        // Map pour lier les numéros de page à leurs objets PDPage
        Map<Integer, List<Tuple<PDPage,Integer>>> pageMap = new HashMap<>();
        // Map pour lier les PageNode avec leur valeur de curseurY (Servira à l'affichage des choix)
        Map<PDPage, Float> cursorMap = new HashMap<>();
        // Liste de tous les noeuds du livre
        List<Tuple<PageNode, Tuple<Integer, Integer>>> listNodes = node.getAllNodes();

        // Numéro de page
        int pageNum = 1;

        // On parcourt tous les noeuds du livre et on crée ses pages
        for (Tuple<PageNode, Tuple<Integer, Integer>> allNodes : listNodes) {
            PageNode pn = allNodes.getE1();
            if (!(pn instanceof BookNode)) {
                // Création de la page ou des pages (si texte trop long) pour ce noeud
                PDPage page = new PDPage();
                List<Tuple<PDPage, Integer>> pages = new ArrayList<>();
                pages.add(new Tuple<>(page, pageNum));
                pageMap.put(pn.getPageNumber(), pages);

                // Ajout de la page au document
                doc.addPage(page);

                PDPageContentStream cs = new PDPageContentStream(doc, page);
                float cursorY = page.getMediaBox().getHeight() - TOP_MARGIN;
                float startX = 50;

                // Affichage du numéro de page
                cs.setNonStrokingColor(Color.BLACK);
                cs.setFont(PDType1Font.HELVETICA, 12);
                float textWidth = PDType1Font.HELVETICA.getStringWidth(String.valueOf(pageNum)) / 1000 * 12;
                float posX = (page.getMediaBox().getWidth() - textWidth) / 2;
                float posY = 30;
                cs.beginText();
                cs.newLineAtOffset(posX, posY);
                cs.showText(String.valueOf(pageNum));
                cs.endText();
                pageNum++;

                // Récupération du texte et styles
                String fullText = pn.getText();
                StyleSpans<String> spans = deserializeStyleSpans(pn.getStyleData(), fullText);


                if (spans != null && fullText != null) {
                    int start = 0;
                    for (StyleSpan<String> span : spans) {
                        int length = span.getLength();
                        if (start + length > fullText.length()) break;

                        String sub = fullText.substring(start, start + length);
                        String style = span.getStyle();

                        // Gestion des styles
                        int fontSize = 12;
                        if (style.contains("font-size:")) {
                            try {
                                int index = style.indexOf("font-size:");
                                String sizePart = style.substring(index).split("px")[0].replaceAll("[^0-9]", "");
                                fontSize = Integer.parseInt(sizePart);
                            } catch (Exception e) {
                                fontSize = 12;
                            }
                        }

                        PDFont font = PDType1Font.HELVETICA;
                        if (style.contains("bold")) font = PDType1Font.HELVETICA_BOLD;
                        else if (style.contains("italic")) font = PDType1Font.HELVETICA_OBLIQUE;
                        else if (style.contains("font-courier-new")) font = PDType1Font.COURIER;
                        else if (style.contains("font-times-new-roman")) font = PDType1Font.TIMES_ROMAN;

                        Color color = Color.BLACK;
                        Map<String, String> styleMap = parseStyle(style);
                        String colorValue = styleMap.get("color");
                        if ("red".equalsIgnoreCase(colorValue)) color = Color.RED;
                        else if ("blue".equalsIgnoreCase(colorValue)) color = Color.BLUE;
                        else if ("green".equalsIgnoreCase(colorValue)) color = Color.GREEN;
                        else if ("yellow".equalsIgnoreCase(colorValue)) color = Color.YELLOW;
                        else if ("purple".equalsIgnoreCase(colorValue)) color = new Color(128, 0, 128);
                        else if ("cyan".equalsIgnoreCase(colorValue)) color = Color.CYAN;
                        else if ("magenta".equalsIgnoreCase(colorValue)) color = Color.MAGENTA;
                        else if ("grey".equalsIgnoreCase(colorValue)) color = Color.GRAY;
                        else if ("brown".equalsIgnoreCase(colorValue)) color = new Color(165, 42, 42);

                        // on découpe en paragraphes à partir des \n
                        String[] splitByLine = sub.split("\n");
                        for (String lineSplit : splitByLine) {
                            List<String> wrappedLines = wrapLine(lineSplit, font, fontSize, MAX_LINE_WIDTH);

                            for (String line : wrappedLines) {
                                float lineHeight = fontSize * 1.8f;

                                if (cursorY < MARGIN_BOTTOM) {
                                    cs.close();
                                    page = new PDPage();
                                    doc.addPage(page);
                                    pages.add(new Tuple<>(page, pageNum));
                                    pageMap.put(pn.getPageNumber(), pages);

                                    cs = new PDPageContentStream(doc, page);
                                    cursorY = page.getMediaBox().getHeight() - TOP_MARGIN;

                                    // Numérotation nouvelle page
                                    cs.setNonStrokingColor(Color.BLACK);
                                    cs.setFont(PDType1Font.HELVETICA, 12);
                                    float newTextWidth = PDType1Font.HELVETICA.getStringWidth(String.valueOf(pageNum)) / 1000 * 12;
                                    float newPosX = (page.getMediaBox().getWidth() - newTextWidth) / 2;
                                    cs.beginText();
                                    cs.newLineAtOffset(newPosX, posY);
                                    cs.showText(String.valueOf(pageNum));
                                    cs.endText();
                                    pageNum++;
                                }

                                cs.setFont(font, fontSize);
                                cs.setNonStrokingColor(color);
                                cs.beginText();
                                cs.newLineAtOffset(startX, cursorY);
                                cs.showText(line);
                                cs.endText();

                                cursorY -= lineHeight;
                                cursorMap.put(page, cursorY);
                            }

                            // Ajoute un espace entre les paragraphes (si \n)
                            cursorY -= fontSize * 0.5f;
                        }

                        start += length;
                    }
                }


                // Si le noeud a des objets reçus on les affiche
                if (pn.getObjectReceived() != null) {
                    cs.setNonStrokingColor(Color.BLACK);
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.beginText();
                    cs.newLineAtOffset(startX, cursorY - 20);
                    cs.showText("Vous avez obtenu : ");
                    cs.endText();
                    cursorY -= 40;

                    cs.beginText();
                    cs.newLineAtOffset(startX, cursorY);
                    switch (pn.getObjectReceived().getNom()) {
                        case "red-key.png" -> cs.showText("une clé rouge");
                        case "blue-key.png" -> cs.showText("une clé bleue");
                        case "yellow-key.png" -> cs.showText("une clé jaune");
                        case "green-key.png" -> cs.showText("une clé verte");
                        default -> cs.showText("une clé violette");
                    }
                    cs.endText();
                    cursorY -= 40;
                    cursorMap.put(page,cursorY);
                }
                cs.close();
            }
        }

        // Crée un lien réel si la destination existe
        for (Tuple<PageNode, Tuple<Integer, Integer>> allNodes : listNodes) {
            PageNode pn = allNodes.getE1();

            if (!(pn instanceof BookNode)) {
                PDPage page = pageMap.get(pn.getPageNumber()).getLast().getE1();
                List<PageLink> links = pn.getChoix();

                // Initialise le curseur si besoin
                Float cursorY = cursorMap.get(page);
                if (!(cursorY == null))
                {
                    for (int i = 0; i < links.size(); i++) {
                        PageLink link = links.get(i);

                        if (link.getDestination() != null) {
                            int destPageNum = link.getDestination().getPageNumber();
                            // Si la page de destination existe
                            if (pageMap.containsKey(destPageNum)) {
                                PDPage destinationPage = pageMap.get(destPageNum).getFirst().getE1();

                                // Position du texte
                                float choiceY = cursorY - 20;

                                // Construire le texte du lien
                                String choiceText = link.getText();
                                if (link.getBesoinObjet() != null) {
                                    choiceText += " (requiert ";
                                    switch (link.getBesoinObjet().getNom()) {
                                        case "red-key.png" -> choiceText += "une clé rouge)";
                                        case "blue-key.png" -> choiceText += "une clé bleue)";
                                        case "yellow-key.png" -> choiceText += "une clé jaune)";
                                        case "green-key.png" -> choiceText += "une clé verte)";
                                        default -> choiceText += "une clé violette)";
                                    }
                                }
                                choiceText += "-> Cliquer pour accéder à la suite (Page " + pageMap.get(destPageNum).getFirst().getE2() + ")";

                                // Afficher le texte
                                PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
                                cs.beginText();
                                cs.setNonStrokingColor(Color.BLACK);
                                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                                cs.newLineAtOffset(50, choiceY);
                                cs.showText(choiceText);
                                cs.endText();
                                cs.close();

                                // On crée une zone cliquable
                                float textWidth = PDType1Font.HELVETICA.getStringWidth(choiceText) / 1000 * 12;
                                PDRectangle position = new PDRectangle();
                                position.setLowerLeftX(50);
                                position.setLowerLeftY(choiceY - 2);
                                position.setUpperRightX(50 + textWidth);
                                position.setUpperRightY(choiceY + 12);

                                // Lien PDF
                                PDAnnotationLink pdfLink = new PDAnnotationLink();
                                pdfLink.setRectangle(position);
                                PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
                                borderStyle.setWidth(0);
                                pdfLink.setBorderStyle(borderStyle);

                                // On définit la page de destination
                                PDActionGoTo action = new PDActionGoTo();
                                PDPageXYZDestination dest = new PDPageXYZDestination();
                                dest.setPage(destinationPage);
                                dest.setTop((int) destinationPage.getMediaBox().getHeight());
                                dest.setLeft(0);
                                action.setDestination(dest);
                                pdfLink.setAction(action);

                                page.getAnnotations().add(pdfLink);


                            }
                        }
                        // Décalage du curseur pour le prochain lien
                        cursorY -= 25;
                    }

                }
            }
        }


    }

    /**
     * Découpe une ligne de texte si elle dépasse la largeur maximale spécifiée (MAX_X).
     *
     * @param line La ligne de texte à découper.
     * @param font La police de caractère utilisée pour le texte.
     * @param fontSize La taille de la police.
     * @param maxWidth La largeur maximale autorisée pour la ligne.
     * @return Un tableau de chaînes de caractères représentant les lignes découpées.
     */
    private static List<String> wrapLine(String line, PDFont font, int fontSize, float maxWidth) {
        List<String> wrappedLines = new ArrayList<>();
        line = line.replace("\n", " ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : line.split(" ")) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

            try {
                float width = font.getStringWidth(testLine) / 1000 * fontSize;

                if (width > maxWidth) {
                    // On ajoute la ligne actuelle et on commence une nouvelle ligne avec le mot
                    if (currentLine.length() > 0) {
                        wrappedLines.add(currentLine.toString());
                    }
                    currentLine.setLength(0);
                    currentLine.append(word); // le mot entier va à la ligne suivante
                } else {
                    if (currentLine.length() > 0) currentLine.append(" ");
                    currentLine.append(word);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du calcul de la largeur de la ligne: " + e.getMessage());
            }
        }

        if (currentLine.length() > 0) {
            wrappedLines.add(currentLine.toString());
        }

        return wrappedLines;
    }


    /**
     * Désérialise une chaîne Base64 en un objet {@link StyleSpans} représentant
     * les styles à appliquer au texte (police, couleur, taille, etc.).
     *
     * @param base64 La chaîne sérialisée représentant les styles.
     * @return Un {@link StyleSpans} contenant les styles du texte, ou null en cas d'erreur.
     */
    private static StyleSpans<String> deserializeStyleSpans(String base64, String fallbackText) {
        List<Pair<Integer, String>> list;

        if (base64 == null || base64.isEmpty()) {
           // On ajoute un style par défaut
            list = new ArrayList<>();
            list.add(new Pair<>(fallbackText.length(), "font-size:12px; color:black;"));
        } else {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                list = (List<Pair<Integer, String>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Construction des spans
        StyleSpansBuilder<String> builder = new StyleSpansBuilder<>();
        for (Pair<Integer, String> pair : list) {
            builder.add(pair.getValue(), pair.getKey());
        }

        return builder.create();
    }


    /**
     * Parse une chaîne de style CSS inline et la convertit en une map clé-valeur.
     *
     * Chaque déclaration CSS sous la forme "clé: valeur;" est extraite et ajoutée
     * à la map retournée. Les espaces superflus sont supprimés.
     *
     * La propriété spéciale "-fx-fill" (utilisée en JavaFX) est renommée en "color"
     * pour correspondre aux traitements de couleurs attendus.
     *
     * @param style la chaîne de style CSS à analyser, par exemple "font-size:12px; color:red;"
     * @return une map contenant les paires clé-valeur extraites, ou une map vide si l'entrée est nulle ou vide
     */
    private static Map<String, String> parseStyle(String style) {
        Map<String, String> map = new HashMap<>();
        if (style == null || style.isBlank()) return map;

        String[] declarations = style.split(";");
        for (String declaration : declarations) {
            if (!declaration.contains(":")) continue;
            String[] parts = declaration.split(":", 2);
            String key = parts[0].trim();
            String value = parts[1].trim();
            if (!key.isEmpty() && !value.isEmpty()) {
                if (key.equals("-fx-fill")) {
                    map.put("color", value);
                } else {
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}