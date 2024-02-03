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

- [ ] Ajouter une commande pour upload certains gif automatiquement sur ~~tenor~~ (impossible) giphy
- [ ] Ajouter une commande pour afficher les gifs disponible (déjà créer)
- [ ] Améliorer le message durant la création du gif (avec des status et leur évolution)
- [ ] Améliorer la gestion d'erreur