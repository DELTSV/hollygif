package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.like
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.varchar

class TranscriptionRepository(
    private val db: Database
) {
    fun getEpisodeTranscriptions(episodeEntity: EpisodeEntity): Result<List<TranscriptionEntity>> =
        Result.success(db.transcriptions.filter { it.episode eq episodeEntity.id }.map { it })

    fun getEpisodeTranscription(episodeEntity: EpisodeEntity, index: Int): Result<TranscriptionEntity> =
        db.transcriptions.find { (it.episode eq episodeEntity.id) and (it.index eq index) }?.let {
            Result.success(it)
        } ?: Result.failure(NotFoundException("Transcription not found"))

    fun searchTextInTranscription(search: String, page: Int, pageSize: Int): Result<Pair<List<TranscriptionEntity>, Int>> =
        Result.success(
            db.transcriptions.filter { it.text like search }.drop(page * pageSize).take(pageSize).map { it }
            to
            db.transcriptions.count { it.text like search }
        )
}

object TranscriptionsTable: Table<TranscriptionEntity>("TRANSCRIPTIONS") {
    val episode = int("id_episode").primaryKey().references(EpisodeTable) { it.episode }
    val index = int("index").primaryKey().bindTo { it.index }
    val text = text("text").bindTo { it.text }
    val info = varchar("info").bindTo { it.info }
    val speaker = varchar("speaker").bindTo { it.speaker }
}

interface TranscriptionEntity: Entity<TranscriptionEntity> {
    val episode: EpisodeEntity
    val index: Int
    val text: String
    val info: String?
    val speaker: String

    companion object: Entity.Factory<TranscriptionEntity>()
}

val Database.transcriptions get() = this.sequenceOf(TranscriptionsTable)