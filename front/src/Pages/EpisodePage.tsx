import Card from "../Components/Card.tsx";
import {useCallback, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import API from "../Api/Api.ts";
import Accordion from "../Components/Accordion.tsx";
import Carousel from "../Components/Carousel.tsx";
import {clsx} from "clsx";
import Player from "../Components/Player.tsx";

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
	const [currentScene, setCurrentScene] = useState<number>(0);
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
	useEffect(() => {
		const el = document.getElementById("scene" + currentScene);
		if(el) {
			el.scrollIntoView({behavior: "smooth", block: "center"});
		}
	}, [currentScene]);
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
				<p className={"text-xl"}>Durée {Math.floor((ep?.duration ?? 0) / 60)}:{formatter.format(Math.floor((ep?.duration ?? 0) % 60))}</p>
			</Card>
			<Card className={"pt-2 pl-2"}>
				<h2 className={"text-2xl"}>Scène {currentScene !== null && currentScene + 1}</h2>
				<div className={"flex justify-between gap-4 relative"}>
					<div/>
					<Player
						className={"w-3/5"}
						scenes={scene ?? []}
						onSceneClick={(s: Scene) => {
							setCurrentScene(s.index);
						}}
						currentScene={currentScene}
						setCurrentScene={setCurrentScene}
						name={name ?? ""}
						season={parseInt(season ?? "0")}
						episode={parseInt(episode ?? "0")}
						api={props.api}
					/>
					<div/>
					<div className={"overflow-y-auto h-full cursor-pointer flex flex-col gap-4 absolute right-0"}>
						{scene?.map((s, index) =>
							<div
								id={"scene" + s.index}
								className={clsx("py-2 px-4 mr-4 rounded-lg hover:bg-neutral-900 transition", currentScene === index && "bg-neutral-800 hover:bg-neutral-800")}
								key={s.index} onClick={() => setCurrentScene(s.index)}>
								<p>Scène {s.index + 1}</p>
								<p>Début {Math.floor(s.start / 60)}:{formatter.format(Math.floor(s.start % 60))}</p>
								<p>Fin {Math.floor(s.end / 60)}:{formatter.format(Math.floor(s.end % 60))}</p>
							</div>
						)}
					</div>
				</div>
			</Card>
			<Card className={"p-2 relative"}>
				<Carousel gifs={gifs ?? []} fetchMore={fetchMoreGif}/>
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