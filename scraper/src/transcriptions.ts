import { getEpisodesIdsWithSeasonAndEpisodesIndexes } from "./episodes";
import { type PoolConnection } from "mariadb";
import { type Data } from "./models";

export const insertTranscriptions = async (
  databaseConnection: PoolConnection,
  transcriptionData: Data[],
): Promise<void> => {
  const episodes =
    await getEpisodesIdsWithSeasonAndEpisodesIndexes(databaseConnection);

  for (const season of transcriptionData) {
    for (const episode of season.episodes) {
      const episodeId = episodes.find(
        (episodeInDB) =>
          episodeInDB.episodeIndex === episode.episodeIndex &&
          episodeInDB.seasonIndex === season.seasonIndex,
      )?.episodeId;

      if (episodeId != null) {
        await databaseConnection.query(
          "DELETE FROM TRANSCRIPTIONS WHERE episode_id = ?",
          [episodeId],
        );

        const values = episode.lines.map((line) => [
          line.info,
          line.index,
          line.name,
          line.text,
          episodeId,
        ]);

        await databaseConnection.batch(
          "INSERT INTO TRANSCRIPTIONS (info, `index`, speaker, text, episode_id) VALUES (?, ?, ?, ?, ?)",
          values,
        );

        console.log(
          `${episode.lines.length} transcriptions inserted for episode ${episode.episodeIndex} from season ${season.seasonIndex}`,
        );
      } else {
        console.error(
          `Episode ${episode.episodeIndex} from season ${season.seasonIndex} not found in database`,
        );
      }
    }
  }
};
