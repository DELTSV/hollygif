import {useParams, Link, useNavigate} from "react-router-dom";
import API from "../Api/Api.ts";
import { useEffect, useRef, useState } from "react";
import { Clipboard } from "react-feather";
import Button, {DangerButton} from "../Components/Button.tsx";
import GifCard from "../Components/GifCard.tsx";
import Card from "../Components/Card.tsx";

interface GifProps {
	api: API,
	user: User | null
}

export default function Gif(props: GifProps) {
	const { id } = useParams();

	const [gif, setGif] = useState<Gif | null>(null);

	useEffect(() => {
		props.api.gif(parseInt(id ?? "0")).then(r => {
			setGif(r);
		})
	}, [id, props.api]);

	const input = useRef<HTMLInputElement>(null);

	const navigate = useNavigate();

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
			{props.user?.id === gif?.creator?.id &&
				<Card className={"p-2 flex justify-between items-center"}>
					<p className={"text-2xl"}>Gestion du gif</p>
					<DangerButton onClick={() => {
						props.api.deleteGif(parseInt(id ?? "0")).then(() => {
							navigate(-1);
						})
					}}>
						Supprimer
					</DangerButton>
				</Card>
			}
		</div>
	)
}