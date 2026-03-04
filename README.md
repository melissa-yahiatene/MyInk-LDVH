# 🖋️ MyInk - Logiciel de Création de Livres Interactifs

**MyInk** est un logiciel desktop conçu pour l'édition et la gestion de "Livres Dont Vous Êtes le Héros" (LDVH). Ce projet, réalisé dans le cadre de l'UE Projet-Tutoré de la L2 Informatique, propose un environnement complet allant de la modélisation de graphes interactifs à l'exportation interactive.

---

## 📋 Présentation Générale

## 📋 Fonctionnalités Métier 

L'application MyInk répond aux exigences strictes définies dans le cahier des charges pour offrir une expérience de création narrative complète et sans faille technique.

### 🖋️ Édition et Création Interactive
* **Modélisation par Graphes** : Système de création de pages (`PageNode`) interconnectées par des liens directionnels (`PageLink`). Chaque nœud représente une unité narrative isolée avec son propre état.
* **Éditeur de Texte Riche (Rich Text)** : Intégration d'un module d'édition avancée permettant la mise en forme dynamique (styles, couleurs, polices) via *RichTextFX*. Les données sont persistées pour conserver l'intégrité visuelle du récit.
* **Arborescence de Projet** : Organisation hiérarchique des fichiers permettant la gestion multi-projets, avec séparation claire entre les assets (images, sons) et la structure logique.

### 🎒 Système d'Inventaire et Mécaniques de Jeu
Conformément aux spécifications fonctionnelles, MyInk intègre un moteur de règles pour enrichir la narration :
* **Gestion d'Objets (`BookObject`)** : Création et catalogage d'objets uniques (clés) pouvant être ramassés ou perdus au cours du récit.
* **Conditions de Transition (Gating)** : Implémentation de verrous logiques sur les liens. Un passage peut être conditionné par la possession (ou l'absence) d'un objet spécifique dans l'inventaire du lecteur.
* **Variables d'État** : Suivi en temps réel de l'état du joueur (inventaire, historique de navigation) influençant directement les options disponibles dans le moteur de lecture.

### 📄 Exportation et Interopérabilité
* **Génération de PDF Interactifs** : Utilisation de la librairie *Apache PDFBox* pour traduire le graphe logique en un document physique. Le moteur d'exportation calcule les ancres et génère des liens hypertexte internes pour maintenir l'interactivité hors de l'application.
* **Mode Simulation (Playtest)** : Environnement de test intégré permettant de valider le "Flow" narratif, d'éprouver les mécaniques d'inventaire et de vérifier la cohérence des embranchements avant la publication.

---

### 🏗️ Architecture Logicielle
Le projet repose sur le pattern **MVP (Model-View-Presenter)**, garantissant une séparation stricte des responsabilités :
* **Model** : Structures de données complexes et logique de persistance.
* **View** : Interfaces graphiques JavaFX (FXML).
* **Presenter** : Orchestration des flux et contrôle de la logique métier.

---

## 🛡️ Focus Cybersécurité & Robustesse Système

En vue d'une spécialisation en **Master Cybersécurité**, ce projet a été développé avec une attention particulière portée sur l'intégrité des données, le contrôle des flux et la résilience du système.

### 1. Contrôle d'Intégrité et Validation de Graphe
Pour prévenir les états système incohérents (failles logiques), MyInk intègre un moniteur de validité :
* **Audit de Structure** : Algorithmes de parcours de graphe vérifiant l'absence de nœuds orphelins ou de boucles infinies avant toute persistance.
* **Checksum Logique** : La classe `BookNode` porte un indicateur d'intégrité (`isValid`) mis à jour en temps réel après chaque modification structurelle.

### 2. Sécurité de la Persistance et Désérialisation
* **Contrôle de Version (SerialVersionUID)** : Implémentation rigoureuse de la sérialisation Java pour empêcher l'exécution de données corrompues ou de versions d'objets non conformes.
* **Isolation des Assets (Sandboxing local)** : Le `FileManager` assure un cloisonnement strict des répertoires. Chaque projet est confiné dans son propre espace de stockage pour empêcher toute collision ou accès inter-projets non autorisé.

### 3. Contrôle d'Accès Logique (Moteur de Règles)
Le système d'inventaire (`ObjectInventory`) fait office de contrôleur d'accès :
* **Vérification de Privilèges** : L'accès aux transitions du graphe (`PageLink`) est soumis à une validation de clés (objets possédés). Le moteur interdit tout "bypass" de la logique narrative sans les privilèges requis.

### 🧪 Qualité Logicielle & Tests
Le projet a suivi un cycle de développement rigoureux comprenant :
* Cahier des Charges : Analyse du besoin et définition des objectifs.
* Cahier de Recette : Plan des cas de tests détaillé.
* Conception UML : Modélisation complète des classes et des séquences avant le codage.
* Plan de Tests (Recette) : Plus de 70 scénarios de tests ont été exécutés manuellement et via scripts pour valider chaque fonctionnalité (création de nœuds, suppression, export, gestion de l'inventaire).
* Gestion des erreurs : Validation de la cohérence du livre (détection de pages orphelines ou de boucles infinies).
* Manuel d'Installation : Guide pas à pas pour configurer l'environnement Java et lancer l'application.
* Rapport Final : Bilan technique, organisation du travail et perspectives d'amélioration.

---

## 🛠️ Stack Technique
* **Langage** : Java 21
* **UI** : JavaFX / Scene Builder
* **Build** : Maven
* **Librairies** : Apache PDFBox(Pour la génération de documents), RichTextFX(Pour l'édition de texte avancée), JUnit 5(Pour la suite de tests unitaires et fonctionnels).

## 🚀 Procédure de Lancement
Le projet est livré avec un binaire pré-compilé (Fat JAR) incluant toutes les dépendances. Vous pouvez également le lancer via les sources si vous disposez d'un environnement de développement.

### 1. Prérequis
* **Java JDK 21** installé. (via le lien suivant: https://www.oracle.com/fr/java/technologies/downloads/#java21) 
* Variable `JAVA_HOME` configurée.

### 2. Lancement direct via l'Exécutable
Le fichier `.jar` regroupe l'application et ses bibliothèques (JavaFX, PDFBox) dans un environnement isolé.

**Sur Windows (PowerShell / CMD) :**
```powershell
java -jar MyInk-1.0-SNAPSHOT.jar
````

**Sur Linux  / macOS :**
```bash
java -jar MyInk-1.0-SNAPSHOT.jar
```

--- 

### 2. Lancement via les sources
Si vous souhaitez auditer le code ou modifier l'application, utilisez le wrapper Maven inclus :

**Sur Windows (PowerShell / CMD) :**
```powershell
.\mvnw clean javafx:run
````

**Sur Linux  / macOS :**
```bash
chmod +x mvnw
./mvnw clean javafx:run
```
