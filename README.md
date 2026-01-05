# Jin-Zana-pcm-2025

Application de quiz sur le thème des Pokémons, développée dans le cadre de la matière
"Programmation de composants mobiles" à l'Université de Paris Cité en première année de master.

Le projet a été réalisé à l'aide de Android Studio Narwhal 3 Feature Drop | 2025.1.3

# Présentation

L'application propose 4 quiz différents sur le thème des Pokémons :
- Quiz "Ombre": Deviner le Pokémon à partir de sa silhouette.
- Quiz "Pokédex": Ranger dans l'ordre du numéro du pokédex les Pokémons.
- Quiz "Cri": Deviner le Pokémon à partir de son cri.
- Quiz "Statistiques": Ranger les pokémons dans l'ordre croissant à partir d'une statistique
  choisit aléatoirement.

Chaque quiz possède 3 niveaux de difficulté : facile, moyen et difficile, avec plus ou moins de
choix proposés (s'il y a lieu) et de plus ou moins de tentatives.

L'application comporte également un classement des meilleurs scores pour chaque quiz et répertorié
par difficulté.

# Installation

1. Cloner le dépôt Gitlab :
    ```bash
    git clone https://moule.informatique.univ-paris-diderot.fr/zana/jin-zana-pcm-2025.git
    ```

2. Télécharger les fichiers supplémentaires (images, sons, données) en exécutant le script Python
   depuis la racine du projet :
    ```bash
    py ./app/src/main/res/import_data.py # Pour les images, les sons et les données xml.
    py ./app/src/main/res/import_name.py # Pour les statistiques des pokémons.
    # Peut prendre plusieurs minutes selon la vitesse de connexion.
    ```

3. Compiler le projet à l'aide d'Android Studio:
    - Ouvrir Android Studio.
    - Cliquer sur "Open an existing Android Studio project".
    - Sélectionner le dossier cloné précédemment.
    - Laisser Android Studio configurer le projet (téléchargement des dépendances, etc.).
    - Une fois la configuration terminée, cliquer sur "Run" pour compiler et exécuter l'application
      sur un émulateur ou un appareil connecté.

# Contributeurs

Thomas Zana : 22205158
Christophe Jin : 22202010
