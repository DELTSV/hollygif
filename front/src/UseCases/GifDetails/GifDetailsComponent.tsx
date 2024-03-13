import { useParams, Link } from "react-router-dom";
import API from "../../Api/Api.ts";
import { useEffect, useRef, useState } from "react";
import { Clipboard } from "react-feather";
import { Button, GifCard, Card } from "../../Components";

interface GifProps {
	api: API
}

export function GifDetails({ api }: GifProps) {
	const { id } = useParams();
	const [gif, setGif] = useState<Gif | null>(null);

	useEffect(() => {
		api.gif(parseInt(id ?? "0")).then(r => {
			setGif(r);
		})
	}, [id, api]);

	const input = useRef<HTMLInputElement>(null);

	return (
		<div className={"flex flex-col items-stretch gap-2"}>
			<GifCard gif={gif} redirect={false} />
			<Card className={"p-2"}>
				<p>Créateur: {gif?.creator.global_name}</p>
				<p>Date: {(new Date(gif?.createdAt ?? "")).toLocaleString()}</p>
				<p>Timecode: {gif?.timecode}</p>
				<p>
					<Link to={`/series/${gif?.scene?.episode?.season?.series?.name}/${gif?.scene?.episode?.season?.number}`}>
						Livre: {gif?.scene.episode.season.number}
					</Link>
				</p>
				<p>
					<Link to={`/series/${gif?.scene?.episode?.season?.series.name}/${gif?.scene?.episode?.season?.number}/${gif?.scene?.episode.number}`}>
						Épisode: {gif?.scene.episode.number}
					</Link>
				</p>
				<div>
					<p>Commande discord:</p>
					<div className={"flex"}>
						<Button className={"mr-2"} onClick={() => {
							input?.current?.select();
							navigator.clipboard.writeText(input?.current?.value ?? "");
						}}><Clipboard /></Button>
						<input ref={input} readOnly value={`/kaagif livre: Livre ${gif?.scene.episode.season.number} episode: ${gif?.scene.episode.number} timecode: ${gif?.timecode} ${gif?.text !== "" ? "text: " + gif?.text : ""}`} className={"grow bg-transparent"} />
					</div>
				</div>
			</Card>
		</div>
	)
}