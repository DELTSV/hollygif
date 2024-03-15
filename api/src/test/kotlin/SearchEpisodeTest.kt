import fr.imacaron.gif.api.usecases.search.SearchEpisode
import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SeasonEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SeriesEntity
import fr.imacaron.gif.shared.search.EpisodeRepository
import fr.imacaron.gif.shared.search.SeasonRepository
import fr.imacaron.gif.shared.search.SeriesRepository
import io.ktor.server.plugins.*
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SearchEpisodeTest {

	val seriesRepository = mockk<SeriesRepository>()
	val seasonRepository = mockk<SeasonRepository>()
	val episodeRepository = mockk<EpisodeRepository>()

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

	private val searchEpisode = SearchEpisode(
		seriesRepository,
		seasonRepository,
		episodeRepository
	)

	@Test
	fun `should find episode`() {
		val series = "kaamelott"
		val season = 1
		val episode = 1

		every { seriesRepository.getSeries(series) } returns Result.success(seriesEntity)
		every { seasonRepository.getSeriesSeason(seriesEntity, season) } returns Result.success(seasonEntity)
		every { episodeRepository.getSeasonEpisode(seasonEntity, episode) } returns Result.success(episodeEntity)

		val result = searchEpisode(series, season, episode)

		assertTrue(result.isSuccess)
	}

	@Test
	fun `should not find non existent episode`() {
		val series = "kaamelott"
		val season = 1
		val episode = -1

		every { seriesRepository.getSeries(series) } returns Result.success(seriesEntity)
		every { seasonRepository.getSeriesSeason(seriesEntity, season) } returns Result.success(seasonEntity)
		every { episodeRepository.getSeasonEpisode(seasonEntity, episode) } returns Result.failure(NotFoundException())

		val result = searchEpisode(series, season, episode)

		assertFalse(result.isSuccess)
		assertNotNull(result.exceptionOrNull())
	}

	@Test
	fun `should not find non existent season`() {
		val series = "kaamelott"
		val season = -1
		val episode = 1

		every { seriesRepository.getSeries(series) } returns Result.success(seriesEntity)
		every { seasonRepository.getSeriesSeason(seriesEntity, season) } returns Result.failure(NotFoundException())
		every { episodeRepository.getSeasonEpisode(seasonEntity, episode) } returns Result.success(episodeEntity)

		val result = searchEpisode(series, season, episode)

		assertFalse(result.isSuccess)
		assertNotNull(result.exceptionOrNull())
	}
}