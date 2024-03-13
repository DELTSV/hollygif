import type { PoolConnection } from "mariadb";
import type { Episode } from "./models";

export const getEpisodesIdsWithSeasonAndEpisodesIndexes = async (
  connection: PoolConnection,
): Promise<Episode[]> => {
  return await connection.query(
    "SELECT EPISODES.id_episode as episodeId, EPISODES.number as episodeIndex, S.number as seasonIndex FROM EPISODES INNER JOIN SEASONS S on EPISODES.season = S.id_season ",
  );
};
