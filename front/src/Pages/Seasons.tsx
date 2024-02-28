import API from "../api/api.ts";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Card from "../Components/Card.tsx";

interface SeasonsProps {
	api: API
}

export default function Seasons(props: SeasonsProps) {
	const { name } = useParams();

	const [series, setSeries] = useState<Series|null>(null);
	const [seasons, setSeasons] = useState<Season[]|null>(null)

	useEffect(() => {
		props.api.oneSeries(name ?? "").then(res => setSeries(res));
	}, [props.api, name]);

	useEffect(() => {
		props.api.seasons(name ?? "").then(res => setSeasons(res));
	}, [props.api, name, series]);

	return (
		<div className={"flex flex-col items-center gap-2"}>
			<h1 className={"text-4xl"}>Les saisons de {series?.name}</h1>
			{seasons?.map(s =>
				<Card image={series?.logo} imageClassName={"h-24 bg-white"} horizontal>
					<div className="flex items-center p-2">
						Saison {s.number}
					</div>
				</Card>
			)}
		</div>
	);
}