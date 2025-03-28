import SearchResult from "../Types/SearchResult.ts";
import {Link} from "react-router-dom";
import {clsx} from "clsx";
import React from "react";

interface ResultContainerProps {
	result: SearchResult,
	clean: () => void,
	setType: React.Dispatch<React.SetStateAction<string | null>>,
	type: string | null,
	nextPage: (() => void) | null,
}

export default function ResultContainer(props: ResultContainerProps) {
	if(props.result.type === "series") {
		return ;
	}
	let name: string = "";
	switch (props.result.type) {
		case "episode": name = "Épisode" + (props.result.total > 1 ? "s" : ""); break;
		case "gif": name = "Gif" + (props.result.total > 1 ? "s" : ""); break;
		case "transcription": name = "Texte" + (props.result.total > 1 ? "s" : ""); break;
	}
	return (
		<div className={""}>
			<div className={"bg-neutral-800 border-y-2 border-neutral-700 flex justify-between"}>
				<p>{name}: {props.result.total} résultat{props.result.total > 1 ? "s" : ""}</p>
				{
					props.type === null && props.result.total > props.result.data.length &&
					<button onClick={() => props.setType(props.result.type)}>
						Voir plus …
					</button>
				}
			</div>
			<div className={clsx("max-h-80 overflow-y-auto", props.result.type === "gif" && "flex flex-wrap")}>
				{(props.result.data.map((data, key) => <DisplayResult data={data} key={key} clean={props.clean} />))}
				{props.result.total === 0 && "Aucun Résultat"}
				{props.type !== null && props.result.data.length < props.result.total && <button className={"w-full bg-neutral-800"} onClick={() => {
					if(props.nextPage) props.nextPage();
				}}>Charger plus</button>}
			</div>
		</div>
	)
}


interface DisplayResultProps {
	data: any,
	clean: () => void,
}

function DisplayResult(props: DisplayResultProps) {
	const {data, clean}	= props;
	if(data.type === "episode") {
		const episode = data as Episode;
		return <div onClick={clean}>
			<Link to={`/series/${episode.season.series.name}/${episode.season.number}/${episode.number}`}>
				<p>Épisode {episode.number}: {episode.title.replace("_", " ")}</p>
			</Link>
		</div>
	}
	if(data.type === "gif") {
		const gifs = data as Gif;
		return <div onClick={clean}>
			<Link to={"/gif/" + gifs.id}>
				<img alt={gifs.text} className={"w-32"} src={import.meta.env.VITE_API + "/api/gif/file/" + gifs.file}/>
			</Link>
		</div>
	}
	if(data.type === "transcription") {
		const transcription = data as Transcription;
		return <div className={"whitespace-nowrap overflow-x-hidden overflow-ellipsis"} title={transcription.text} onClick={clean}>
			<Link to={`/series/${transcription.episode.season.series.name}/${transcription.episode.season.number}/${transcription.episode.number}`}>
				{transcription.speaker}: {transcription.text}
			</Link>
		</div>
	}
}