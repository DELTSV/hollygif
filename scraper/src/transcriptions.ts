import { type PoolConnection } from "mariadb";
import { type Data, type TranscriptionData } from "./models";
import { getEpisodesIdsWithSeasonAndEpisodesIndexes } from "./episodes";

type EpisodeMapping = Record<number, Record<number, number>>;

type EpisodeMappingDefinition = Array<{
  seasonIndex: number;
  ranges: Array<{
    episodeIndex: number;
    range: [number, number];
  }>;
}>;

const range = (from: number, to: number): number[] =>
  [...Array(Math.floor(to - from) + 1)].map((_, i) => from + i);

const mapping: EpisodeMappingDefinition = [
  {
    seasonIndex: 5,
    ranges: [
      { episodeIndex: 1, range: [1, 6] },
      { episodeIndex: 2, range: [7, 12] },
      { episodeIndex: 3, range: [13, 18] },
      { episodeIndex: 4, range: [19, 25] },
      { episodeIndex: 5, range: [26, 32] },
      { episodeIndex: 6, range: [33, 38] },
      { episodeIndex: 7, range: [39, 44] },
      { episodeIndex: 8, range: [45, 50] },
    ],
  },
];

const getEpisodeMapping = (def: EpisodeMappingDefinition): EpisodeMapping => {
  return def.reduce((acc, season) => {
    return {
      ...acc,
      [season.seasonIndex]: season.ranges.reduce((acc, episode) => {
        const [from, to] = episode.range;

        return {
          ...acc,
          ...range(from, to).reduce(
            (acc, episodeIndex) => ({
              ...acc,
              [episodeIndex]: episode.episodeIndex,
            }),
            {},
          ),
        };
      }, {}),
    };
  }, {});
};

const applyEpisodeMapping = (
  mapping: EpisodeMapping,
  transcriptionData: Data[],
): Data[] => {
  return transcriptionData.map((season) => {
    return {
      ...season,
      episodes: season.episodes.reduce<TranscriptionData[]>((acc, episode) => {
        if (mapping[season.seasonIndex]?.[episode.episodeIndex] == null) {
          return [...acc, episode];
        }

        const episodeIndex = mapping[season.seasonIndex][episode.episodeIndex];

        const existingEpisode = acc.find(
          (e) => e.episodeIndex === episodeIndex,
        );

        if (existingEpisode == null) {
          return [
            ...acc,
            {
              episodeIndex,
              lines: episode.lines,
            },
          ];
        }

        return acc.map((e) =>
          e.episodeIndex === episodeIndex
            ? {
                ...e,
                lines: [
                  ...e.lines,
                  ...episode.lines.map((line, index) => ({
                    ...line,
                    index: e.lines.length + index,
                  })),
                ],
              }
            : e,
        );
      }, []),
    };
  });
};

export const insertTranscriptions = async (
  databaseConnection: PoolConnection,
  transcriptionData: Data[],
): Promise<void> => {
  const episodes =
    await getEpisodesIdsWithSeasonAndEpisodesIndexes(databaseConnection);

  const episodeMapping = getEpisodeMapping(mapping);

  await Promise.all(
    applyEpisodeMapping(episodeMapping, transcriptionData).map(
      async (season) => {
        await Promise.all(
          season.episodes.map(async (episode) => {
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

              if (values.length === 0) {
                console.error(
                  `No transcriptions found for episode ${episode.episodeIndex} from season ${season.seasonIndex}`,
                );
                return;
              }

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
          }),
        );
      },
    ),
  );
};
