# Épure — Journal des décisions (DECISIONS)

> Ce fichier consigne les choix techniques et de conception d'Épure, avec leur justification.
> Règles :
> - Une décision ouverte `Dx` de `SPEC.md` est consignée ici **avant** la tâche de `ROADMAP.md` qui en dépend ; `SPEC.md` est ensuite mis à jour (marque **[À DÉCIDER]** retirée).
> - Les entrées ne sont jamais effacées. Une décision remplacée est marquée « Remplacée par DEC-xxxx », et la nouvelle entrée renvoie à l'ancienne.
> - Les entrées sont numérotées dans l'ordre chronologique (`DEC-0001`, `DEC-0002`…).

---

## Modèle d'entrée

```
## DEC-xxxx — Titre court

- **Date** : AAAA-MM-JJ
- **Statut** : Acceptée | Remplacée par DEC-xxxx | Abandonnée
- **Décision(s) SPEC concernée(s)** : Dx (ou « — »)
- **Contexte** : le problème posé, les contraintes.
- **Options envisagées** : A, B, C (avantages / inconvénients en une ligne chacune).
- **Décision** : l'option retenue, formulée sans ambiguïté.
- **Justification** : pourquoi cette option.
- **Conséquences** : ce que cela implique (code, permissions, Play Store, documents à mettre à jour).
- **Réversibilité** : facile / coûteuse / impossible, et à quelles conditions.
```

---

## DEC-0001 — Nom, identifiants et dépôt d'Épure

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : D24 (en partie : nom distinctif et sa portée). Le choix de publier ou non (Play Store, F-Droid, aucune publication) reste ouvert.
- **Contexte** :
  - Il fallait un nom qui évoque la sobriété, sans aucun lien avec la référence fonctionnelle : « minimalist phone® » est une marque déposée (QQ42 Labs UG) et ne doit jamais servir de nom ni suggérer une affiliation.
  - Homonymes vérifiés en octobre 2026 : une app de soins de la peau « Épure » sur le Play Store, `epure-sh/epure` (outil commercial de surveillance d'erreurs serveur), quelques dépôts anciens ou inactifs. Aucun conflit pour un usage personnel ni pour un dépôt GitHub.
  - L'app de soins « Épure » déjà présente sur le Play Store impose, en cas de publication, un titre de fiche qui s'en distingue.
- **Options envisagées** (portée du nom distinctif) :
  - A. Nom distinctif partout, y compris sous l'app sur le téléphone : distinction maximale, mais libellé trop long pour un écran d'accueil sobre.
  - B. Nom distinctif seulement sur la fiche Play Store, « Épure » sur le téléphone : distinction là où les homonymes se côtoient, nom court là où il est lu au quotidien.
- **Décision** :

| Élément | Valeur |
|---|---|
| Nom affiché sur le téléphone (`app_name`) | « Épure » (avec accent) |
| Titre de la fiche Play Store (si publication) | « Épure – launcher minimaliste » |
| Nom technique (projet Android Studio, modules, classes) | Epure (sans accent) |
| Identifiant d'application (`applicationId`) | `com.alain.epure` |
| Dépôt GitHub (public) | `epure-launcher` |
| Licence | GPL v3 |

- **Justification** :
  - Option B retenue : le nom court reste lisible et sobre sous l'app, conformément aux principes de conception ; la mention « launcher minimaliste » ne sert qu'à se distinguer dans les résultats du Play Store.
  - Nom technique sans accent : évite les problèmes d'encodage dans les chemins, les noms de classes et les outils de build.
  - Dépôt `epure-launcher` plutôt que `epure` : nom libre, plus explicite, et sans confusion avec `epure-sh/epure`.
- **Conséquences** :
  - `res/values/strings.xml` : `app_name` = « Épure ».
  - Avant toute publication sur le Play Store : vérification INPI et EUIPO **obligatoire** en classe 9 (logiciels), puis nouvelle recherche d'homonymes juste avant la publication.
  - Fiche Play Store, README et écran « À propos » : jamais « minimalist phone » comme nom, titre ou mot-clé ; seule la mention factuelle « inspiré de Minimalist Phone » est admise dans le README.
  - `SPEC.md` § 2 et D24, `ROADMAP.md` (après le jalon 5) mis à jour en version 0.4.
- **Réversibilité** :
  - Nom affiché et titre de fiche : faciles à changer avant publication.
  - `applicationId` : **définitif une fois l'app publiée** sur le Play Store (le changer revient à publier une autre app). Le modifier avant publication fait perdre les données locales d'Épure sur le téléphone (désinstallation/réinstallation).
  - Nom du dépôt : renommable sur GitHub (redirection automatique de l'ancienne adresse).

---

## DEC-0002 — Choix d'architecture de base et de langue (D1, D2, D3, D11, D23)

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : D1, D2, D3, D11, D23
- **Contexte** : cinq décisions à trancher au jalon 0, avant le jalon 1. Les recommandations de `SPEC.md` § 3, § 2 et § 9 s'appliquent.
- **Options envisagées** :
  - D1 (navigation) : état géré à la main (A) ou `navigation-compose` (B). A : simple, 3–4 écrans. B : plus de code à maintenir pour un besoin réduit.
  - D2 (persistance) : DataStore seul (A) ou DataStore + Room (B). A : suffisant pour les jalons 1–2. B : utile seulement si les blocages deviennent très structurés, ce qui se réévalue au jalon 4.
  - D3 (liste des apps) : `LauncherApps` (A) ou `PackageManager.queryIntentActivities` (B). A : gère les profils et notifie les installations. B : plus simple, sans profils.
  - D11 (langue, forme) : français seul, ou français et anglais ; tutoiement ou vouvoiement.
  - D23 (sauvegarde Android) : `allowBackup` désactivée (A) ou limitée aux réglages hors noms personnalisés (B). B envoie quand même des données vers la sauvegarde Google, ce qui contredit SPEC § 9.
- **Décision** :
  - D1 : état géré à la main (sans `navigation-compose`).
  - D2 : DataStore seul pour les jalons 1–2 ; réévaluation de Room au jalon 4.
  - D3 : `LauncherApps`.
  - D11 : interface en français par défaut ; anglais possible en seconde langue ; tutoiement.
  - D23 : `android:allowBackup="false"`.
- **Justification** :
  - Ces options suivent la recommandation de `SPEC.md`, qui privilégie la solution simple (principe 6).
  - `LauncherApps` gère nativement les profils et les installations, sans permission supplémentaire.
  - D23 : une sauvegarde Google, même chiffrée, ferait sortir les favoris et noms personnalisés du téléphone, ce qui contredit « aucune donnée ne quitte l'appareil ».
- **Conséquences** :
  - `AndroidManifest.xml` : `allowBackup="false"` ; les attributs `dataExtractionRules` et `fullBackupContent` sont retirés, ainsi que leurs fichiers `res/xml/`.
  - Conséquence de D23 : les favoris et noms personnalisés ne sont pas restaurés lors d'un transfert de téléphone ou d'une réinstallation.
  - `res/values/strings.xml` : textes en français ; un dossier `res/values-en/` pourra être ajouté plus tard, sans l'être au jalon 0.
  - Tutoiement à respecter dans tous les textes d'interface.
- **Réversibilité** :
  - D1 et D2 : coûteuses une fois le code écrit, mais faciles à changer tant que le jalon 1 n'est pas commencé.
  - D3 : faible coût à changer tant que la liste des apps n'est pas en place.
  - D23 : facile à changer (une ligne dans le manifeste), mais une sauvegarde activée ultérieurement ne couvrirait que les données créées après.
  - D11 : facile à changer (ajout d'un dossier `values-en/`).

---

## DEC-0003 — Choix d'interface du jalon 1 (D4, D5, D6, D7, D8, D9, D10, D25, D26, D28, D31)

- **Date** : 2026-10-21
- **Statut** : Acceptée (D8 : report, voir ci-dessous)
- **Décision(s) SPEC concernée(s)** : D4, D5, D6, D7, D8, D9, D10, D25, D26, D28, D31
- **Contexte** : le jalon 1 pose la navigation, la liste des apps et le menu d'appui long. Ces points doivent être tranchés avant leurs tâches (`CLAUDE.md`).
- **Options envisagées** : pour chaque point, les options de `SPEC.md` ; la recommandation de `SPEC.md` a été retenue dans tous les cas.
- **Décision** :
  - D4 : les apps du profil professionnel et du Dossier sécurisé Samsung sont ignorées au jalon 1.
  - D5 : balayage vers le haut depuis l'accueil, qui ouvre la liste avec le champ de recherche actif ; un libellé discret « Apps » est affiché en bas de l'accueil.
  - D6 : accès aux Paramètres depuis la liste des apps, coin inférieur droit. Pas d'appui long sur l'accueil au jalon 1.
  - D7 : clavier ouvert automatiquement à l'ouverture de la liste ; pas de lancement automatique quand un seul résultat reste.
  - D8 : **reporté au jalon 2**, où le renommage est introduit.
  - D9 : menu d'appui long du jalon 1 limité à « Infos de l'app » et « Désinstaller ».
  - D10 : appui sur la date sans effet.
  - D25 : pas d'index alphabétique au jalon 1 ; à reconsidérer plus tard.
  - D26 : l'emplacement sous l'horloge est réservé au jalon 1, sans affichage ; le choix de l'indicateur lui-même (oui ou non) reste ouvert pour le jalon 3.
  - D28 : texte seul, sans pictogramme (ni loupe ni roue dentée).
  - D31 : « Désinstaller » renvoie vers la fiche système de l'app ; aucune permission `REQUEST_DELETE_PACKAGES`.
- **Justification** : suivre la recommandation de `SPEC.md` (principe 6 : simplicité d'abord). Pour D8, la décision ne sert pas avant le renommage (jalon 2).
- **Conséquences** :
  - `ui/` : écran accueil avec horloge, date, libellé « Apps » ; écran liste avec recherche et bouton Paramètres en bas à droite.
  - Manifeste : pas de `REQUEST_DELETE_PACKAGES`.
  - Textes en français, tutoiement, dans `strings.xml`.
- **Réversibilité** : facile pour D4, D5, D6, D7, D9, D10, D25, D26, D28 (interface seulement). D31 : facile (ajouter la permission et un intent si l'on change d'avis).

---

## DEC-0004 — Dépendance DataStore Preferences et mémorisation de la demande de launcher

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : D2 (DEC-0002) ; F-1.1 (demande de rôle au premier lancement)
- **Contexte** : F-1.1 demande de devenir launcher « au premier lancement ». Sans mémoire, la demande reviendrait à chaque ouverture tant que l'utilisateur n'accepte pas. Il faut donc stocker un seul booléen (« demande déjà faite »). D2 a retenu DataStore pour les réglages.
- **Options envisagées** :
  - A. `androidx.datastore:datastore-preferences` : bibliothèque officielle, asynchrone, sans configuration. Un ajout de dépendance (quelques Ko).
  - B. `SharedPreferences` : déjà dans Android, aucune dépendance. Mais D2 a retenu DataStore, et l'API synchrone est moins recommandée.
- **Décision** : option A. Dépendance `androidx.datastore:datastore-preferences` (version `1.1.1`), utilisée uniquement dans `data/SettingsStore.kt` pour un booléen `home_role_prompted`.
- **Justification** : c'est le choix de D2, sans bibliothèque tierce (Google, GPL compatible, aucune donnée sortant du téléphone). Pas d'analytics, de crash reporting ni de publicité.
- **Conséquences** :
  - `gradle/libs.versions.toml` et `app/build.gradle.kts` : nouvelle dépendance.
  - Pour refaire le test de la demande, il faut effacer les données d'Épure (Paramètres > Applications > Épure > Stockage > Effacer les données).
- **Réversibilité** : facile (un seul fichier à remplacer).

---

## DEC-0005 — Présentation du menu d'appui long (T-2) : fenêtre centrée

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : T-2 (point non numéroté : fenêtre centrée ou feuille ancrée en bas)
- **Contexte** : `SPEC.md` T-2 laisse ouvert le choix entre une fenêtre centrée (comme la référence) et une feuille ancrée en bas d'écran (plus accessible au pouce).
- **Options envisagées** :
  - A. Fenêtre centrée : fidèle à la référence ; le titre et les actions tiennent dans une boîte noire.
  - B. Feuille en bas : plus accessible au pouce sur grand écran.
- **Décision** : option A, fenêtre centrée sur fond assombri, fermeture par Retour ou toucher hors de la fenêtre.
- **Justification** : choix exprimé par Alain sur le téléphone ; cohérent avec la référence visuelle (§ 5 bis).
- **Conséquences** : le composant de menu est une fenêtre centrée. La question « même menu depuis un favori » (T-2) reste ouverte pour le jalon 2.
- **Réversibilité** : facile (remplacer le composant d'affichage, pas la logique des actions).

---

## DEC-0006 — Taille de base du texte de la liste des apps : 32 sp

- **Date** : 2026-10-21
- **Statut** : Remplacée par DEC-0008
- **Décision(s) SPEC concernée(s)** : F-1.2 (point non numéroté : valeur de la taille de base)
- **Contexte** : `SPEC.md` F-1.2 demande une taille « nettement supérieure au texte courant », sans valeur. Le texte courant de l'app est de 16 sp (corps de texte Material). Alain demande une taille deux fois plus grande.
- **Options envisagées** : 28 sp (proposé initialement), 32 sp (deux fois 16 sp), 56 sp (deux fois 28 sp).
- **Décision** : 32 sp pour les noms d'apps dans la liste.
- **Justification** : choix d'Alain, en référence au texte courant de l'app. Les tailles restent en `sp`, donc suivent le réglage de taille de police du téléphone.
- **Conséquences** : les noms longs passent à la ligne (F-1.2). Cible tactile ≥ 48 dp conservée. La taille de l'horloge (F-1.3) reste à définir à part.
- **Réversibilité** : facile (une constante).

---

## DEC-0007 — Tailles de l'heure et de la date sur l'accueil

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : F-1.3 (taille de l'heure et de la date, non chiffrées dans la spec)
- **Contexte** : F-1.3 demande une heure « en très grande taille et graisse légère » et une date en dessous, sans valeur. Alain a accepté 120 sp pour l'heure.
- **Options envisagées** : heure de 120 sp (proposée et acceptée) ; date de 20 sp (proposée) ou 16 sp (texte courant).
- **Décision** : heure à 120 sp, graisse légère (`FontWeight.Light`) ; date à 20 sp, graisse normale.
- **Justification** : valeur de l'heure acceptée par Alain ; la date doit rester subordonnée à l'heure, d'où une taille supérieure au texte courant mais nettement plus petite.
- **Conséquences** : tailles en `sp`, donc elles suivent le réglage de taille de police du téléphone.
- **Réversibilité** : facile (deux constantes).

---

## DEC-0008 — Taille de base du texte de la liste des apps : 28 sp (remplace DEC-0006)

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : F-1.2 (valeur de la taille de base)
- **Contexte** : après test sur le téléphone, 32 sp est trop grand pour la liste (retour d'Alain).
- **Options envisagées** : 28 sp (proposition initiale), 32 sp (DEC-0006, testée et jugée trop grande).
- **Décision** : 28 sp pour les noms d'apps dans la liste et pour le titre du menu d'appui long.
- **Justification** : test concret sur le téléphone ; 28 sp reste nettement plus grand que le texte courant.
- **Conséquences** : tailles en `sp`, donc elles suivent le réglage de taille de police du téléphone.
- **Réversibilité** : facile (une constante).

---

## DEC-0009 — Menu d'appui long réduit ; paramètres simplifiés (complète DEC-0003, D31)

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : D9, D31, F-1.5, T-2
- **Contexte** : après test sur le téléphone, Alain juge « Désinstaller » inutile dans le menu d'appui long, puisque « Infos de l'app » mène à la fiche système où la désinstallation est possible. Il demande aussi un écran Paramètres plus sobre.
- **Décision** :
  - Menu d'appui long : seule l'action « Infos de l'app » reste au jalon 1. « Désinstaller » est retiré. D31 n'a donc plus d'objet : la désinstallation passe par la fiche système ouverte par « Infos de l'app ».
  - Paramètres : l'option « Quitter Épure » est renommée « Changer de launcher ». Le texte explicatif qui la précédait est supprimé.
  - À propos : toute la ligne « Dépôt GitHub : epure-launcher » est en 20 sp, comme « Épure » dans le même écran.
- **Justification** : choix d'Alain, après test sur le téléphone. Un menu plus court évite une action redondante.
- **Conséquences** :
  - `SPEC.md` F-1.5 et le tableau T-2 sont mis à jour pour refléter ces choix.
  - Aucun paramètre de permission n'est modifié.
  - Le texte explicatif était le seul moyen d'expliquer pourquoi Épure ne quitte pas lui-même : ce libellé doit être compris sans lui. Un point à surveiller sur le téléphone.
- **Réversibilité** : facile (libellés et une ligne de menu).

---

## DEC-0010 — Favoris et renommage avancés au jalon 1 (F-2.1 et F-2.4)

- **Date** : 2026-10-21
- **Statut** : Acceptée
- **Décision(s) SPEC concernée(s)** : F-2.1, F-2.4 (jalon 2 à l'origine), D8, D12, D14 ; T-2 (actions « Ajouter aux favoris », « Renommer »)
- **Contexte** : Alain demande ces deux fonctions dès le jalon 1, depuis le menu d'appui long.
- **Décision** :
  - **Favoris (F-2.1)** : ajout et retrait depuis le menu d'appui long (« Ajouter aux favoris » / « Retirer des favoris »). Affichage en liste sous l'horloge. Maximum **7** (D12). Ordre d'ajout ; le réordonnancement reste au jalon 2. Si aucun favori : indication « Appui long sur une app pour l'ajouter ici ». Une app désinstallée disparaît de l'accueil sans erreur.
  - **Renommage (F-2.4)** : « Renommer » depuis le menu d'appui long ouvre une saisie. Longueur maximale **30** caractères (D14). Nom vide refusé. Le nom personnalisé remplace le nom d'origine partout : liste, favoris, titre du menu et recherche. « Réinitialiser le nom » restaure le nom système depuis la même saisie. L'écran Paramètres « Apps renommées » reste au jalon 2.
  - **Recherche (D8)** : sur le nom personnalisé seul.
  - **Stockage** : DataStore (D2), réglages en JSON (`org.json`, fourni par Android, aucune dépendance ajoutée). Clé d'une app : paquet et classe ; le profil utilisateur n'est pas pris en compte, comme pour D4.
- **Justification** : demande explicite d'Alain. Ces fonctions n'exigent pas de nouvelle dépendance ni de permission.
- **Conséquences** :
  - Jalon 1 à revalider pour ces deux fonctions.
  - Restent au jalon 2 : raccourcis (F-2.2), espaces vides (F-2.3), réordonnancement et suppression dans les Paramètres, écran « Apps renommées », nettoyage des entrées orphelines, guide (T-3).
  - Le menu d'appui long depuis un favori de l'accueil reste un point ouvert (T-2).
- **Réversibilité** : facile (ajout de données dans DataStore, aucune migration nécessaire).
