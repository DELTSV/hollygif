import APIResponse from "../types/APIResponse.ts";

export default class API {

	baseURL: string

	constructor(baseURL: string) {
		this.baseURL = baseURL
	}

	private async request<T>(uri: string, method: "GET" | "POST" | "PUT" | "DELETE" = "GET"): Promise<APIResponse<T>> {
		const rep = await fetch(`${this.baseURL}${uri}`, {method: method});
		return await rep.json();
	}

	async gifs(): Promise<Gif[]> {
		const rep = await this.request<Gif[]>("/api/gif");
		return rep.data;
	}

	async gif(id: number): Promise<Gif|null> {
		const rep = await this.request<Gif|null>("/api/gif/" + id);
		return rep.data
	}
}