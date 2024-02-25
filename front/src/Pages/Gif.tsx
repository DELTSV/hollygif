import {useParams} from "react-router-dom";
import API from "../api/api.ts";
import {useEffect, useRef, useState} from "react";
import {Clipboard} from "react-feather";
import Button from "../Components/Button.tsx";

interface GifProps {
	api: API
}

export default function Gif(props: GifProps) {
	const {id} = useParams();

	const [gif, setGif] = useState<Gif|null>(null);

	useEffect(() => {
		props.api.gif(parseInt(id ?? "0")).then(r => {
			setGif(r);
		})
	}, [props.api]);

	const input = useRef<HTMLInputElement>(null);

	return (
		<div className={"flex flex-col items-stretch gap-2"}>
			<div className={"border-2 border-black rounded bg-neutral-300"}>
				<img src={import.meta.env.VITE_API + "/api/gif/file/" + gif?.file} alt={"gif"}/>
				<div className={"h-0.5 w-full bg-black"}/>
				<div className={"flex items-center px-2 py-1 gap-2"}>
					<img className={"h-6 rounded-full"} src={gif?.creator.avatar ?? ""} alt={"Profile picture"}/>
					<p>{gif?.creator.global_name}</p>
				</div>
			</div>
			<div className={"bg-neutral-300 border-2 border-black p-2"}>
				<p>Créateur: {gif?.creator.global_name}</p>
				<p>Date: {(new Date(gif?.createdAt ?? "")).toLocaleString()}</p>
				<p>Timecode: {gif?.timecode}</p>
				<p>Épisode: {gif?.scene.episode.number}</p>
				<p>Livre: {gif?.scene.episode.season.number}</p>
				<div>
					<p>Commande discord:</p>
					<div className={"flex"}>
						<input ref={input} readOnly value={`/kaagif livre: Livre ${gif?.scene.episode.season.number} episode: ${gif?.scene.episode.number} timecode: ${gif?.timecode} ${gif?.text !== "" ? "text: " + gif?.text : ""}`} className={"w-full bg-neutral-300"}/>
						<Button onClick={() => {
							input?.current?.select();
							navigator.clipboard.writeText(input?.current?.value ?? "");
						}}><Clipboard/></Button>
					</div>
				</div>
			</div>
		</div>
	)
}