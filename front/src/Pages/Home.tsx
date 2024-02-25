import GifCard from "../Components/GifCard.tsx";
import API from "../api/api.ts";
import {useEffect, useState} from "react";

interface HomeProps {
	api: API
}

export default function Home(props: HomeProps) {
	const [gifs, setGifs] = useState<Gif[]|null>(null)
	useEffect(() => {
		props.api.gifs().then(res => {
			setGifs(res)
		});
	}, [props.api]);
	return (
		<div className={"w-3/4 flex flex-col items-center"}>
			<h1 className={"text-4xl"}>Les derniers gif</h1>
			<div className={"grid grid-cols-4 gap-4"}>
				{gifs?.map(gif => <GifCard gif={gif}/>)}
			</div>
		</div>
	)
}