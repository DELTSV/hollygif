package fr.imacaron.gif.shared.gif

import fr.imacaron.gif.shared.infrastrucutre.repository.SceneEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity

interface SceneRepository {
    fun getEpisodeScenes(episode: EpisodeEntity): Result<List<SceneEntity>>
    fun getEpisodeScene(episode: EpisodeEntity, index: Int): Result<SceneEntity>
    fun getEpisodeSceneAt(episode: EpisodeEntity, at: Double): Result<SceneEntity>
    fun addEpisodeScene(info: SceneEntity): Result<SceneEntity>
}