import SearchResult from "../Types/SearchResult.ts";

interface ResultContainerProps {
	result: SearchResult
}

export default function ResultContainer(props: ResultContainerProps) {
	return (
		<div>
			<div className={"bg-neutral-800"}>
				{props.result.type}
			</div>
			<div>
				{(props.result.data.map(DisplayResult))}
			</div>
		</div>
	)
}

function DisplayResult(data: any, key: number) {
	if(data.type === "episode") {
		const episode = data as Episode;
		return <div key={key}>
			<p>Épisode {episode.number}: {episode.title.replace("_", " ")}</p>
		</div>
	}
	if(data.type === "gif") {
		const gifs = data as Gif;
		return <div key={key}>
			<img src={import.meta.env.VITE_API + "/api/gif/file/" + gifs.file}/>
		</div>
	}
	if(data.type === "transcription") {
		const transcription = data as Transcription;
		return <div key={key}>
			<p>{transcription.speaker}: {transcription.text}</p>
		</div>
	}
}