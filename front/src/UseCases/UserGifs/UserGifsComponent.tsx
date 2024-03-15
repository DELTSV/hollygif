import API from "../../Api/Api.ts";
import { GifListView } from "../../Components/index.ts";

interface UserGifsProps {
	api: API,
	bottom: boolean
}

export function UserGifs({ api, bottom }: UserGifsProps) {
	const gifSource = api.myGif;
	return <GifListView bottom={bottom} title="Mes gifs" gifSource={gifSource.bind(api)} />
}