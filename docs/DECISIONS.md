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
