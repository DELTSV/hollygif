package fr.imacaron.gif.bot

import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.addFile
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.seconds

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTimecode(user: User) {
    respond {
        content = "<@${user.id}>\nLe time code doit être sous la forme `mm:ss`"
        addFile(Path("pas_compliquer.gif"))
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTropLoin(duration: Double, demand: Int, user: User) {
    respond {
        val min = duration.seconds.inWholeMinutes
        content = "<@${user.id}>\nL'épisode fait ${min}min${duration.toInt()%60}s donc la scène à la ${demand/60}min et ${demand%60}s. Bon voilà quoi"
        addFile(Path("pas_compliquer.gif"))
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTropCourt(user: User) {
    respond {
        content = "<@${user.id}>\nEuh l'épisode il commence à 00:00 pas avant"
        addFile(Path("pas_compliquer.gif"))
    }
}


suspend fun DeferredPublicMessageInteractionResponseBehavior.respondUnknownError(user: User) {
    respond {
        content = "<@${user.id}>\nUne erreur est survenue"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondBadCommand(user: User) {
    respond {
        content = "<@${user.id}>\nNormalement c'est pas possible de faire ça"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondPortionTropCourte(user: User) {
    respond {
        content = "<@${user.id}>\nLa portion choisie est trop courte, veuillez en choisir une autre"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTexteErreur(user: User) {
    respond {
        content = "<@${user.id}>\nErreur lors de l'ajout du texte"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.repondTropGros(user: User, fileSize: Long, fileName: String) {
    respond {
        var size = fileSize
        var unit = 0
        while(size > 1024) {
            size /= 1024
            unit++
        }
        val stringUnit = when(unit) {
            0 -> "o"
            1 -> "Ko"
            2 -> "Mo"
            3 -> "Go"
            4 -> "To"
            else -> "?"
        }
        content = "<@${user.id}>\nLe gif il est trop gros là\nFichier: `$fileName: $size$stringUnit`\nContactez macaron pour avoir le gif si vous le souhaitez"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondBookError(user: User) {
    respond {
        content = "<@${user.id}>\nErreur, ce livre n'existe pas"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondEpNumError(user: User) {
    respond {
        content = "<@${user.id}>\nErreur, cet épisode n'existe pas"
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondNoScene(user: User, timeCode: String) {
    respond {
        content = "<@${user.id}>\nErreur, aucune scene correspond à ce timecode $timeCode "
    }
}