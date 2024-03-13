import API from "../api/api.ts";
import Card from "../Components/Card.tsx";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";

interface EpisodeProps {
	api: API
}

export default function EpisodePage(props: EpisodeProps) {
	const { name, season, episode } = useParams();
	const [ep, setEp] = useState<Episode|null>(null);
	useEffect(() => {
		props.api.episode(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setEp(res);
		})
	}, [props.api, name, season, episode]);
	return (
		<Card className={"p-2"}>
			<p>Épisode {ep?.number}</p>
			<p>{ep?.title.replaceAll("_", " ")}</p>
			<p>Durée {ep?.duration}s</p>
		</Card>
	)
}