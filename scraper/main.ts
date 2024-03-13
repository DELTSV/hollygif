import { scrap } from "./src/scrapper";
import { getDBConnection } from "./src/database";

import "dotenv/config";
import { loadEnv } from "./src/env";
import { insertTranscriptions } from "./src/transcriptions";

void (async () => {
  const transcriptionData = await scrap();

  const envConfig = loadEnv();
  const databaseConnection = await getDBConnection(envConfig);

  await insertTranscriptions(databaseConnection, transcriptionData);

  await databaseConnection.release();

  console.log("Done");

  process.exit(0);
})();
