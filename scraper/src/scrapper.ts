import axios from "axios";
import { JSDOM } from "jsdom";

import mariadb from 'mariadb';

interface Line {
  name: string;
  text: string;
  emotion?: string;
}

const baseURL = "https://kaamelott.hypnoweb.net";

const headers = {
  "User-Agent": "PostmanRuntime/7.36.1"
}

const pool = mariadb.createPool({
  host: 'mydb.com',
  user:'myUser',
  password: 'myPassword',
  connectionLimit: 5
});

pool.getConnection()
  .then(conn => {

    conn.query("SELECT 1 as val")
      .then((rows) => {
        console.log(rows); //[ {val: 1}, meta: ... ]
        //Table must have been created before
        // " CREATE TABLE myTable (id int, val varchar(255)) "
        return conn.query("INSERT INTO myTable value (?, ?)", [1, "mariadb"]);
      })
      .then((res) => {
        console.log(res); // { affectedRows: 1, insertId: 1, warningStatus: 0 }
        conn.end();
      })
      .catch(err => {
        //handle error
        console.log(err);
        conn.end();
      })

  }).catch(err => {
  //not connected
});

export const scrap = async () => {

  const episodeLinks = await getEpisodesLinks();
  const lines = await getEpisodeTranscript(episodeLinks[0][0]);

  console.log(lines);
}

export const getEpisodesLinks = async () => {
  const document = await fetchDocument("/kaamelott/guide-des-episodes.119.2");

  const tabsContents = document.querySelector(".tabs-content");
  const seasonsContent = tabsContents?.querySelectorAll(".content");

  return Array.from(seasonsContent || []).map((seasonContent) => {
    const episodes = seasonContent.querySelectorAll("a");
    return Array.from(episodes).map((episode) => episode.href);
  });
}

export const getEpisodeTranscript = async (episodeLink: string): Promise<Line[]> => {
  const document = await fetchDocument(episodeLink);

  const transcript = document.getElementById("script_vo");

  const dom = new JSDOM(transcript?.innerHTML.replace(/<br>/g, '\n') || "");

  const divInnerText = dom.window.document.body.textContent;

  return (divInnerText?.match(/.+ ?:.+/g) || []).map((line) => {
    const [rawName, text] = line.split(":").map((s) => s.trim());

    const emote = rawName.match(/(.+) \((.+)\)/);
    const name = emote ? emote[1] : rawName;
    const emotion = emote ? emote[2] : undefined;


    return {name, text, emotion};
  });
}

const fetchDocument = async (url: string): Promise<Document> => {
  const response = await axios.get(`${baseURL}${url}`, {
    headers,
  });

  const dom = new JSDOM(response.data);

  return dom.window.document;
}
