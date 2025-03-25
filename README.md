# Kaamelott gif

Un projet pour faire des gif Kaamelott (et autre potentiellement) contenant:
- Un bot discord
- Un site web
- Une API
- Une app mobile

## Technologies

* FFMPEG pour l'édition vidéo
* Kotlin
  * KTor pour l'API
  * Kord pour le bot discord
  * Compose Multiplatform pour l'app mobile
* TypeScript pour le site web
  * React
  * Tailwind

## Utilisation

`/kaagif 'livre' 'episode' 'timecode' 'text'`

`livre` -> Le livre (saison) de kaamelott dans lequel se trouve l'épisode

`episode` -> Le numéro de l'épisode voulu

`timecode` -> Le time code de la scène dans l'épisode au format `mm:ss[.mmm]`

`text` -> Le text à ajouter sur le gif

## Todo

- [ ] Ajouter une commande pour upload certains gif automatiquement sur ~~tenor~~ (pas prévu, mais faisable) giphy
- [ ] Ajouter une commande pour afficher les gifs disponible (déjà créer)
- [ ] Améliorer le message durant la création du gif (avec des status et leur évolution)
- [x] Améliorer la gestion d'erreur
- [x] Conserver les mp4 sans texte
- [x] Sauvegarder les infos dans une base
  - [x] Parser les infos déjà disponibles vers la base
- [x] Sauvegarder les appels au bot dans une base
- [X] Faire un site web pour présenter toutes les scènes de tous les épisodes
  - [ ] Générer la première image de chaque scène d'un épisode
  - [x] Utiliser les MP4 des scènes quand ils existent pour présenter les scènes
  - [X] Sauvegarder les fichiers sur un S3
- [ ] Ajouter ~~une réponse éphémere à la commande~~ une commande de message permettant de supprimer ou de demander la sauvegarde d'un gif
- [X] Fixer les problèmes de textes
- [ ] Limiter la taille des textes
- [x] Permettre un texte vide
- [ ] Rendre la commande spécifique aux guildes
- [X] Utiliser une interface pour la récupération des fichiers
- [ ] Préparer plusieurs méthodes de récupération de fichier (S3, SFTP …)
- [ ] Faire en sorte que le bot répondent aux commandes sur les serveurs autorisées uniquement
- [ ] Ajouter une interface admin au site pour les configurations de base
- [ ] Permettre de configurer la font
- [ ] Permettre de configurer le wording du bot
- [x] Héberger les gifs sur un site pour avoir des gifs de toutes tailles
- [x] Ajouter une commande pour voir son historique de création de gif
- [ ] Supprimer automatiquement les gifs après un certain temps (3 jours)
