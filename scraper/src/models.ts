export interface EnvConfig {
  DB_HOST: string;
  DB_PORT: number;
  DB_USER: string;
  DB_PASSWORD: string;
  DB_NAME: string;
}

export interface Line {
  name: string;
  text: string;
  info?: string;
  index: number;
}

export interface TranscriptionData {
  episodeIndex: number;
  lines: Line[];
}

export interface Episode {
  episodeId: number;
  seasonIndex: number;
  episodeIndex: number;
}

export interface Data {
  seasonIndex: number;
  episodes: TranscriptionData[];
}
