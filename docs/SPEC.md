# Épure — Cahier des charges (SPEC)

> Version 0.7 — 22 octobre 2026
> Statut : brouillon de référence. Les points marqués **[À DÉCIDER]** ne sont pas tranchés ; ils ne doivent pas être implémentés comme s'ils l'étaient. Une fois tranchés, les consigner dans `DECISIONS.md` puis mettre à jour ce document.
> Les points marqués **[À VÉRIFIER]** reposent sur un comportement Android/One UI à confirmer sur l'appareil de test.
>
> **Historique**
> - 0.1 (21/10/2026) : version initiale, à partir du cadrage du Projet.
> - 0.2 (21/10/2026) : intégration de l'analyse des captures de référence (§ 5 bis) ; ajout des exigences transversales T-1 à T-3 ; ajout de F-1.6 (index alphabétique) ; précisions sur l'accueil, la recherche, le menu d'appui long, les écrans d'autorisation ; décisions D25 à D31.
> - 0.3 (21/10/2026) : identité précisée (§ 2) — dépôt GitHub `epure-launcher`, marque tierce, homonymes vérifiés en octobre 2026 ; conditions de nom avant une publication sur le Play Store (§ 10) ; lien « À propos » vers le dépôt (F-1.5).
> - 0.4 (21/10/2026) : nom distinctif et portée tranchés (`DECISIONS.md` DEC-0001) — « Épure » sur le téléphone, « Épure – launcher minimaliste » comme titre de la fiche Play Store uniquement ; D24 réduite au choix du mode de publication.
> - 0.5 (21/10/2026) : D1, D2, D3, D11 et D23 tranchées (`DECISIONS.md` DEC-0002) ; marques [À DÉCIDER] retirées pour ces points.
> - 0.6 (22/10/2026) : D5 (balayage de droite à gauche, DEC-0014), D13 (icônes des raccourcis, DEC-0013) et menu depuis l'accueil (T-2, DEC-0012) intégrés ; D27 reste reporté.
> - 0.7 (22/10/2026) : libellé « Apps » rétabli en bas à gauche de l'accueil (DEC-0015) ; le balayage reste.

---

## 1. Objet

Épure est une application Android de type **launcher** (écran d'accueil) minimaliste. Son but est de réduire le temps d'écran et les usages compulsifs du smartphone : interface en texte seul, sans icônes, et outils de limitation volontaire (minuteur, blocages, filtre de notifications, niveaux de gris).

- **Public visé** : usage personnel en priorité.
- **Distribution** : code source public sur GitHub (GPL v3). Publication sur le Play Store éventuelle, **non prioritaire** (voir § 10).
- **Référence** : Minimalist Phone sert uniquement de référence fonctionnelle et visuelle (voir § 5 bis). Aucune rétro-ingénierie, aucune reprise de code, de nom, de textes, d'icônes ni d'éléments graphiques. « minimalist phone® » est une marque déposée (QQ42 Labs UG) : ne jamais l'utiliser comme nom ni suggérer une affiliation (voir § 2).
- **Code tiers réutilisable** : launchers open source (Olauncher, KISS Launcher) — origine mentionnée, avis de copyright conservés, licence de chaque fichier repris vérifiée.

## 2. Identité

| Élément | Valeur |
|---|---|
| Nom affiché | « Épure » (avec accent) — chaîne `app_name` dans `res/values/strings.xml` |
| Nom technique (projet Android Studio, modules, classes) | Epure (sans accent) |
| Identifiant d'application (`applicationId`) | `com.alain.epure` |
| Dépôt GitHub (public) | `epure-launcher` |
| Licence | GPL v3 |
| Langue de l'interface | Français par défaut, anglais possible en seconde langue, **tutoiement** (DEC-0002) — dans tous les cas, aucun texte en dur : toutes les chaînes dans `res/values/strings.xml` |

### Marque tierce
« minimalist phone® » est une marque déposée (QQ42 Labs UG). Ne jamais l'utiliser comme nom (application, dépôt, paquet, fiche de store), ni suggérer une affiliation. Seule la mention factuelle « inspiré de Minimalist Phone » est admise dans le README (§ 11).

### Homonymes vérifiés (octobre 2026)
- App de soins de la peau « Épure » (Play Store).
- `epure-sh/epure` : outil commercial de surveillance d'erreurs serveur.
- Quelques dépôts anciens ou inactifs.

Conclusion : **aucun conflit pour un usage personnel et pour le dépôt GitHub.**

### Avant une publication sur le Play Store
- Vérification **INPI / EUIPO obligatoire**, classe 9 (logiciels).
- **Titre distinctif de la fiche Play Store** : « Épure – launcher minimaliste » (décidé, DEC-0001). Il ne s'applique **qu'à la fiche** ; le nom affiché sous l'app sur le téléphone reste « Épure ».

## 3. Contraintes techniques (figées)

| Domaine | Choix |
|---|---|
| Langage | Kotlin |
| Interface | Jetpack Compose (Material 3, thème personnalisé noir) |
| Code natif / Rust | Aucun |
| `minSdk` | 31 (Android 12) |
| `targetSdk` / `compileSdk` | 36 (Android 16) |
| Appareil de test principal | Samsung, Android 16 / One UI 8.0 |
| Commentaires de code | Anglais |

### Conséquences du `targetSdk` 36 à prendre en compte
- **Edge-to-edge imposé** : l'interface s'affiche sous les barres système ; gérer les marges (`WindowInsets`) dans Compose.
- **Retour prédictif** : `onBackPressed()` n'est plus appelé ; utiliser `BackHandler` (Compose) / `OnBackPressedCallback`.
- **Visibilité des paquets** (depuis Android 11) : lister les apps lançables impose une déclaration `<queries>` dans le manifeste (intent `MAIN` + `LAUNCHER`). La permission `QUERY_ALL_PACKAGES` n'est **pas** nécessaire et ne doit pas être demandée.

### Architecture proposée (simple)
- Une seule activité (`MainActivity`), écrans en Compose.
- `ViewModel` + `StateFlow` pour l'état des écrans.
- Pas de framework d'injection de dépendances (instanciation manuelle via une classe `AppContainer`).
- Navigation : **état simple géré à la main** (3–4 écrans) ; `navigation-compose` écarté (DEC-0002).
- Persistance : **DataStore** seul pour les jalons 1–2 (réglages, favoris, noms personnalisés) ; **Room** réévalué au jalon 4 si les blocages et plages horaires deviennent trop structurés pour DataStore (DEC-0002).
- Organisation des paquets (indicative) : `ui/` (écrans), `data/` (dépôts, persistance), `system/` (accès aux API Android : apps, usage, accessibilité, notifications), `domain/` (logique pure testable : recherche, évaluation des blocages, minuteur).

## 4. Principes de conception

1. **Sobriété visuelle** : fond noir, texte blanc ou gris clair, aucune icône d'application, aucune animation superflue.
2. **Sobriété technique** : aucun service permanent sans nécessité ; données d'usage lues à la demande ; dépendances au strict minimum.
3. **Permissions justifiées** : chaque permission sensible correspond à une fonction précise (voir § 8). Si l'utilisateur la refuse, seule cette fonction est désactivée ; le reste de l'app fonctionne.
4. **Confidentialité absolue** : toutes les données restent sur le téléphone. Aucune collecte, aucun envoi, aucune statistique externe. **Garantie technique : le manifeste ne déclare pas la permission `INTERNET`.**
5. **Ne pas contrarier One UI** : ne pas dupliquer ni entrer en conflit avec les outils Samsung (niveaux de gris, Bien-être numérique).
6. **Simplicité d'abord** : solution simple avant solution sophistiquée.

## 5. Écrans

| Écran | Rôle | Jalon |
|---|---|---|
| Accueil | Horloge, date, favoris, raccourcis | 1 (base), 2 (favoris, raccourcis) |
| Liste des apps / recherche | Champ de recherche, liste texte de toutes les apps, index alphabétique (selon D25), accès aux Paramètres | 1 |
| Menu d'appui long | Actions sur une app (T-2) | 1, enrichi aux jalons 2 et 4 |
| Paramètres | Réglages d'Épure, « Changer de launcher » | 1, enrichi à chaque jalon |
| Écran d'autorisation | Modèle commun pour chaque permission spéciale (T-1) | 3, réutilisé aux jalons 4 et 5 |
| Guide de premier lancement | Quelques conseils d'usage, une seule fois (T-3, selon D27) | 2 |
| Statistiques | Temps par app, nombre d'ouvertures | 3 |
| Choix de la durée | Avant ouverture d'une app avec minuteur | 4 |
| Écran « Temps écoulé » / « App bloquée » | Affiché quand une app est fermée ou bloquée | 4 |

**Navigation entre accueil et liste des apps (D5, DEC-0014 et DEC-0015)** : balayage de droite à gauche depuis l'accueil, ouvrant la liste avec le champ de recherche actif. Un libellé « Apps » gris, en bas à gauche de l'accueil, ouvre aussi la liste (DEC-0015).

**Accès aux Paramètres (D6, DEC-0003)** : depuis la liste des apps, coin inférieur droit. Pas d'appui long sur l'accueil au jalon 1.

**Pictogrammes d'interface (D28, DEC-0003)** : texte seul, sans pictogramme (ni loupe ni roue dentée). Exception : les trois raccourcis de l'accueil, en icônes simples (DEC-0013).

## 5 bis. Référence visuelle — analyse des captures

Les captures annotées de Minimalist Phone sont conservées **uniquement** dans les connaissances du Projet claude.ai, **jamais dans le dépôt** (règle § 11). Ce tableau décrit ce qui est observé et ce qui est transposé dans Épure, conçu et codé à partir de zéro, avec ses propres textes.

### Observations et transposition

| Observation sur la référence | Transposition dans Épure | Exigence |
|---|---|---|
| Accueil : très grande heure centrée, graisse fine ; date en toutes lettres en dessous (jour, quantième, mois, sans l'année) ; reste de l'écran vide | Heure en grand, graisse légère ; date en toutes lettres sans l'année | F-1.3 |
| Accueil : un arc de cercle fin entoure partiellement l'heure. Sa signification n'est pas lisible sur les captures (probable indicateur de temps d'écran du jour) | Non repris par défaut | D26 |
| Balayage vers le haut depuis l'accueil pour rechercher | Geste proposé pour ouvrir la liste/recherche | F-1.1, D5 |
| Liste : noms d'apps seuls, très grande police, interligne généreux, marge gauche, aucun pictogramme | Repris dans l'esprit ; tailles exprimées en `sp` pour respecter le réglage de taille de police | F-1.2 |
| Recherche : champ réduit à une ligne soulignée, loupe à droite, clavier ouvert d'emblée et curseur actif | Champ minimal ; clavier ouvert automatiquement (à confirmer) | F-1.4, D7 |
| Index alphabétique vertical à droite de la liste ; seules les lettres présentes y figurent | Proposé comme option | F-1.6, D25 |
| Appui long : fenêtre noire à fin liseré blanc et coins arrondis, nom de l'app en titre souligné d'un trait, actions en liste texte, arrière-plan assombri. Actions : rappel de consommation (interrupteur), favoris, bloquer, renommer, masquer, déplacer vers un dossier, désinstaller, description de l'app. Une action indisponible apparaît grisée | Menu texte de même sobriété, contenu adapté aux fonctions retenues d'Épure (masquage et dossiers exclus) | T-2, D9 |
| Blocage d'une app jusqu'à 30 jours (texte d'aide partiellement visible) | Durée maximale de blocage à fixer | F-4.3, D30 |
| Cartes de conseils numérotées (1/9 à 9/9) au-dessus de la liste au premier usage : rechercher plutôt que parcourir, choisir ses favoris avec soin, appui long pour les actions | Guide court optionnel, textes propres à Épure | T-3, D27 |
| Assistant de configuration en plusieurs étapes (indicateur d'étapes, consignes numérotées, bouton ouvrant le réglage système, mention « révocable à tout moment », fermeture possible) | Écran d'autorisation commun, présenté **au moment où la fonction est utilisée** | T-1, D29 |
| La référence demande l'affichage par-dessus les autres apps et l'accès aux données d'utilisation pour son rappel de temps | Épure n'utilise **pas** l'affichage par-dessus les autres apps : l'écran « Temps écoulé » est une activité d'Épure affichée après retour à l'accueil (§ 8) | F-4.0 |
| Réglage One UI correspondant à l'accès aux statistiques : écran « Données d'utilisation », interrupteur « Octroi d'autorisation » | Libellés One UI à citer dans l'écran d'autorisation et le README | F-3.1 |
| Captures 1 et 2 : un sélecteur système de navigateur (« Terminer l'action avec ») apparaît par-dessus l'app ; son déclencheur n'est pas identifiable sur les captures | Épure ne déclenche aucune ouverture web non demandée | F-1.4 |

### Non repris (décision de principe)
- Masquage d'apps et dossiers : hors périmètre (masquage écarté ; dossiers non retenus).
- Textes, illustrations de téléphone, pictogrammes et mise en page exacte de la référence.
- Ton promotionnel ou chiffré (promesses de progrès en N jours) : les textes d'Épure restent factuels et neutres.

## 6. Exigences fonctionnelles

Identifiants : `F-<jalon>.<n>` pour les fonctions, `T-<n>` pour les exigences transversales. Chaque exigence comporte des **critères d'acceptation** vérifiables sur le téléphone.

### Exigences transversales

#### T-1 Écran d'autorisation (modèle commun)
Utilisé pour chaque accès spécial : statistiques d'usage (jalon 3), accessibilité et batterie (jalon 4), écoute des notifications (jalon 5).
- Plein écran noir, texte seul : **titre** (la fonction concernée), **pourquoi** (une ou deux phrases), **ce qu'Épure ne fait pas** (aucune lecture de contenu, aucun envoi), **consignes numérotées** pour One UI (nom exact du réglage et de l'interrupteur, ex. « Données d'utilisation » → « Octroi d'autorisation »), bouton **« Ouvrir le réglage »**, mention « Révocable à tout moment dans les réglages du téléphone », bouton **« Plus tard »**.
- Au retour du réglage système : vérification automatique de l'autorisation ; écran fermé si elle est accordée, consigne maintenue sinon.
- **[À DÉCIDER (D29)]** Moment de la demande : au premier usage de la fonction concernée (recommandé, cohérent avec le principe 3) ou assistant groupé au premier lancement (choix de la référence).
- Pas d'illustration ni de capture du réglage système (sobriété, et pas de visuels tiers dans le dépôt).

*Critères* : refuser l'autorisation désactive seulement la fonction concernée ; accorder puis revenir dans Épure active la fonction sans redémarrage.

#### T-2 Menu d'appui long sur une app
- Fenêtre texte sur fond noir, liseré fin clair, titre = nom affiché de l'app (nom personnalisé s'il existe), séparateur, puis actions en liste ; arrière-plan assombri ; fermeture par Retour ou toucher hors de la fenêtre. Fenêtre centrée, comme la référence (DEC-0005).
- Contenu (D9, DEC-0003, puis DEC-0009 pour le jalon 1 : « Infos de l'app » seule) — chaque action n'apparaissant qu'à partir du jalon qui l'implémente :

| Action proposée | Jalon | Remarque |
|---|---|---|
| Ajouter aux favoris / Retirer des favoris | 2 | F-2.1 |
| Renommer | 2 | F-2.4 |
| Minuteur (interrupteur « avec minuteur ») | 4 | F-4.1 ; grisé avec explication si le service d'accessibilité est inactif |
| Bloquer… | 4 | F-4.3 ; grisé si le service d'accessibilité est inactif |
| Infos de l'app | 1 | Ouvre la fiche système (`Settings.ACTION_APPLICATION_DETAILS_SETTINGS`) |
| Désinstaller | — | Retiré (DEC-0009) : passer par « Infos de l'app » |

- Exclus : « Masquer » (fonction écartée), « Déplacer vers un dossier » (non retenue).
- Une action indisponible (permission absente) reste visible mais grisée, avec un appui qui ouvre l'écran d'autorisation (T-1).
- Même menu depuis un favori de l'accueil (DEC-0012).

*Critères* : chaque action produit l'effet attendu ; aucune action d'un jalon non encore implémenté n'apparaît.

#### T-3 Guide de premier lancement
- **[À DÉCIDER (D27)]** : à implémenter ou non.
- Si retenu : 3 à 5 cartes de texte au plus, affichées une seule fois, ignorables (« Passer »), sans animation, avec des textes propres à Épure. Thèmes possibles : chercher l'app voulue plutôt que parcourir la liste ; choisir ses favoris avec soin ; appui long pour les actions ; où se trouvent les Paramètres et « Changer de launcher ».
- Le guide peut être relancé depuis les Paramètres.

### Jalon 1 — Launcher de base

#### F-1.1 Rôle de launcher
- L'activité principale déclare un `intent-filter` `MAIN` + `HOME` + `DEFAULT`.
- Au premier lancement, si Épure n'est pas l'écran d'accueil par défaut, proposer de le devenir via `RoleManager` (`ROLE_HOME`, disponible depuis Android 10).
- Appui sur le bouton Accueil quand Épure est déjà affiché : retour à l'état initial de l'accueil (recherche effacée, clavier fermé, menu d'appui long fermé).
- Bouton Retour sur l'accueil : sans effet (ne quitte pas le launcher). Retour depuis la liste des apps : revient à l'accueil.
- Geste d'accès à la liste des apps selon D5 : balayage de droite à gauche (DEC-0014).

*Critères* : Épure peut être choisi comme launcher par défaut sur le Samsung ; Accueil ramène toujours à l'écran d'accueil propre ; Retour ne fait pas « sortir » du launcher ; le geste choisi ne se déclenche pas par erreur lors d'un simple toucher.

#### F-1.2 Liste des apps en texte
- Lister les activités lançables. API `LauncherApps` retenue (DEC-0002) (recommandée : gère nativement les profils utilisateurs et notifie les installations/désinstallations) ou `PackageManager.queryIntentActivities` (plus simple, mais sans profils).
- Tri alphabétique, insensible à la casse et aux accents, sur le nom affiché (nom personnalisé s'il existe, voir F-2.4).
- Épure lui-même n'apparaît pas dans la liste.
- Mise à jour automatique à l'installation, la désinstallation ou la mise à jour d'une app.
- Présentation : une app par ligne, nom seul, grande taille de police et interligne généreux (cible tactile d'au moins 48 dp), marge gauche, texte blanc sur noir. Tailles en `sp` (respect du réglage de taille de police système). Taille de base : 32 sp, soit deux fois le texte courant de l'app (DEC-0006). Un nom trop long passe à la ligne plutôt que d'être tronqué.
- Appui court : lance l'app. Appui long : menu d'actions (T-2).
- Profil professionnel et Dossier sécurisé Samsung : **ignorés au jalon 1** (D4, DEC-0003).

*Critères* : toutes les apps lançables apparaissent ; une app installée apparaît sans redémarrer Épure ; une app désinstallée disparaît.

#### F-1.3 Horloge et date
- Heure (format 24 h ou 12 h selon le réglage système) en très grande taille et graisse légère, centrée dans le haut de l'accueil ; date en toutes lettres, en français, sans l'année, en dessous (ex. « mercredi 21 octobre » **[À DÉCIDER : format exact]**).
- Mise à jour à la minute, sans service ni minuteur permanent (abonnement à `ACTION_TIME_TICK` uniquement quand l'écran est visible, ou équivalent Compose).
- Appui sur l'horloge : ouvre l'app Horloge (voir F-2.2). Appui sur la date : sans effet (D10, DEC-0003).
- Emplacement sous l'horloge **réservé au jalon 1, sans affichage** (D26, DEC-0003). **[À DÉCIDER (D26)]** : indicateur discret autour ou sous l'horloge (ex. arc ou trait proportionnel au temps d'écran du jour), oui ou non, à trancher au jalon 3 ; il ne sera en aucun cas une limite quotidienne (fonction écartée).

*Critères* : l'heure change à la minute exacte ; elle reste juste après mise en veille prolongée et changement de fuseau horaire.

#### F-1.4 Recherche rapide
- Champ de recherche minimal (ligne soulignée) en haut de la liste. Texte seul, sans loupe (D28, DEC-0003).
- Clavier ouvert automatiquement à l'ouverture de la liste (D7, DEC-0003).
- Filtrage à chaque frappe, insensible à la casse et aux accents (« eclair » trouve « Éclair »), correspondance sur le début de mot puis sur une sous-chaîne.
- Recherche sur le nom personnalisé seul (D8, DEC-0010).
- Pas de lancement automatique quand un seul résultat subsiste (rapide, mais source de lancements involontaires ; D7, DEC-0003).
- Touche « Entrée » du clavier : lance le premier résultat.
- Aucun résultat : message « Aucune app » ; pas de repli automatique vers une recherche web ni d'ouverture de navigateur.
- Pendant la saisie, l'index alphabétique (F-1.6) est masqué.

*Critères* : la logique de recherche est une fonction pure couverte par des tests unitaires (accents, casse, ordre des résultats).

#### F-1.5 Paramètres et « Changer de launcher »
- Écran Paramètres en liste texte, même typographie que la liste des apps.
- Accès selon D6.
- Option **« Changer de launcher »** (DEC-0009) : ouvre le réglage système de l'écran d'accueil par défaut (`Settings.ACTION_HOME_SETTINGS`), avec repli sur `Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS` si la première n'est pas disponible. **[À VÉRIFIER]** comportement exact sous One UI 8.0.
- Pas de texte explicatif (DEC-0009).
- Section « À propos » : nom « Épure », version, licence GPL v3, lien vers le dépôt GitHub `epure-launcher` (sans permission Internet : le lien est ouvert par le navigateur), crédits des éventuels codes tiers. Aucune mention suggérant une affiliation avec Minimalist Phone.

*Critères* : depuis Épure, l'utilisateur revient à One UI Home en deux actions au plus après « Changer de launcher ».

#### F-1.6 Index alphabétique
- Non implémenté au jalon 1 (D25, DEC-0003). Argument pour : navigation rapide dans une longue liste. Argument contre : favorise le défilement, que la recherche cherche justement à éviter. À reconsidérer plus tard.
- Si retenu : colonne verticale de lettres, à droite de la liste ; seules les initiales présentes dans la liste y figurent (lettres accentuées regroupées avec leur lettre de base ; chiffres et symboles regroupés sous « # ») ; toucher ou glisser sur une lettre fait défiler jusqu'à la première app correspondante, sans animation superflue ; masqué pendant une recherche.

*Critères (si retenu)* : toucher « M » amène la première app en M en haut de l'écran ; une lettre sans app n'apparaît pas.

### Jalon 2 — Personnalisation de l'accueil

#### F-2.1 Favoris (jalon 1, DEC-0010)
- Liste ordonnée d'apps affichées en texte sur l'accueil, sous l'horloge.
- Ajout depuis l'appui long dans la liste des apps (T-2) ; suppression et réordonnancement dans les Paramètres.
- Accueil sans favori : courte indication texte (ex. « Appui long sur une app pour l'ajouter ici »), sans encadré ni animation.
- Nombre maximal : 7 (D12, DEC-0010).
- Si une app favorite est désinstallée, elle disparaît de l'accueil sans erreur.

#### F-2.2 Raccourcis personnalisables
- Trois raccourcis : **appareil photo**, **téléphone**, **horloge**.
- Comportement par défaut : intents système génériques (appareil photo : `MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA` ; téléphone : `Intent.ACTION_DIAL` ; horloge : `AlarmClock.ACTION_SHOW_ALARMS`).
- Personnalisation : chaque raccourci peut pointer vers n'importe quelle app installée, ou être désactivé.
- **Décidé (D13, DEC-0013)** : trois icônes simples (Material Symbols, Apache 2.0) en bas de l'accueil, par dérogation à D28 pour ces seuls raccourcis. Personnalisables dans Paramètres > Raccourcis (app choisie, retour au défaut, ou désactivé).

#### F-2.3 Espaces vides
- Possibilité d'insérer des séparateurs vides entre les favoris pour aérer la disposition.
- Gestion dans l'écran de réordonnancement des favoris (un « espace » est un élément de la liste).

#### F-2.4 Renommage des apps (jalon 1, DEC-0010)
- Depuis l'appui long (T-2) ou les Paramètres : définir un nom personnalisé.
- **Seul le nom personnalisé** apparaît dans la liste, les favoris, le titre du menu d'appui long et la recherche (sous réserve de D8).
- Le **nom d'origine** n'est visible que dans les Paramètres d'Épure (écran « Apps renommées »), où le nom personnalisé est modifiable ou réinitialisable à tout moment.
- Clé de stockage : composant de l'activité (paquet + classe) + profil utilisateur, pour éviter les collisions.
- Nom personnalisé vide refusé ; longueur maximale : **30 caractères** (D14, DEC-0010).

*Critères* : le renommage survit au redémarrage du téléphone et à la mise à jour de l'app renommée ; la réinitialisation restaure le nom système.

### Jalon 3 — Statistiques d'usage

#### F-3.1 Accès aux statistiques
- Permission spéciale `PACKAGE_USAGE_STATS`, accordée par l'utilisateur dans le réglage système ouvert via `Settings.ACTION_USAGE_ACCESS_SETTINGS`. Sous One UI 8.0 : écran « Données d'utilisation », interrupteur « Octroi d'autorisation ».
- Demande via l'écran d'autorisation commun (T-1) ; si refusé, l'écran Statistiques l'indique et propose de réessayer.

#### F-3.2 Contenu
- Pour aujourd'hui **[À DÉCIDER (D15) : et les 7 derniers jours ?]** : temps passé par app et nombre d'ouvertures, triés par temps décroissant ; temps total.
- Temps : agrégation via `UsageStatsManager`. Ouvertures : comptage des événements `ACTIVITY_RESUMED` via `queryEvents`, en regroupant les reprises rapprochées d'une même app **[À DÉCIDER (D15) : seuil de regroupement]**.
- Épure et le launcher système sont exclus du classement.
- Lecture **à la demande** à l'ouverture de l'écran ; aucune collecte en arrière-plan, aucune base de données d'historique propre à Épure **[À DÉCIDER (D15) : conserver un historique local au-delà de ce que garde le système ?]**.
- **[À VÉRIFIER]** Durée de rétention des événements par le système et cohérence des chiffres avec Bien-être numérique de Samsung (des écarts sont attendus : méthodes de calcul différentes). Ne pas prétendre reproduire ses chiffres.
- Si D26 retient un indicateur sur l'accueil, il réutilise la même lecture à la demande (calcul à l'affichage de l'accueil, au plus une fois par minute).

*Critères* : les chiffres d'une app utilisée volontairement 5 minutes sont cohérents (± 1 minute) ; l'écran s'affiche en moins d'une seconde.

### Jalon 4 — Minuteur, fermeture automatique, blocages

> Jalon le plus délicat : repose sur un `AccessibilityService` et sur la gestion de la batterie par One UI.

#### F-4.0 Service d'accessibilité (socle commun)
- Un `AccessibilityService` détecte l'app au premier plan (événements `TYPE_WINDOW_STATE_CHANGED` uniquement).
- Configuration minimale, par confidentialité : `canRetrieveWindowContent="false"` (aucune lecture du contenu des écrans), pas d'identifiants de vues, aucun enregistrement des événements.
- Action de « fermeture » : `performGlobalAction(GLOBAL_ACTION_HOME)` puis affichage de l'écran Épure correspondant (Épure étant le launcher, l'écran s'affiche dans sa propre activité).
- **Pas d'affichage par-dessus les autres apps** (`SYSTEM_ALERT_WINDOW`) : contrairement à la référence, Épure n'en a pas besoin. Les services d'accessibilité liés par le système font partie des exceptions aux restrictions de lancement d'activité en arrière-plan. **[À VÉRIFIER]** sur One UI 8.0.
- **Limite importante** : Android ne permet pas à Épure de fermer réellement une autre app. « Fermer » signifie **renvoyer à l'accueil** ; l'app reste en mémoire et reste accessible par les apps récentes (d'où la surveillance continue décrite ci-dessous).
- Activation : par l'utilisateur, dans les réglages d'accessibilité, précédée de l'écran d'autorisation commun (T-1) : ce que fait le service, ce qu'il ne fait pas, données non lues.
- **[À VÉRIFIER]** « Paramètres restreints » (Android 13+) : selon le mode d'installation, Android peut bloquer l'activation de l'accessibilité et de l'écoute des notifications pour une app hors Play Store ; l'utilisateur doit alors l'autoriser dans les infos de l'app. Vérifier avec une installation par Android Studio / ADB et documenter la procédure dans le README.
- Batterie One UI : guider l'utilisateur pour exclure Épure de l'optimisation de batterie (« Applis jamais en veille » sous One UI). **[À DÉCIDER (D20)]** Demande directe via `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` (simple, mais strictement encadrée sur le Play Store) ou simple lien vers le réglage.
- **Contournement assumé** : l'utilisateur peut toujours désactiver le service dans les réglages. Épure est un outil d'aide volontaire, pas un contrôle parental ; aucune mesure anti-contournement **[À DÉCIDER (D19) si l'on souhaite une friction, ex. délai avant désactivation dans Épure]**.

#### F-4.1 Minuteur par session
- Marquer certaines apps « avec minuteur » : interrupteur dans le menu d'appui long (T-2) et liste récapitulative dans les Paramètres.
- Au lancement d'une telle app depuis Épure : écran de choix de la durée **[À DÉCIDER (D16) : valeurs proposées, ex. 1 / 5 / 10 / 15 / 30 min, et saisie libre ?]**, puis lancement.
- L'échéance est enregistrée de façon persistante (horodatage de fin), pour survivre à un arrêt du processus.
- **[À DÉCIDER (D17)] Ouverture hors Épure** (apps récentes, notification, lien depuis une autre app) d'une app avec minuteur sans session active : renvoyer à l'accueil et demander une durée (cohérent, mais intrusif), ou laisser faire.

#### F-4.2 Fermeture automatique
- À l'échéance, si l'app minutée est au premier plan : retour à l'accueil et écran « Temps écoulé ».
- Si l'utilisateur a quitté l'app avant l'échéance puis y revient pendant la session : la session continue jusqu'à l'échéance.
- Après l'échéance, la session est terminée ; la rouvrir redemande une durée. (La prolongation de session après une pause est **écartée**.)
- Mécanisme de déclenchement : minuterie interne au service d'accessibilité, **et** vérification de l'échéance à chaque changement de fenêtre (filet de sécurité si la minuterie a été interrompue).

#### F-4.3 Blocage prolongé
- Bloquer une ou plusieurs apps pour une durée (heures ou jours) ou jusqu'à une date/heure, depuis l'action « Bloquer… » du menu d'appui long (T-2) ou les Paramètres.
- **[À DÉCIDER (D30)]** Durée maximale (la référence plafonne à 30 jours) et durées proposées (ex. 1 h, 4 h, jusqu'à demain matin, 1 j, 7 j, date libre).
- Pendant le blocage, toute ouverture de l'app (quel que soit le chemin) renvoie à l'accueil avec l'écran « App bloquée jusqu'à … ».
- Dans la liste des apps, une app bloquée reste visible (le masquage est écarté) **[À DÉCIDER : avec mention « bloquée » ou en gris]**.
- **[À DÉCIDER (D18)]** Levée anticipée du blocage : impossible, possible avec friction (délai d'attente, saisie d'une phrase), ou possible librement.

#### F-4.4 Plages horaires programmées
- Définir des plages (jours de la semaine + heure de début + heure de fin, y compris plages passant minuit) pendant lesquelles une liste d'apps est bloquée.
- **Évaluation à la demande** : le blocage est vérifié au moment où une app arrive au premier plan. Aucune alarme ni tâche planifiée nécessaire (pas de permission d'alarme exacte).
- La logique « telle app est-elle bloquée à tel instant ? » (blocages prolongés + plages) est une fonction pure, couverte par des tests unitaires (minuit, changement d'heure été/hiver, fuseau).

*Critères du jalon 4* : sur le Samsung, écran éteint 30 minutes puis rallumé, le minuteur et les blocages fonctionnent toujours ; après redémarrage du téléphone, les blocages en cours sont toujours appliqués ; aucune consommation de batterie anormale signalée par One UI après 48 h.

### Jalon 5 — Notifications et niveaux de gris

#### F-5.1 Filtre de notifications
- `NotificationListenerService`, activé par l'utilisateur (`Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS`), précédé de l'écran d'autorisation commun (T-1).
- L'utilisateur choisit les apps dont les notifications sont masquées.
- **[À DÉCIDER (D21)]** Mode de masquage : suppression (`cancelNotification` — la notification est perdue) ou report (`snoozeNotification` — réapparaît plus tard ; durée à définir), ou récapitulatif local consultable dans Épure.
- Limites à indiquer à l'utilisateur : les notifications « en cours » (appel, lecture média, services au premier plan) ne peuvent pas être supprimées ; le contenu des notifications n'est ni stocké ni lu au-delà de l'identification de l'app source.
- **[À DÉCIDER (D21)]** Filtre actif en permanence ou lié aux plages horaires du jalon 4.

#### F-5.2 Mode niveaux de gris
- Activation/désactivation depuis Épure en écrivant les réglages sécurisés d'accessibilité (correction des couleurs en mode monochrome).
- Nécessite la permission `WRITE_SECURE_SETTINGS`, **accordée une fois par ADB** : `adb shell pm grant com.alain.epure android.permission.WRITE_SECURE_SETTINGS`. La permission est perdue en cas de désinstallation.
- Sans cette permission : l'option est grisée, avec la commande ADB affichée et un lien vers le réglage Samsung équivalent.
- **Coexistence One UI** : One UI propose déjà un niveau de gris (Bien-être numérique / mode Coucher, réglages d'accessibilité). Épure ne doit pas désactiver un niveau de gris activé par Samsung. **[À VÉRIFIER]** interaction des deux mécanismes sur One UI 8.0. **[À DÉCIDER (D22)]** Activation liée aux plages horaires, ou manuelle uniquement.

## 7. Fonctions écartées

Ne pas implémenter (décision prise) :
- masquage d'apps ;
- prolongation de session après une pause ;
- barre de progression flottante ;
- limite de temps quotidienne.

Non retenues (absentes des fonctions retenues ; à ne pas implémenter sauf décision contraire) :
- dossiers d'apps ;
- recherche web depuis la recherche d'apps.

## 8. Permissions

| Permission / accès | Type | Fonction | Jalon | Obtention |
|---|---|---|---|---|
| `<queries>` intent `MAIN`/`LAUNCHER` | Déclaration manifeste | Liste des apps | 1 | Automatique |
| Rôle `HOME` | Rôle | Être le launcher | 1 | Choix utilisateur |
| `REQUEST_DELETE_PACKAGES` | Normale (à l'installation) | Action « Désinstaller » directe (T-2) | 1 | Automatique — **uniquement si D31 retient l'intent direct** |
| `PACKAGE_USAGE_STATS` | Spéciale | Statistiques (et indicateur D26 éventuel) | 3 | Réglage système (T-1) |
| Service d'accessibilité (`BIND_ACCESSIBILITY_SERVICE`) | Service lié | Minuteur, blocages | 4 | Réglage accessibilité (T-1) |
| Exclusion de l'optimisation de batterie | Réglage | Fiabilité du jalon 4 | 4 | Réglage One UI **[À DÉCIDER (D20) : demande directe ou lien]** |
| Écoute des notifications (`BIND_NOTIFICATION_LISTENER_SERVICE`) | Service lié | Filtre de notifications | 5 | Réglage système (T-1) |
| `WRITE_SECURE_SETTINGS` | Signature/développement | Niveaux de gris | 5 | ADB, une fois |
| `INTERNET` | — | **Non déclarée** (garantie de confidentialité) | — | — |
| `QUERY_ALL_PACKAGES` | — | **Non déclarée** (inutile) | — | — |
| `SYSTEM_ALERT_WINDOW` (affichage par-dessus les autres apps) | — | **Non déclarée** : écrans « Temps écoulé » / « App bloquée » affichés dans Épure après retour à l'accueil (F-4.0) | — | — |

## 9. Exigences non fonctionnelles

| Domaine | Exigence |
|---|---|
| Performance | Retour à l'accueil instantané (pas de rechargement visible) ; liste des apps affichée en moins de 300 ms sur l'appareil de test **[À VÉRIFIER : cible réaliste]** ; ouverture du clavier sans délai perceptible à l'ouverture de la liste |
| Batterie | Aucun service actif hors accessibilité (jalon 4) et écoute des notifications (jalon 5), tous deux liés par le système et activés seulement si l'utilisateur utilise ces fonctions |
| Poids | APK aussi léger que possible ; chaque nouvelle dépendance justifiée dans `DECISIONS.md` |
| Accessibilité | Taille de texte respectant le réglage système (tailles en `sp`) ; cibles tactiles ≥ 48 dp ; libellés lisibles par TalkBack (y compris l'index alphabétique et les éventuels pictogrammes) ; contraste élevé (texte clair sur noir) ; éléments inactifs en gris mais lisibles |
| Robustesse | Aucun plantage si une app disparaît, si une permission est retirée en cours d'usage ou si les données stockées sont absentes/corrompues (retour aux valeurs par défaut) |
| Confidentialité | Aucune donnée ne quitte l'appareil ; sauvegarde Android (`allowBackup`) **désactivée** (DEC-0002) |
| Tests | Logique pure (recherche, tri, index alphabétique, évaluation des blocages, minuteur) couverte par tests unitaires ; chaque jalon validé manuellement sur le Samsung selon la liste de contrôle de `ROADMAP.md` |

## 10. Play Store (non prioritaire — points d'attention)

À ne traiter que si la publication est décidée :
- **Service d'accessibilité** : Google encadre strictement son usage hors aide au handicap ; déclaration obligatoire, divulgation visible dans l'app et risque de refus. Point bloquant potentiel.
- **`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`** : usage restreint par la politique du Play Store.
- **`WRITE_SECURE_SETTINGS`** : ne peut pas être accordée via le Play Store ; la fonction reste « avancée » (ADB).
- **Nom et marques** (voir § 2) : vérification INPI et EUIPO **obligatoire** en classe 9 (logiciels) ; titre de fiche « Épure – launcher minimaliste » (DEC-0001 ; nom sur le téléphone inchangé : « Épure »), notamment pour se distinguer de l'app de soins de la peau « Épure » déjà présente sur le Play Store. La recherche d'homonymes (GitHub, Play Store) a été faite en octobre 2026 ; la refaire juste avant la publication.
- **Fiche Play Store** : captures d'Épure uniquement ; aucune ressemblance trompeuse avec la référence ; aucune utilisation de « minimalist phone » dans le titre, la description ou les mots-clés.
- Alternative possible : F-Droid **[À DÉCIDER (D24)]**.

## 11. Dépôt GitHub public — rappel des règles

- Nom du dépôt : `epure-launcher`.
- `LICENSE` (GPL v3) dès le premier commit ; `.gitignore` Android standard vérifié avant le premier push.
- Jamais dans le dépôt : clé de signature (`.jks`, `.keystore`) et ses mots de passe, `local.properties`, informations personnelles, **captures ou visuels de Minimalist Phone** (ils restent dans les connaissances du Projet claude.ai ; ce document n'en contient qu'une description textuelle).
- Ressources tierces uniquement sous licence compatible (Google Fonts, icônes Material).
- Commits avec l'adresse e-mail « noreply » de GitHub.
- README : mention factuelle « inspiré de Minimalist Phone » autorisée, sans logo, visuel ni suggestion d'affiliation ; section Confidentialité (données sur l'appareil, pas de permission Internet) ; procédure d'activation des permissions spéciales (accessibilité, notifications, ADB), avec les libellés One UI.
- Clé de signature conservée et sauvegardée hors du dépôt.

## 12. Décisions ouvertes — récapitulatif

À trancher puis consigner dans `DECISIONS.md` (le jalon concerné est indiqué). Les recommandations ne valent pas décision.

| # | Sujet | Jalon | Piste issue des captures (v0.2) |
|---|---|---|---|
| D1 | Navigation : état manuel ou `navigation-compose` (**tranchée : DEC-0002**) | 1 | — |
| D2 | Persistance : DataStore seul ou DataStore + Room (**tranchée : DEC-0002**) | 1 / 4 | — |
| D3 | `LauncherApps` ou `PackageManager` (**tranchée : DEC-0002**) | 1 | — |
| D4 | Profil professionnel / Dossier sécurisé (**tranchée : DEC-0003**) | 1 | — |
| D5 | Passage accueil ↔ liste des apps (geste, libellé) (**tranchée : DEC-0003, puis DEC-0014 et DEC-0015** : balayage de droite à gauche, avec libellé « Apps » en bas à gauche) | 1 | Référence : balayage vers le haut → recherche |
| D6 | Accès aux Paramètres (**tranchée : DEC-0003**) | 1 | Référence : depuis l'écran de la liste des apps |
| D7 | Clavier automatique ; lancement auto si un seul résultat (**tranchée : DEC-0003**) | 1 | Référence : clavier ouvert d'emblée |
| D8 | Recherche sur le nom d'origine des apps renommées (**reportée au jalon 2, DEC-0003**) | 1 / 2 | — |
| D9 | Contenu du menu d'appui long (**tranchée pour le jalon 1 : DEC-0003**) | 1 | Proposition en T-2 |
| D10 | Appui sur la date (**tranchée : DEC-0003**) | 1 | — |
| D11 | Langue(s) de l'interface ; tutoiement ou vouvoiement (**tranchée : DEC-0002**) | 1 | Référence : tutoiement |
| D12 | Nombre max de favoris | 2 | — |
| D13 | Forme et emplacement des raccourcis (**tranchée : DEC-0013**, icônes simples en bas de l'accueil) | 2 | — |
| D14 | Longueur max des noms personnalisés | 2 | — |
| D15 | Période des statistiques ; historique local ; seuil de regroupement des ouvertures | 3 | — |
| D16 | Durées proposées par le minuteur | 4 | — |
| D17 | Ouverture hors Épure d'une app avec minuteur | 4 | — |
| D18 | Levée anticipée d'un blocage | 4 | — |
| D19 | Friction avant désactivation du service | 4 | — |
| D20 | Exclusion de batterie : demande directe ou lien | 4 | — |
| D21 | Filtre de notifications : suppression, report ou récapitulatif ; lien avec les plages | 5 | — |
| D22 | Niveaux de gris : manuel ou programmé | 5 | — |
| D23 | Sauvegarde Android (`allowBackup`) (**tranchée : DEC-0002**) | 1 | — |
| D24 | Publication : Play Store, F-Droid, aucune (le nom distinctif et sa portée sont tranchés : DEC-0001) | Après 5 | — |
| D25 | Index alphabétique : oui ou non (**non au jalon 1, DEC-0003**) | 1 | Présent dans la référence |
| D26 | Indicateur de temps d'écran sur l'accueil (arc ou trait) : oui ou non | 1 (emplacement) / 3 (données) | Arc autour de l'heure dans la référence, signification non confirmée |
| D27 | Guide de premier lancement : oui ou non ; nombre de cartes (**reporté : DEC-0012**) | 2 | Référence : 9 cartes de conseils |
| D28 | Pictogrammes d'interface (loupe, paramètres) ou texte seul (**tranchée : DEC-0003**) | 1 | Référence : loupe et roue dentée |
| D29 | Moment des demandes d'autorisation : à l'usage ou assistant initial | 3 | Référence : assistant en étapes ; recommandation : à l'usage |
| D30 | Durée maximale et durées proposées pour le blocage prolongé | 4 | Référence : jusqu'à 30 jours |
| D31 | « Désinstaller » : intent direct (`ACTION_DELETE`, permission `REQUEST_DELETE_PACKAGES`) ou renvoi vers la fiche système de l'app (aucune permission) | 1 | — |
