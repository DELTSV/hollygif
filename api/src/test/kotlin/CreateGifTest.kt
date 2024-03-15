import fr.imacaron.gif.api.usecases.gif.CreateGif
import fr.imacaron.gif.api.usecases.gif.GifCreation
import fr.imacaron.gif.shared.gif.SceneRepository
import fr.imacaron.gif.shared.infrastrucutre.FFMPEG
import fr.imacaron.gif.shared.infrastrucutre.FileManager
import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SceneEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SeasonEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SeriesEntity
import fr.imacaron.gif.shared.search.EpisodeRepository
import fr.imacaron.gif.shared.search.SeasonRepository
import fr.imacaron.gif.shared.search.SeriesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateGifTest {
	val fileManager = mockk<FileManager>()
	val seriesRepository = mockk<SeriesRepository>()
	val seasonRepository = mockk<SeasonRepository>()
	val episodeRepository = mockk<EpisodeRepository>()
	val sceneRepository = mockk<SceneRepository>()

	private val seriesEntity = mockk<SeriesEntity> {
		every { id } returns 1
		every { name } returns "kaamelott"
		every { logo } returns "logo"
	}
	private val seasonEntity = mockk<SeasonEntity> {
		every { id } returns 1
		every { number } returns 1
		every { this@mockk.series } returns seriesEntity
	}
	private val episodeEntity = mockk<EpisodeEntity> {
		every { id } returns 1
		every { number } returns 1
		every { width } returns 1920
		every { height } returns 1080
		every { fps } returns 25
		every { title } returns "Heat"
		every { this@mockk.season } returns seasonEntity
		every { duration } returns 257.25
	}

	private val sceneEntity = mockk<SceneEntity> {
		every { id } returns 1
		every { index } returns 1
		every { start } returns 0.0
		every { end } returns 10.0
		every { episode } returns episodeEntity
	}

	private val createGif = CreateGif(
		fileManager,
		seriesRepository,
		seasonRepository,
		episodeRepository,
		sceneRepository
	)

	@Test
	fun `should create gif`() {
		val series = "kaamelott"
		val season = 1
		val episode = 1
		val time = 70.0

		mockkObject(FFMPEG)

		every { seriesRepository.getSeries(series) } returns Result.success(seriesEntity)
		every { seasonRepository.getSeriesSeason(seriesEntity, season) } returns Result.success(seasonEntity)
		every { episodeRepository.getSeasonEpisode(seasonEntity, episode) } returns Result.success(episodeEntity)
		every { sceneRepository.getEpisodeSceneAt(episodeEntity, time) } returns Result.success(sceneEntity)
		every { fileManager.fileExist(any()) } returns true
		every { fileManager.createFile(any(), any()) } returns Unit
		every { FFMPEG.getTextLength(any(), any()) } returns 150.0
		every { FFMPEG.writeText(any(), any(), any()) } returns Unit
		every { FFMPEG.convertToGif(any(), any()) } returns mockk()

		val gifCreation = GifCreation(
			series,
			season,
			episode,
			"1:10",
			"Bonjour"
		)

		val result = createGif(gifCreation, 156)

		assertTrue(result.isSuccess)

		unmockkObject(FFMPEG)
	}

	@Test
	fun `should not create gif with invalid timecode`() {
		val series = "kaamelott"
		val season = 1
		val episode = 1
		val time = 70.0

		mockkObject(FFMPEG)
		every { seriesRepository.getSeries(series) } returns Result.success(seriesEntity)
		every { seasonRepository.getSeriesSeason(seriesEntity, season) } returns Result.success(seasonEntity)
		every { episodeRepository.getSeasonEpisode(seasonEntity, episode) } returns Result.success(episodeEntity)
		every { sceneRepository.getEpisodeSceneAt(episodeEntity, time) } returns Result.success(sceneEntity)
		every { fileManager.fileExist(any()) } returns true
		every { fileManager.createFile(any(), any()) } returns Unit
		every { FFMPEG.getTextLength(any(), any()) } returns 150.0
		every { FFMPEG.writeText(any(), any(), any()) } returns Unit

		val gifCreation = GifCreation(
			series,
			season,
			episode,
			"1.100:100",
			"Bonjour"
		)

		val result = createGif(gifCreation, 156)

		assertFalse(result.isSuccess)
		assertNotNull(result.exceptionOrNull())

		unmockkObject(FFMPEG)
	}

	@Test
	fun `should not create gif with invalid duration`() {
		val series = "kaamelott"
		val season = 1
		val episode = 1
		val time = 70.0

		mockkObject(FFMPEG)
		every { seriesRepository.getSeries(series) } returns Result.success(seriesEntity)
		every { seasonRepository.getSeriesSeason(seriesEntity, season) } returns Result.success(seasonEntity)
		every { episodeRepository.getSeasonEpisode(seasonEntity, episode) } returns Result.success(episodeEntity)
		every { sceneRepository.getEpisodeSceneAt(episodeEntity, time) } returns Result.success(sceneEntity)
		every { fileManager.fileExist(any()) } returns true
		every { fileManager.createFile(any(), any()) } returns Unit
		every { FFMPEG.getTextLength(any(), any()) } returns Double.NaN
		every { FFMPEG.writeText(any(), any(), any()) } returns Unit

		val gifCreation = GifCreation(
			series,
			season,
			episode,
			"1.100:100",
			"Bonjour"
		)

		val result = createGif(gifCreation, 156)

		assertFalse(result.isSuccess)
		assertNotNull(result.exceptionOrNull())

		unmockkObject(FFMPEG)
	}
}