import APIResponse from "../types/APIResponse.ts";

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

	private async request<T>(uri: string, method: "GET" | "POST" | "PUT" | "DELETE" = "GET"): Promise<APIResponse<T>> {
		const headers = new Headers();
		headers.append("Authorization", "Bearer " + this.token);
		const rep = await fetch(`${this.baseURL}${uri}`, {method: method, headers: headers});
		this.status = rep.status;
		return await rep.json();
	}

	async gifs(page: number): Promise<Gif[]> {
		const rep = await this.request<Gif[]>("/api/gif?page_size=12&page=" + page);
		return rep.data;
	}

	async gif(id: number): Promise<Gif|null> {
		const rep = await this.request<Gif|null>("/api/gif/" + id);
		return rep.data
	}

	async myGif(page: number): Promise<Gif[]> {
		const rep = await this.request<Gif[]>("/api/gif/me?page_size=12&page=" + page);
		return rep.data;
	}
}