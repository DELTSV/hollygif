import { type EnvConfig } from "./models";

export const loadEnv = (): EnvConfig => {
  return {
    DB_HOST: process.env.DB_HOST ?? "",
    DB_PORT: parseInt(process.env.DB_PORT ?? ""),
    DB_USER: process.env.DB_USER ?? "",
    DB_PASSWORD: process.env.DB_PASSWORD ?? "",
    DB_NAME: process.env.DB_NAME ?? "",
  };
};
