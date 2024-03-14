package fr.imacaron.gif.shared.gif

import fr.imacaron.gif.shared.infrastrucutre.repository.*

interface GifRepository {
    fun getGif(id: Int): Result<GifEntity>
    fun getUserGifs(user: String, page: Int, pageSize: Int): List<GifEntity>
    fun getSceneGifs(sceneEntity: DbSceneEntity, page: Int, pageSize: Int): List<GifEntity>
    fun getEpisodeGifs(episodeEntity: EpisodeEntity, page: Int, pageSize: Int): List<GifEntity>
    fun getSeasonGifs(seasonEntity: SeasonEntity, page: Int, pageSize: Int): List<GifEntity>
    fun getSeriesGifs(seriesEntity: SeriesEntity, page: Int, pageSize: Int): List<GifEntity>
    fun addGif(gifEntity: DbGifEntity): DbGifEntity
}