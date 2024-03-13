import mariadb, { type PoolConnection } from "mariadb";
import { type EnvConfig } from "./models";

export const getDBConnection = async (
  config: EnvConfig,
): Promise<PoolConnection> => {
  const pool = mariadb.createPool({
    host: config.DB_HOST,
    port: config.DB_PORT,
    user: config.DB_USER,
    password: config.DB_PASSWORD,
    database: config.DB_NAME,
  });
  return await pool.getConnection();
};
