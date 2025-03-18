import Card from "../Components/Card.tsx";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import API from "../Api/Api.ts";
import Accordion from "../Components/Accordion.tsx";
import Carousel from "../Components/Carousel.tsx";

interface EpisodeProps {
	api: API
}

export default function EpisodePage(props: EpisodeProps) {
	const {name, season, episode} = useParams();
	const [ep, setEp] = useState<Episode | null>(null);
	const [transcriptions, setTranscriptions] = useState<Transcription[] | null>(null);
	const [gifs, setGifs] = useState<Gif[] | null>(null);
	const [scene, setScene] = useState<Scene[] | null>(null);
	const [currentScene, setCurrentScene] = useState<number | null>(null);
	useEffect(() => {
		props.api.episode(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setEp(res);
		});
		props.api.scripts(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setTranscriptions(res);
		});
		props.api.episodeGif(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setGifs(res);
		});
		props.api.episodeScenes(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setScene(res);
		});
	}, [props.api, name, season, episode]);
	return (
		<div className={"flex flex-col gap-4 px-4"}>
			<Card className={"p-2"}>
				<div className={"flex justify-between gap-8"}>
					<p>Épisode {ep?.number}</p>
					<p>{ep?.numberOfGif} gifs au total</p>
				</div>
				<p>{ep?.title.replaceAll("_", " ")}</p>
				<p>Durée {ep?.duration}s</p>
			</Card>
			<Card>
				<div className={"flex justify-between gap-4 h-96"}>
					<div/>
					{currentScene !== null &&
						<video className={"aspect-video h-96"} src={import.meta.env.VITE_API + `/api/series/${name}/seasons/${season}/episodes/${episode}/scenes/${currentScene}/file`} controls/> || <div/>
					}
					<div className={"overflow-y-auto h-full"}>
						{scene?.map(s =>
							<div className={"py-2 px-8"} key={s.index} onClick={() => setCurrentScene(s.index)}>
								<p>Scène {s.index + 1}</p>
								<p>Début {Math.floor(s.start/60)}:{Math.floor(s.start % 60)}</p>
								<p>Fin {Math.floor(s.end/60)}:{Math.floor(s.end % 60)}</p>
							</div>
						)}
					</div>
				</div>
			</Card>
			<Card className={"p-2"}>
				<Carousel images={gifs?.map(g => import.meta.env.VITE_API + "/api/gif/file/" + g.file) ?? []} />
			</Card>
			<Accordion title={"Script"}>
				<div className={"flex flex-col"}>
					{transcriptions?.map(t =>
						<p key={t.index}><span className={"font-bold"}>{t.speaker}</span>: {t.text}</p>
					)}
				</div>
			</Accordion>
		</div>
	)
}