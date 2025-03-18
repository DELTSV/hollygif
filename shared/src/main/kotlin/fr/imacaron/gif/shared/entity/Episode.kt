package fr.imacaron.gif.shared.entity

import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.repository.EpisodeEntity
import fr.imacaron.gif.shared.repository.SceneRepository
import fr.imacaron.gif.shared.repository.SeasonEntity
import fr.imacaron.gif.shared.repository.TranscriptionRepository

class Episode(
	private val sceneRepository: SceneRepository,
	private val transcriptionRepository: TranscriptionRepository,
	val entity: EpisodeEntity,
	val season: Season
) {
	val title: String
		get() = entity.title

	val fps: Int
		get() = entity.fps

	val width: Int
		get() = entity.width

	val height: Int
		get() = entity.height

	val number: Int
		get() = entity.number

	val duration: Double
		get() = entity.duration

	val scenes = SceneList()

	val script: String
		get() = entity.script

	val transcriptions = TranscriptionList()

	inner class SceneList {
		operator fun get(i: Int): Scene = sceneRepository.getEpisodeScene(entity, i).getOrThrow().let { Scene(it, this@Episode) }

		fun getSceneFromTime(time: Double): Scene? {
			return sceneRepository.getEpisodeSceneAt(entity, time).getOrElse {
				if(it is NotFoundException) {
					return null
				}
				throw Exception()
			}.let { Scene(it, this@Episode) }
		}

		val size by lazy {
			sceneRepository.getEpisodeScenesCount(entity).getOrElse { 0 }
		}
	}

	inner class TranscriptionList {
		operator fun get(index: Int): Transcription =
			Transcription(transcriptionRepository.getEpisodeTranscription(entity, index).getOrThrow(), this@Episode)

		operator fun invoke(): List<Transcription> = transcriptionRepository.getEpisodeTranscriptions(entity)
			.getOrThrow().map { Transcription(it, this@Episode) }
	}
}