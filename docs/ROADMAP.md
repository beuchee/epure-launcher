# Épure — Feuille de route (ROADMAP)

> Version 0.6 — 22 octobre 2026
> Règle : **chaque jalon produit une application fonctionnelle, testée sur le téléphone (Samsung, Android 16 / One UI 8.0), avant de passer au suivant.** Commit Git après chaque étape validée sur le téléphone. Une session Claude Code par jalon ou par tâche.
> Références : exigences `F-x.y` / `T-n` et décisions `Dx` dans `SPEC.md`. Une décision `Dx` doit être consignée dans `DECISIONS.md` **avant** la tâche qui en dépend.
>
> **Historique**
> - 0.1 (21/10/2026) : version initiale.
> - 0.2 (21/10/2026) : intégration de l'analyse des captures de référence (`SPEC.md` § 5 bis) — geste d'accès à la liste, index alphabétique, menu d'appui long progressif (T-2), écran d'autorisation commun (T-1), guide de premier lancement (T-3), durée max de blocage ; décisions D25 à D31 réparties dans les jalons.
> - 0.3 (21/10/2026) : identité (`SPEC.md` § 2) — vérification des homonymes cochée (faite en octobre 2026) ; création du dépôt GitHub `epure-launcher` ; nom affiché « Épure » dans `strings.xml` ; conditions de nom avant le Play Store.
> - 0.4 (21/10/2026) : DEC-0001 consignée dans `DECISIONS.md` (nom sur le téléphone « Épure » ; titre de fiche Play Store « Épure – launcher minimaliste ») ; `DECISIONS.md` créé avec modèle d'entrée.
> - 0.5 (22/10/2026) : section transversale « Batterie et mémoire » ajoutée (mesures et seuils à fixer) ; cases du jalon 0 (dépôt, clé, premier push) cochées.
> - 0.6 (22/10/2026) : jalon 2 validé sauf « une semaine d'usage » ; guide reporté (D27) ; indicateur d'accueil du jalon 3 non retenu (DEC-0016).

---

## Jalon 0 — Mise en place du projet

Préparatoire : aucune fonction visible, mais conditionne tout le reste.

- [x] Vérifier l'absence d'app ou de projet homonyme « Épure / Epure » (GitHub, Play Store) avant de rendre le dépôt public — fait en octobre 2026 : aucun conflit pour un usage personnel et GitHub (`SPEC.md` § 2)
- [x] Créer le projet Android Studio « Epure » (nom technique sans accent) : Kotlin, Compose, `applicationId` `com.alain.epure`, `minSdk` 31, `targetSdk`/`compileSdk` 36
- [x] Définir `app_name` = « Épure » (avec accent) dans `res/values/strings.xml`
- [x] Créer le dépôt Git local ; configurer l'e-mail « noreply » de GitHub
- [x] Créer le dépôt public `epure-launcher` sur GitHub (description sans « minimalist phone » ni suggestion d'affiliation)
- [x] Ajouter `LICENSE` (texte complet GPL v3)
- [x] Vérifier le `.gitignore` Android (`local.properties`, `*.jks`, `*.keystore`, `build/`, `.idea/` partiel, `captures/`)
- [x] Créer `docs/` avec `SPEC.md`, `ROADMAP.md`, `DECISIONS.md` (modèle d'entrée + DEC-0001 sur l'identité)
- [x] Vérifier qu'aucune capture de la référence n'est présente dans le dépôt (elles restent dans le Projet claude.ai)
- [x] Rédiger `CLAUDE.md` : choix techniques figés, identité (`SPEC.md` § 2), règles du dépôt public, commandes de build/installation, consigne de lire `SPEC.md` (dont § 5 bis) et `ROADMAP.md` avant toute tâche
- [x] README initial : titre « Épure », description, mention factuelle « inspiré de Minimalist Phone », section Confidentialité, licence
- [x] Vérifier que le manifeste ne contient **pas** les permissions `INTERNET` ni `SYSTEM_ALERT_WINDOW`
- [x] Générer la clé de signature, la stocker **hors du dépôt** et la sauvegarder
- [x] Trancher D1, D2, D3, D11, D23
- [x] Premier build installé sur le Samsung (`./gradlew installDebug`)
- [x] Premier push

**Validation** : l'app « vide » s'installe et s'ouvre sur le téléphone sous le nom « Épure » ; `git status` propre ; aucun fichier sensible suivi par Git.

---

## Jalon 1 — Launcher de base

Décisions préalables : D4, D5, D6, D7, D8, D9, D10, D25, D28, D31 (et choisir l'emplacement réservé à D26, même si l'indicateur n'est réalisé qu'au jalon 3).

### Tâches
- [x] Thème Compose noir, typographie système, échelle de tailles en `sp` (heure très grande et légère ; liste en grande taille), gestion edge-to-edge (`WindowInsets`)
- [x] Manifeste : `intent-filter` `MAIN`/`HOME`/`DEFAULT` ; déclaration `<queries>` (F-1.1, F-1.2)
- [x] Proposition de devenir launcher par défaut via `RoleManager` au premier lancement (F-1.1)
- [x] Gestion du bouton Accueil (`onNewIntent` → réinitialisation de l'accueil) et du bouton Retour (`BackHandler`) (F-1.1)
- [x] Geste ou libellé d'accès à la liste des apps selon D5 (F-1.1)
- [x] Source des apps (`LauncherApps` ou `PackageManager` selon D3) + mise à jour à l'installation/désinstallation (F-1.2)
- [x] Liste des apps en texte, triée, Épure exclu, cibles ≥ 48 dp, noms longs sur deux lignes (F-1.2)
- [x] Horloge et date (format selon F-1.3), mise à jour à la minute sans service permanent (F-1.3)
- [x] Fonction pure de normalisation et de recherche (accents, casse, ordre) + tests unitaires (F-1.4)
- [x] Écran de recherche branché sur la liste : champ minimal, clavier selon D7, message « Aucune app », aucune ouverture web (F-1.4)
- [ ] Index alphabétique, si D25 le retient (non retenu, DEC-0003) : fonction pure de calcul des initiales + tests unitaires, puis composant (F-1.6)
- [x] Menu d'appui long (T-2), version jalon 1 : « Infos de l'app » seule (« Désinstaller » retiré, DEC-0009) ; titre = nom affiché
- [x] Favoris depuis le menu d'appui long, affichés sous l'horloge, maximum 7 (F-2.1, DEC-0010)
- [x] Renommage depuis le menu d'appui long, 30 caractères, réinitialisation (F-2.4, DEC-0010)
- [x] Écran Paramètres (accès selon D6) + « Changer de launcher » (`ACTION_HOME_SETTINGS`, repli `ACTION_MANAGE_DEFAULT_APPS_SETTINGS`) + « À propos » avec lien vers le dépôt `epure-launcher` (F-1.5)

### Liste de contrôle sur le téléphone
- [x] Épure sélectionnable et sélectionné comme launcher par défaut
- [x] Bouton Accueil depuis n'importe quelle app → accueil Épure, propre (recherche vidée, clavier et menu fermés)
- [x] Bouton Retour sur l'accueil sans effet ; depuis la liste → retour à l'accueil
- [x] Geste d'accès à la liste fiable, sans déclenchement involontaire
- [x] Toutes les apps lançables présentes ; installer puis désinstaller une app met la liste à jour
- [x] Recherche « eclair » trouve une app nommée « Éclair » ; recherche sans résultat → aucun navigateur ne s'ouvre
- [x] Clavier ouvert à l'ouverture de la liste (si D7 le retient)
- [ ] Index alphabétique (si retenu) : seules les lettres présentes ; toucher une lettre amène la bonne app
- [x] Appui long : menu lisible ; « Infos de l'app » ouvre la bonne fiche (« Désinstaller » retiré, DEC-0009)
- [x] Heure juste à la minute, y compris après 1 h de veille
- [x] Taille de police système agrandie : affichage lisible, sans texte tronqué gênant
- [x] TalkBack : liste, index et menu lisibles
- [x] « Changer de launcher » → retour à One UI Home en deux actions au plus ([À VÉRIFIER] sous One UI 8.0)
- [x] « À propos » : nom, version, licence et lien vers le dépôt corrects
- [x] Rotation de l'écran / mode multifenêtre : pas de plantage

**Validation** : utilisation d'Épure comme launcher principal pendant 3 jours sans blocage.

---

## Jalon 2 — Favoris, raccourcis, espaces vides, renommage

Décisions préalables : D12, D13, D14, D27 (et D8 si non tranchée).

### Tâches
- [x] Persistance des favoris (liste ordonnée, éléments « app » ou « espace ») (F-2.1, F-2.3)
- [x] Affichage des favoris sur l'accueil ; indication texte si aucun favori (F-2.1)
- [x] Menu d'appui long (T-2) : ajout de « Ajouter aux favoris / Retirer des favoris » et « Renommer »
- [x] Écran Paramètres « Favoris » : suppression, réordonnancement, insertion d'espaces vides (F-2.1, F-2.3)
- [x] Raccourcis appareil photo / téléphone / horloge avec intents par défaut (F-2.2)
- [x] Personnalisation de chaque raccourci (choix d'une app ou désactivation) (F-2.2)
- [x] Persistance des noms personnalisés (clé : composant + profil) (F-2.4)
- [x] Renommage : seul le nom personnalisé est affiché partout, y compris en titre du menu d'appui long (F-2.4)
- [x] Écran Paramètres « Apps renommées » : nom d'origine visible, modification, réinitialisation (F-2.4)
- [x] Nettoyage silencieux des favoris et noms orphelins (app désinstallée)
- Guide de premier lancement : reporté (D27, DEC-0012), non implémenté.

### Liste de contrôle sur le téléphone
- [x] Favoris, ordre et espaces conservés après redémarrage du téléphone
- [x] App favorite désinstallée → disparaît sans erreur
- [x] Chaque raccourci ouvre la bonne app ; un raccourci personnalisé pointe bien vers l'app choisie
- [x] App renommée : nom personnalisé dans la liste, les favoris, le menu et la recherche ; nom d'origine uniquement dans les Paramètres
- [x] Renommage conservé après mise à jour de l'app renommée (Play Store)
- [x] Réinitialisation → nom système restauré
- Guide : reporté (D27, DEC-0012), hors de ce jalon.

**Validation** : accueil personnalisé utilisé au quotidien pendant une semaine.

---

## Jalon 3 — Statistiques d'usage

Décisions préalables : D15, D26, D29.

### Tâches
- [x] Composant réutilisable « Écran d'autorisation » (T-1) : titre, pourquoi, ce qu'Épure ne fait pas, consignes numérotées One UI, « Ouvrir le réglage », « Plus tard », vérification au retour
- [x] Application à `PACKAGE_USAGE_STATS` : ouverture de `ACTION_USAGE_ACCESS_SETTINGS`, libellés One UI (« Données d'utilisation » → « Octroi d'autorisation »), moment de la demande selon D29 (F-3.1)
- [x] Lecture du temps par app via `UsageStatsManager` (F-3.2)
- [x] Comptage des ouvertures via `queryEvents` (`ACTIVITY_RESUMED`), avec regroupement selon D15 — logique pure + tests unitaires (F-3.2)
- [x] Exclusion d'Épure et du launcher système (F-3.2)
- [x] Écran Statistiques : total du jour, liste triée par temps (F-3.2)
- Indicateur de temps d'écran sur l'accueil : non retenu (D26, DEC-0016).
- [x] État « permission absente » géré proprement

### Liste de contrôle sur le téléphone
- [x] Écran d'autorisation : refus → seule la fonction Statistiques est inactive ; accord → active sans redémarrage
- [x] Une app utilisée 5 minutes volontairement apparaît avec 5 min (± 1)
- [x] Ouvrir une app 3 fois → 3 ouvertures
- [x] Permission retirée dans les réglages → l'écran le signale sans plantage
- [ ] Écran affiché en moins d'une seconde
- [ ] Accueil toujours instantané après l'ajout des statistiques (indicateur non retenu, DEC-0016)
- [ ] Écarts avec Bien-être numérique notés (attendus, non bloquants) — [À VÉRIFIER] rétention des données système

**Validation** : chiffres jugés plausibles sur une semaine d'usage.

---

## Jalon 4 — Minuteur, fermeture automatique, blocages

> Jalon le plus délicat (accessibilité + batterie One UI). Le découper en sessions Claude Code distinctes : 4a socle, 4b minuteur, 4c blocages.

Décisions préalables : D16, D17, D18, D19, D20, D30 ; réévaluer D2 (Room ?).

### 4a — Socle : service d'accessibilité
- [ ] Déclaration du service, configuration minimale (`TYPE_WINDOW_STATE_CHANGED`, `canRetrieveWindowContent="false"`) (F-4.0)
- [ ] Détection de l'app au premier plan (F-4.0)
- [ ] Action « retour à l'accueil » (`GLOBAL_ACTION_HOME`) + affichage d'un écran Épure, **sans** `SYSTEM_ALERT_WINDOW` (F-4.0)
- [ ] [À VÉRIFIER] Affichage fiable de l'écran Épure depuis le service sous One UI 8.0 (exception aux restrictions de lancement en arrière-plan)
- [ ] Écran d'autorisation (T-1) avant activation : ce que fait / ne fait pas le service (F-4.0)
- [ ] [À VÉRIFIER] « Paramètres restreints » après installation par Android Studio/ADB ; documenter la procédure dans le README
- [ ] Guide d'exclusion de l'optimisation de batterie One UI (selon D20) (F-4.0)
- [ ] Gestion propre du service désactivé (fonctions du jalon 4 indiquées comme inactives ; actions du menu d'appui long grisées)

### 4b — Minuteur par session et fermeture automatique
- [ ] Marquage des apps « avec minuteur » : interrupteur dans le menu d'appui long (T-2) + liste dans les Paramètres (F-4.1)
- [ ] Écran de choix de durée au lancement depuis Épure, durées selon D16 (F-4.1)
- [ ] Échéance persistante (horodatage) (F-4.1)
- [ ] Minuterie interne + vérification de l'échéance à chaque changement de fenêtre (F-4.2)
- [ ] Écran « Temps écoulé » (F-4.2)
- [ ] Comportement à l'ouverture hors Épure (selon D17) (F-4.1)
- [ ] Tests unitaires de la logique de session (actif, expiré, reprise)

### 4c — Blocage prolongé et plages horaires
- [ ] Modèle et persistance des blocages prolongés et des plages (F-4.3, F-4.4)
- [ ] Fonction pure « app bloquée à l'instant T ? » + tests unitaires (minuit, changement d'heure, plusieurs plages, durée maximale D30) (F-4.4)
- [ ] Action « Bloquer… » dans le menu d'appui long (T-2), durées selon D30 (F-4.3)
- [ ] Écran « App bloquée jusqu'à … » (F-4.3)
- [ ] Marquage d'une app bloquée dans la liste (selon décision F-4.3)
- [ ] Écrans Paramètres : créer/modifier/supprimer blocages et plages (F-4.3, F-4.4)
- [ ] Levée anticipée selon D18 (F-4.3)

### Liste de contrôle sur le téléphone
- [ ] Minuteur de 1 min : retour à l'accueil à l'échéance, app au premier plan ; écran « Temps écoulé » affiché
- [ ] Quitter puis revenir dans l'app pendant la session → session poursuivie
- [ ] Rouvrir l'app après l'échéance → nouvelle durée demandée
- [ ] Bloquer une app depuis l'appui long → blocage effectif et visible dans les Paramètres
- [ ] Rouvrir une app bloquée par les apps récentes → renvoyée à l'accueil
- [ ] Durée de blocage au-delà du maximum D30 impossible à saisir
- [ ] Plage horaire passant minuit appliquée correctement
- [ ] Écran éteint 30 min puis rallumé : minuteur et blocages toujours actifs
- [ ] Redémarrage du téléphone : blocages en cours toujours appliqués (service réactivé automatiquement)
- [ ] 48 h d'usage : aucune alerte de consommation de batterie One UI
- [ ] Désactivation du service dans les réglages → Épure l'indique, actions grisées, pas de plantage
- [ ] Manifeste toujours sans `SYSTEM_ALERT_WINDOW`

**Validation** : une semaine d'usage réel avec au moins un minuteur, un blocage et une plage horaire actifs.

---

## Jalon 5 — Filtre de notifications et niveaux de gris

Décisions préalables : D21, D22.

### Tâches — Filtre de notifications
- [ ] Déclaration du `NotificationListenerService` + écran d'autorisation (T-1) + ouverture du réglage (F-5.1)
- [ ] Choix des apps filtrées (F-5.1)
- [ ] Masquage selon D21 (suppression, report ou récapitulatif) (F-5.1)
- [ ] Notifications « en cours » ignorées sans erreur (F-5.1)
- [ ] Lien éventuel avec les plages horaires (selon D21)

### Tâches — Niveaux de gris
- [ ] Détection de `WRITE_SECURE_SETTINGS` ; option grisée + commande ADB affichée si absente (F-5.2)
- [ ] Activation/désactivation du mode monochrome (F-5.2)
- [ ] [À VÉRIFIER] Coexistence avec les niveaux de gris de One UI : ne jamais désactiver un réglage activé par Samsung (F-5.2)
- [ ] Programmation selon D22

### Liste de contrôle sur le téléphone
- [ ] Notification d'une app filtrée masquée ; celle d'une app non filtrée affichée
- [ ] Appel entrant et lecture média non affectés
- [ ] `adb shell pm grant com.alain.epure android.permission.WRITE_SECURE_SETTINGS` → option disponible
- [ ] Niveaux de gris activé/désactivé depuis Épure ; état cohérent après redémarrage
- [ ] Niveau de gris activé par One UI non perturbé par Épure

**Validation** : version 1.0 utilisée au quotidien pendant deux semaines.

---

## Transversal — Batterie et mémoire

Objectif : Épure reste léger à chaque jalon (`SPEC.md` § 4, principe 2 ; § 9). À mesurer sur le Samsung. L'exclusion de l'optimisation de batterie reste au jalon 4 (D20).

### Tâches
- [ ] Relever la consommation de batterie d'Épure sur 24 h d'usage normal (Réglages > Batterie, ou `adb shell dumpsys batterystats`)
- [ ] Relever la mémoire utilisée par Épure à l'accueil et après ouverture de la liste (`adb shell dumpsys meminfo com.alain.epure`)
- [ ] Vérifier dans le code et le manifeste qu'aucun service, alarme ou tâche planifiée ne tourne en permanence (F-1.3, F-2.1)
- [ ] Vérifier que l'horloge ne se met à jour que lorsque l'écran d'accueil est visible
- [ ] Fixer les seuils chiffrés de consommation et de mémoire après la première mesure **[À DÉCIDER : seuils]**

### Liste de contrôle sur le téléphone
- [ ] Consommation de batterie sur 24 h jugée normale par Alain
- [ ] Mémoire stable après une heure d'usage (pas de hausse continue)
- [ ] Aucun service actif à l'accueil au repos

---

## Après le jalon 5 (non planifié)

- [ ] Relecture complète de `SPEC.md` vs app réelle ; mise à jour des documents
- [ ] README final : captures d'Épure uniquement, procédures d'activation des permissions (libellés One UI), confidentialité
- [ ] Trancher D24 (Play Store, F-Droid, aucune publication)
- [ ] Si Play Store :
  - [ ] vérification des marques INPI et EUIPO, classe 9 (logiciels) — obligatoire
  - [ ] nouvelle recherche d'homonymes juste avant la publication
  - [ ] titre de la fiche : « Épure – launcher minimaliste » (DEC-0001) ; nom sur le téléphone inchangé : « Épure »
  - [ ] analyse de conformité de l'usage de l'accessibilité ; politique de confidentialité
  - [ ] fiche sans ressemblance trompeuse avec la référence, sans « minimalist phone » dans le titre, la description ou les mots-clés
- [ ] Release signée et tag Git `v1.0.0`
