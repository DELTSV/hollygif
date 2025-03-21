import Card from "../Components/Card.tsx";
import {useCallback, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import API from "../Api/Api.ts";
import Accordion from "../Components/Accordion.tsx";
import Carousel from "../Components/Carousel.tsx";
import {clsx} from "clsx";

interface EpisodeProps {
	api: API
}

const formatter = Intl.NumberFormat("fr-FR", {minimumIntegerDigits: 2})

export default function EpisodePage(props: EpisodeProps) {
	const {name, season, episode} = useParams();
	const [ep, setEp] = useState<Episode | null>(null);
	const [transcriptions, setTranscriptions] = useState<Transcription[] | null>(null);
	const [gifs, setGifs] = useState<Gif[] | null>(null);
	const [scene, setScene] = useState<Scene[] | null>(null);
	const [currentScene, setCurrentScene] = useState<number | null>(null);
	const [_, setGifsPage] = useState(0);
	useEffect(() => {
		props.api.episode(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setEp(res);
		});
		props.api.scripts(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setTranscriptions(res);
		});
		props.api.episodeGif(name!!, parseInt(season!!), parseInt(episode!!), 0).then(res => {
			setGifs(res);
		});
		props.api.episodeScenes(name!!, parseInt(season!!), parseInt(episode!!)).then(res => {
			setScene(res);
		});
	}, [props.api, name, season, episode]);
	const fetchMoreGif = useCallback(() => {
		setGifsPage(prev => {
			props.api.episodeGif(name!!, parseInt(season!!), parseInt(episode!!), prev + 1).then(res => {
				setGifs(prev => {
					return [...prev ?? [], ...res];
				});
			})
			return prev + 1;
		});
	}, [props.api, name, season, episode]);
	return (
		<div className={"flex flex-col gap-4 px-4"}>
			<Card className={"p-2"}>
				<div className={"flex justify-between gap-8"}>
					<p className={"text-3xl"}>Épisode {ep?.number}</p>
					<p className={"text-2xl"}>{ep?.numberOfGif} gifs au total</p>
				</div>
				<p className={"text-2xl"}>{ep?.title.replaceAll("_", " ")}</p>
				<p className={"text-xl"}>Durée {Math.floor((ep?.duration ?? 0)/60)}:{formatter.format(Math.floor((ep?.duration ?? 0) % 60))}</p>
			</Card>
			<Card className={"pt-2 pl-2"}>
				<h2 className={"text-2xl"}>Scène {currentScene !== null && currentScene + 1}</h2>
				<div className={"flex justify-between gap-4 h-96"}>
					<div/>
					{currentScene !== null &&
						<video className={"aspect-video h-96 cursor-pointer"} src={import.meta.env.VITE_API + `/api/series/${name}/seasons/${season}/episodes/${episode}/scenes/${currentScene}/file`} controls /> || <div/>
					}
					<div className={"overflow-y-auto h-full cursor-pointer flex flex-col gap-4"}>
						{scene?.map((s, index) =>
							<div className={clsx("py-2 px-4 mr-4 rounded-lg hover:bg-neutral-900 transition", currentScene === index && "bg-neutral-800 hover:bg-neutral-800")} key={s.index} onClick={() => setCurrentScene(s.index)}>
								<p>Scène {s.index + 1}</p>
								<p>Début {Math.floor(s.start/60)}:{formatter.format(Math.floor(s.start % 60))}</p>
								<p>Fin {Math.floor(s.end/60)}:{formatter.format(Math.floor(s.end % 60))}</p>
							</div>
						)}
					</div>
				</div>
			</Card>
			<Card className={"p-2 relative"}>
				<Carousel gifs={gifs ?? []} fetchMore={fetchMoreGif} />
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