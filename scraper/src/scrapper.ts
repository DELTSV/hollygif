import axios, { type AxiosResponse } from "axios";
import { JSDOM } from "jsdom";
import type { Data, Line } from "./models";

const baseURL = "https://kaamelott.hypnoweb.net";

const headers = {
  "User-Agent": "PostmanRuntime/7.36.1",
};

const toTitleCase = (str: string): string => {
  return str.replace(
    /\w\S*/g,
    (txt) => txt.charAt(0).toUpperCase() + txt.substring(1).toLowerCase(),
  );
};

const truncateData = (links: Data[]): Data[] => {
  return links.filter((data) => data.seasonIndex === 5);
};

export const scrap = async (): Promise<Data[]> => {
  const episodeLinks = await getEpisodesLinks();

  const data = await Promise.all(
    episodeLinks.map(async (seasonLinks, seasonIndex) => {
      // if (seasonIndex === 4) {
      //   return {
      //     seasonIndex: seasonIndex + 1,
      //     episodes: await Promise.all(
      //       seasonLinks.map(async (episodeLink, episodeIndex) => {
      //         const lines = await getEpisodeTranscript(episodeLink);
      //         return { episodeIndex: episodeIndex + 1, lines };
      //       }),
      //     ),
      //   };
      // } else {
      //   return {
      //     seasonIndex: seasonIndex + 1,
      //     episodes: [],
      //   };
      // }

      return {
        seasonIndex: seasonIndex + 1,
        episodes: await Promise.all(
          seasonLinks.map(async (episodeLink, episodeIndex) => {
            const lines = await getEpisodeTranscript(episodeLink);
            return { episodeIndex: episodeIndex + 1, lines };
          }),
        ),
      };
    }),
  );

  return data;
};

export const getEpisodesLinks = async (): Promise<string[][]> => {
  const document = await fetchDocument("/kaamelott/guide-des-episodes.119.2");

  const tabsContents = document.querySelector(".tabs-content");
  const seasonsContent = tabsContents?.querySelectorAll(".content");

  return Array.from(seasonsContent ?? []).map((seasonContent) => {
    const episodes = seasonContent.querySelectorAll("a");
    return Array.from(episodes).map((episode) => episode.href);
  });
};

export const getEpisodeTranscript = async (
  episodeLink: string,
): Promise<Line[]> => {
  const document = await fetchDocument(episodeLink);

  const transcript =
    document.getElementById("script_vo") ??
    document.getElementById("script_vf");

  if (transcript == null) {
    console.error(`No transcript found for episode ${episodeLink}`);
    return [];
  }

  const dom = new JSDOM(transcript?.innerHTML.replace(/<br>/g, "\n") ?? "");

  const divInnerText = dom.window.document.body.textContent;

  console.log(`Processing episode ${episodeLink}`);

  return (divInnerText?.match(/.+ ?:.+/g) ?? []).reduce((acc, line, index) => {
    const [rawName, text] = line
      .split(":")
      .map((s) => s.trim().replace(/\s|&nbsp;/g, " "));

    const emote = rawName.match(/(.+) \((.+)\)/);
    const name = emote != null ? emote[1] : rawName;
    const emotion = emote != null ? emote[2] : undefined;

    if (
      name.length > 50 ||
      text.length > 1000 ||
      (emotion ?? "").length > 100
    ) {
      console.error(
        `Line ${index} from episode ${episodeLink} is too long, skipping`,
      );
      return acc;
    }

    return [
      ...acc,
      {
        index,
        name: toTitleCase(name),
        text,
        info: emotion,
      },
    ];
  }, [] as Line[]);
};

const fetchDocument = async (url: string): Promise<Document> => {
  const response: AxiosResponse<string> = await axios.get(`${baseURL}${url}`, {
    headers,
  });

  const dom = new JSDOM(response.data);

  return dom.window.document;
};
