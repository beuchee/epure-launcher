# CLAUDE.md — Épure

Launcher Android minimaliste (écran d'accueil en texte seul) pour réduire le temps d'écran. Usage personnel, code public sur GitHub (`epure-launcher`, GPL v3).

## Avant toute tâche

1. Lire `docs/SPEC.md` (exigences `F-x.y` / `T-n`, § 5 bis pour la référence visuelle), `docs/ROADMAP.md` (jalon en cours, tâches cochables) et `docs/DECISIONS.md` (décisions prises).
2. Identifier le jalon et la tâche concernés. Ne travailler que sur ce jalon : ne pas anticiper les jalons suivants.
3. Vérifier que les décisions `Dx` dont dépend la tâche sont consignées dans `DECISIONS.md`. Sinon : **s'arrêter et demander**. Ne jamais implémenter un point marqué **[À DÉCIDER]** comme s'il était tranché.
4. Un point **[À VÉRIFIER]** se teste sur le téléphone ; consigner le résultat (`DECISIONS.md` ou `SPEC.md`).

## Interlocuteur

- Alain, clinicien-chercheur, pas développeur Android de métier.
- Échanges en **français**, clairs, sans jargon inutile ; expliquer brièvement le pourquoi d'un choix technique.
- **Code, noms et commentaires de code en anglais.** Textes de l'interface en français, dans `res/values/strings.xml` uniquement (aucune chaîne en dur).
- Proposer la solution simple avant la sophistiquée. Signaler clairement : permissions sensibles, limites des API Android, particularités de One UI, contraintes du Play Store.

## Identité (DEC-0001)

| Élément | Valeur |
|---|---|
| Nom affiché (`app_name`) | « Épure » |
| Nom technique | Epure (sans accent) |
| `applicationId` / paquet | `com.alain.epure` |
| Dépôt | `epure-launcher` |
| Licence | GPL v3 |

« minimalist phone® » est une marque déposée (QQ42 Labs UG) : ne jamais l'utiliser comme nom, dans le code, les ressources ou les textes, ni suggérer une affiliation. Seule la mention factuelle « inspiré de Minimalist Phone » est admise, dans le README. Aucune rétro-ingénierie, aucune reprise de code, de textes, d'icônes ou de visuels de cette app.

## Choix techniques figés

- Kotlin + Jetpack Compose (Material 3, thème noir personnalisé). **Pas de code natif, pas de Rust, pas de NDK.**
- `minSdk` 31, `targetSdk` / `compileSdk` 36.
- Appareil de test : Samsung, Android 16 / One UI 8.0.
- Une seule activité ; `ViewModel` + `StateFlow` ; pas de framework d'injection (classe `AppContainer`).
- Logique pure (recherche, tri, index, minuteur, évaluation des blocages) dans `domain/`, sans dépendance Android, **couverte par des tests unitaires**.
- Contraintes `targetSdk` 36 : edge-to-edge (`WindowInsets`), retour prédictif (`BackHandler`, jamais `onBackPressed()`), `<queries>` pour lister les apps.

## Sobriété et confidentialité

- Fond noir, texte seul, aucune icône d'app, aucune animation superflue.
- Aucun service permanent sans nécessité ; données d'usage lues à la demande ; pas d'alarme ni de tâche planifiée si une évaluation à la demande suffit.
- **Toute nouvelle dépendance** doit être justifiée dans `DECISIONS.md` avant d'être ajoutée. Pas de bibliothèque d'analytics, de crash reporting ni de publicité.
- Aucune donnée ne quitte le téléphone.
- Permissions **interdites** dans le manifeste : `INTERNET`, `SYSTEM_ALERT_WINDOW`, `QUERY_ALL_PACKAGES`. Toute autre permission doit correspondre à une ligne du tableau `SPEC.md` § 8.
- Service d'accessibilité : `canRetrieveWindowContent="false"`, événements `TYPE_WINDOW_STATE_CHANGED` uniquement, aucun enregistrement.
- One UI : ne pas dupliquer ni contrarier les outils Samsung (niveaux de gris, Bien-être numérique) ; prévoir l'exclusion de l'optimisation de batterie pour le jalon 4.

## Commandes

> Poste de développement : **macOS** (shell zsh). Commandes à lancer depuis la racine du dépôt.
> `adb` est fourni par le SDK Android dans `~/Library/Android/sdk/platform-tools` : ce dossier doit figurer dans le `PATH` (ex. `export PATH="$PATH:$HOME/Library/Android/sdk/platform-tools"` dans `~/.zshrc`).

```bash
./gradlew assembleDebug          # build debug
./gradlew installDebug           # build + installation sur le téléphone connecté
./gradlew testDebugUnitTest      # tests unitaires
./gradlew lint                   # analyse statique
adb devices                      # vérifier que le téléphone est connecté
adb logcat --pid=$(adb shell pidof -s com.alain.epure)   # journaux d'Épure
# Jalon 5 uniquement (niveaux de gris) :
adb shell pm grant com.alain.epure android.permission.WRITE_SECURE_SETTINGS
```

Avant de déclarer une tâche terminée : build sans erreur, tests unitaires verts, lint sans nouvelle erreur. Puis donner à Alain la liste de contrôle à vérifier sur le téléphone (`ROADMAP.md`).

## Dépôt public : règles Git

- Ne **jamais** committer : clé de signature (`*.jks`, `*.keystore`) ni ses mots de passe, `local.properties`, fichier contenant des informations personnelles, captures ou visuels de Minimalist Phone.
- Vérifier `git status` et le contenu indexé avant chaque commit.
- Commits avec l'adresse e-mail « noreply » de GitHub.
- **Commit seulement après validation par Alain sur le téléphone.** Messages de commit en anglais, courts, à l'impératif (ex. `Add text-only app list`).
- Ne jamais pousser (`git push`), réécrire l'historique ni forcer une opération sans demande explicite.
- Ressources tierces uniquement sous licence compatible avec la redistribution (Google Fonts, Material Symbols).
- Code repris d'Olauncher ou de KISS Launcher : vérifier la licence du fichier, conserver les avis de copyright, mentionner l'origine en commentaire et dans « À propos ».

## Tenue des documents

- Cocher dans `ROADMAP.md` les tâches réalisées **et validées sur le téléphone**.
- Une décision tranchée → nouvelle entrée `DEC-xxxx` dans `DECISIONS.md` (modèle en tête du fichier), puis retirer la marque [À DÉCIDER] dans `SPEC.md`.
- Ne pas modifier `SPEC.md` pour l'aligner sur le code : signaler l'écart et demander.
- Une session Claude Code par jalon ou par tâche (jalon 4 : sessions 4a, 4b, 4c).
