# Kaamelott gif

Un bot discord pour créer des gifs kaamelott avec FFMPEG

## Technologies

* FFMPEG pour l'édition vidéo
* Kotlin
* Kord pour le bot discord

## Utilisation

`/kaagif 'livre' 'episode' 'timecode' 'text'`

`livre` -> Le livre (saison) de kaamelott dans lequel se trouve l'épisode

`episode` -> Le numéro de l'épisode voulu

`timecode` -> Le time code de la scène dans l'épisode au format `mm:ss`

`text` -> Le text à ajouter sur le gif

## Todo

- [ ] Ajouter une commande pour upload certains gif automatiquement sur ~~tenor~~ (pas prévu mais faisable) giphy
- [ ] Ajouter une commande pour afficher les gifs disponible (déjà créer)
- [ ] Améliorer le message durant la création du gif (avec des status et leur évolution)
- [ ] Améliorer la gestion d'erreur
- [ ] Conserver les mp4 sans texte
- [ ] Sauvegarder les infos dans une base
  - [ ] Parser les infos déjà disponible vers la base
- [ ] Sauvegarder les appels au bot dans une base
- [ ] Faire un site web pour présenter tout les scènes de tout les épisode
  - [ ] Générer la première image de chaque scène d'un épisode
  - [ ] Utiliser les MP4 des scènes quand ils existent pour présenter les scènes
  - [ ] Sauvegarder les fichiers sur un S3
- [ ] Ajouter une réponse éphémere à la commande permettant de supprimer ou de demander la sauvegarde d'un gif
- [ ] Fixer les problèmes de textes
- [ ] Limiter la tailler des textes
- [ ] Permettre un texte vide
- [ ] Rendre la commande spécifique aux guildes
- [ ] Utiliser une interface pour la récupération des fichiers
- [ ] Préparer plusieurs méthode de récupération de fichier (S3, SFTP …)
- [ ] Faire en sorte que le bot répondent aux commandes sur les serveurs autorisé uniquement
- [ ] Ajouter une interface admin au site pour les configuration de base
- [ ] Permettre de configurer la font
- [ ] Permettre de configurer le wording du bot
