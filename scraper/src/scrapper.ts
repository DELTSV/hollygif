import axios, { type AxiosResponse } from "axios";
import { JSDOM } from "jsdom";
import type { Data, Line } from "./models";

const baseURL = "https://kaamelott.hypnoweb.net";

const headers = {
  "User-Agent": "PostmanRuntime/7.36.1",
};

export const scrap = async (): Promise<Data[]> => {
  const episodeLinks = await getEpisodesLinks();
  const lines = await getEpisodeTranscript(episodeLinks[0][0]);

  return [
    {
      seasonIndex: 1,
      episodes: [
        {
          episodeIndex: 1,
          lines,
        },
      ],
    },
  ];
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

  const transcript = document.getElementById("script_vo");

  const dom = new JSDOM(transcript?.innerHTML.replace(/<br>/g, "\n") ?? "");

  const divInnerText = dom.window.document.body.textContent;

  return (divInnerText?.match(/.+ ?:.+/g) ?? []).map((line, index) => {
    const [rawName, text] = line
      .split(":")
      .map((s) => s.trim().replace(/\s|&nbsp;/g, " "));

    const emote = rawName.match(/(.+) \((.+)\)/);
    const name = emote != null ? emote[1] : rawName;
    const emotion = emote != null ? emote[2] : undefined;

    return { name, text, info: emotion, index };
  });
};

const fetchDocument = async (url: string): Promise<Document> => {
  const response: AxiosResponse<string> = await axios.get(`${baseURL}${url}`, {
    headers,
  });

  const dom = new JSDOM(response.data);

  return dom.window.document;
};
