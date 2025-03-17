import Card from "../Components/Card.tsx";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import API from "../Api/Api.ts";

interface EpisodeProps {
	api: API
}

export default function EpisodePage(props: EpisodeProps) {
	const { name, season, episode } = useParams();
	const [ep, setEp] = useState<Episode|null>(null);
	const [transcriptions, setTranscriptions] = useState<Transcription[]|null>(null)
	useEffect(() => {
		props.api.episode(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setEp(res);
		});
		props.api.scripts(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setTranscriptions(res);
		})
	}, [props.api, name, season, episode]);
	return (
		<div className={"flex flex-col gap-4"}>
			<Card className={"p-2"}>
				<div className={"flex justify-between gap-8"}>
					<p>Épisode {ep?.number}</p>
					<p>{ep?.numberOfGif} gifs au total</p>
				</div>
				<p>{ep?.title.replaceAll("_", " ")}</p>
				<p>Durée {ep?.duration}s</p>
			</Card>
			<Card>
				<h2>Script</h2>
				<div className={"flex flex-col"}>
					{transcriptions?.map(t =>
						<p><span className={"font-bold"}>{t.speaker}</span>: {t.text}</p>
					)}
				</div>
			</Card>
		</div>
	)
}