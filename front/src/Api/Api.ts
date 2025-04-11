import APIResponse from "../Types/APIResponse.ts";
import SearchResult from "../Types/SearchResult.ts";
import {SSE, SSEvent} from "sse.js";

export default class API {

	baseURL: string
	token: string = ""

	constructor(baseURL: string) {
		this.baseURL = baseURL;
		this.token = localStorage.getItem("token") ?? "";
	}

	refreshToken() {
		this.token = localStorage.getItem("token") ?? "";
	}

	status: number = 0

	private async request<T>(uri: string, method: "GET" | "POST" | "PUT" | "DELETE" = "GET", body: object | null = null): Promise<APIResponse<T>> {
		const headers = new Headers();
		headers.append("Authorization", "Bearer " + this.token);
		let bodyText: string | null = null;
		if(body !== null) {
			headers.append("Content-Type", "application/json");
			bodyText = JSON.stringify(body);
		}
		const rep = await fetch(`${this.baseURL}${uri}`, { method: method, headers: headers, body: bodyText, mode: "cors" });
		this.status = rep.status;
		if(rep.status === 204) {
			return {data: null as T, code: 204, message: "No Content"}
		}
		return await rep.json();
	}

	async gifs(page: number): Promise<Gif[]> {
		const rep = await this.request<Gif[]>("/api/gif?page_size=12&page=" + page);
		return rep.data;
	}

	async gif(id: number): Promise<Gif | null> {
		const rep = await this.request<Gif | null>("/api/gif/" + id);
		return rep.data
	}

	async myGif(page: number): Promise<Gif[]> {
		const rep = await this.request<Gif[]>("/api/gif/me?page_size=12&page=" + page);
		return rep.data;
	}

	async  deleteGif(id: number): Promise<null> {
		const resp = await this.request<null>("/api/gif/" + id, "DELETE");
		return resp.data;
	}

	async series(): Promise<Series[]> {
		const rep = await this.request<Series[]>("/api/series");
		return rep.data;
	}

	async oneSeries(name: string): Promise<Series> {
		const rep = await this.request<Series>("/api/series/" + name);
		return rep.data;
	}

	async seasons(name: string): Promise<Season[]> {
		const rep = await this.request<Season[]>(`/api/series/${name}/seasons`);
		return rep.data;
	}

	async episodes(series: string, season: number, page: number): Promise<Episode[]> {
		const rep = await this.request<Episode[]>(`/api/series/${series}/seasons/${season}/episodes?page=${page}&page_size=20`);
		return rep.data;
	}

	async episode(series: string, season: number, episode: number): Promise<Episode> {
		const rep = await this.request<Episode>(`/api/series/${series}/seasons/${season}/episodes/${episode}`)
		return rep.data;
	}

	async episodeGif(series: string, season: number, episode: number, page: number): Promise<Gif[]> {
		const rep = await this.request<Gif[]>(`/api/series/${series}/seasons/${season}/episodes/${episode}/gif?page=${page}&page_size=10`);
		return rep.data;
	}

	async episodeScenes(series: string, season: number, episode: number): Promise<Scene[]> {
		const rep = await this.request<Scene[]>(`/api/series/${series}/seasons/${season}/episodes/${episode}/scenes`);
		return rep.data;
	}

	async scripts(series: string, season: number, episode: number): Promise<Transcription[]> {
		const rep = await this.request<Transcription[]>(`/api/series/${series}/seasons/${season}/episodes/${episode}/transcriptions`);
		return rep.data
	}

	async search(search: string, type: string | null, page: number): Promise<SearchResult[]> {
		const rep = await this.request<SearchResult[]>(`/api/search/${search}${type !== null ? `?type=${type}&page=${page}` : ""}`);
		return rep.data;
	}

	async createGif(scene: Scene, text: String, textSize: number, onMessage: (data: SceneStatus) => void) {
		const headers = {
			"Authorization": "Bearer " + this.token,
			"Content-Type": "application/json"
		}
		const height = isNaN(textSize) ? 156 : textSize;
		const source = new SSE(this.baseURL + "/api/gif", { method: "POST", headers: headers, payload: JSON.stringify({ scene: scene, text: text, textSize: height }) });
		source.addEventListener("message", (e: SSEvent) => {
			onMessage(JSON.parse(e.data));
		});
	}

	async getTextSize(scene: Scene, text: string, textSize: number): Promise<number> {
		const rep = await this.request<number>("/api/gif/text", "POST", { scene: scene, text: text, textSize: textSize });
		return rep.data;
	}

}