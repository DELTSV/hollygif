import API from "../api/api.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import Card from "../Components/Card.tsx";

interface EpisodesProps {
	api: API,
	bottom: Boolean
}

export default function Episodes(props: EpisodesProps) {
	const { name, season } = useParams();
	const [episodes, setEpisodes] = useState<Episode[]|null>(null);
	const [page, setPage] = useState(0);
	const [done, setDone] = useState(false);
	useEffect(() => {
		if(props.bottom) {
			setPage(prev => prev+1);
		}
	}, [props.bottom]);
	useEffect(() => {
		if(done) {
			return;
		}
		props.api.episodes(name!!, parseInt(season!!), page).then((res) => {
			if(res.length < 10) {
				setDone(true);
			}
			setEpisodes(prev => {
				const seen: {[n: number]: boolean} = {};
				const tmp = (prev ?? []).concat(res).filter(g => {
					return seen.hasOwnProperty(g.id) ? false : (seen[g.id] = true);
				}).sort((a, b) => a.number - b.number);
				return [...new Set(tmp)];
			});
		})
	}, [props.api, season, name, props.bottom, done, page]);
	return (
		<div className={"flex flex-col gap-4"}>
			{episodes?.map((e) =>
				<Card horizontal key={e.id}>
					<div>
						<p>Épisode {e.number}</p>
						<p>{e.title.replaceAll("_", " ")}</p>
					</div>
				</Card>
			)}
		</div>
	)
}