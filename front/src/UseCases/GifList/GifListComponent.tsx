import API from "../../Api/Api.ts";
import { GifListView } from "../../Components";

interface GifListProps {
	api: API,
	bottom: boolean
}

export function GifList({ api, bottom }: GifListProps) {
	const gifSource = api.gifs;
	return <GifListView bottom={bottom} title="Les derniers gifs" gifSource={gifSource.bind(api)} />;
}