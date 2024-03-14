package fr.imacaron.gif.bot.commands

import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.suggest
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestException
import fr.imacaron.gif.bot.*
import fr.imacaron.gif.shared.ErrorWhileDrawingText
import fr.imacaron.gif.shared.NotEnoughTimeException
import fr.imacaron.gif.shared.search.Series
import fr.imacaron.gif.shared.infrastrucutre.repository.DbGifEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.DbGifRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.GifStatus
import io.ktor.util.logging.*
import kotlinx.datetime.Clock
import java.io.File
import java.util.*

class GifCommand(
	private val kord: Kord,
	private val episodeNumbers: Map<Int, Int>,
	private val kaamelott: Series,
	private val gifRepository: DbGifRepository
) {
	suspend fun init() {
		kord.createGlobalChatInputCommand("kaagif", "Une commande pour créer des gif kaamelott") {
			integer("livre", "Livre") {
				required = true
				for (i in episodeNumbers.keys) {
					choice("Livre $i", i.toLong())
				}
			}
			integer("episode", "Épisode") {
				required = true
				autocomplete = true
			}
			string("timecode", "Timecode sous la forme mm:ss[.mmm]") {
				required = true
			}
			string("text", "Texte")
		}
	}

	suspend fun autoCompleteEpisode(interaction: AutoCompleteInteraction) {
		val options = interaction.command.data.options.value ?: return
		if(options[1].value.value?.focused.value == true) {
			val ep = interaction.command.strings["episode"] ?: run {
				logger.debug("No episode in command")
				return
			}
			val choices = interaction.command.integers["livre"]?.let { livre ->
				val number = episodeNumbers[livre.toInt()] ?: run {
					logger.debug("Missing book in episodeNumbers")
					return
				}
				(1..number).asSequence().map {
					logger.debug("Choice(\"Épisode $it\", Optional(null), $it)")
					Choice.IntegerChoice("Épisode $it", Optional(null), it.toLong())
				}.filter {
					(ep in it.value.toString()).apply {
						logger.debug("${it.value} is keep with ep=$ep: $this")
					}
				}
			} ?: (1..25).asSequence().map {
				logger.debug("Choice(\"Épisode $it\", Optional(null), $it)")
				Choice.IntegerChoice("Épisode $it", Optional(null), it.toLong())
			}
			interaction.suggest(choices.take(25).toList())
		}
	}

	suspend operator fun invoke(interaction: GuildChatInputCommandInteraction) {
		val user = interaction.user
		logger.debug("Receive command from ${user.effectiveName}. Defer it")
		val resp = interaction.deferPublicResponse()
		val name = UUID.randomUUID()
		try {
			val epNum = interaction.command.integers["episode"]?.toInt() ?: run {
				logger.debug("No ep num in command")
				resp.respondBadCommand(user)
				return
			}
			val book = interaction.command.integers["livre"]?.toInt() ?: run {
				logger.debug("No book in command")
				resp.respondBadCommand(user)
				return
			}
			if (book <= 0 || book > kaamelott.seasons.size) {
				resp.respondBookError(user)
				return
			}
			val season = kaamelott.seasons[book]
			if (epNum <= 0 || book > season.episodes.size) {
				resp.respondEpNumError(user)
				return
			}
			val timecode = interaction.command.strings["timecode"] ?: run {
				logger.debug("No timecode in command")
				resp.respondBadCommand(user)
				return
			}
			val ep = season.episodes[epNum]
			val time = try {
				timecode.split(":").let {
					if (it.size != 2 || it[1].length != 2) {
						resp.respondTimecode(user)
						return
					}
					it[0].toInt() * 60 + it[1].toDouble()
				}
			} catch (e: NumberFormatException) {
				logger.debug("Time code not only numbers")
				resp.respondTimecode(user)
				return
			}
			if (time > ep.duration) {
				logger.debug("Timecode greater than episode duration")
				resp.respondTropLoin(ep.duration, time, user)
				return
			}
			if (time < 0) {
				logger.debug("Timecode less than 0")
				resp.respondTropCourt(user)
				return
			}
			val text = interaction.command.strings["text"] ?: run {
				logger.debug("No text in command")
				""
			}
			val scene = ep.scenes.getSceneFromTime(time) ?: run {
				logger.debug("Scene doesn't exist")
				resp.respondNoScene(user, timecode)
				return
			}
			logger.debug("Getting scene {}, starting at {} and last {}", scene, scene.start, scene.duration)
			logger.debug("Creating meme")
			scene.createMeme(text).collect {
				if(it.error != null) {
					logger.debug("Creating meme failed", it.error)
					when (it.error) {
						is NotEnoughTimeException -> {
							logger.debug("Not enough time to create scene")
							resp.respondPortionTropCourte(user)
						}
						is ErrorWhileDrawingText -> {
							logger.debug("Error while drawing text on scene")
							resp.respondTexteErreur(user)
						}
						else -> {
							logger.debug("Unknown error")
							resp.respondUnknownError(user)
						}
					}
					return@collect
				}
				if(it.result != null) {
					val gifEntity = DbGifEntity {
						this.scene = scene.entity
						this.date = Clock.System.now()
						this.text = text
						this.user = user.id.toString()
						this.timecode = timecode
						this.status = GifStatus.SUCCESS
					}
					gifRepository.addGif(gifEntity)
					resp.respond {
						embed {
							title = "Gif créé"
							author {
								this.name = user.effectiveName
								val avatar = user.memberAvatar?.cdnUrl?.toUrl() ?:
								user.avatar?.cdnUrl?.toUrl() ?:
								if(user.discriminator == "0") {
									"https://discord.com/api/v10/embed/avatars/${user.id.toString().toLong().shr(22) % 6}.png"
								} else {
									"https://discord.com/api/v10/embed/avatars/${user.discriminator.toInt() % 5}.png"
								}
								this.icon = avatar
							}
							this.field {
								this.name = "Livre"
								this.value = book.toString()
								this.inline = true
							}
							this.field {
								this.name = "Épisode"
								this.value = epNum.toString()
								this.inline = true
							}
							this.field {
								this.name = "Time Code"
								this.value = timecode
								this.inline = true
							}
							if (text.isNotBlank()) {
								this.field {
									this.name = "Texte"
									this.value = text
								}
							}
							logger.debug("IMAGE URL = $API/api/gif/file/${it.result}")
							image = "$API/api/gif/file/${it.result}"
							url = "$APP/gif/${gifEntity.id}"
						}
					}
				} else if(it.gif) {
					resp.respond {
						content = "Le gif est prêt"
					}
				} else if(it.text) {
					resp.respond {
						content = "Le texte est écrit"
					}
				} else if(it.textLength) {
					resp.respond {
						content = "Les mesures sont prises"
					}
				} else if(it.scene) {
					resp.respond {
						content = "La scène a été créer"
					}
				}
			}
		} catch (e: Exception) {
			when (e) {
				is KtorRequestException -> {
					if (e.status.code == 413) {
						val size = File("gif/$name.gif").length()
						logger.warn("File $name.gif too large, ${size}B")
						resp.repondTropGros(user, size, "$name.gif")
					} else {
						logger.error(e)
						resp.respondUnknownError(user)
					}
				}

				else -> {
					logger.error(e)
					resp.respondUnknownError(user)
				}
			}
		}
	}
}