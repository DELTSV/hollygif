import GifCard from "../../Components/GifCard.tsx";
import API from "../../Api/Api.ts";
import { useEffect, useState } from "react";

interface GifListProps {
	api: API,
	bottom: boolean
}

export function GifList({ api, bottom }: GifListProps) {
	const [gifs, setGifs] = useState<Gif[] | null>(null);
	const [page, setPage] = useState(0);
	const [done, setDone] = useState(false);

	useEffect(() => {
		if (done) {
			return;
		}
		api.gifs(page).then(res => {
			if (res.length < 12) {
				setDone(true);
			}
			setGifs(prev => {
				const seen: { [n: number]: boolean } = {};
				const tmp = (prev ?? []).concat(res).filter(g => {
					return seen.hasOwnProperty(g.id) ? false : (seen[g.id] = true);
				}).sort((a, b) => (new Date(b.createdAt)).getTime() - (new Date(a.createdAt)).getTime());
				return [...new Set(tmp)];
			});
		});
	}, [api, page, done]);

	useEffect(() => {
		if (bottom) {
			setPage(prev => prev + 1);
		}
	}, [bottom]);

	return <section className={"w-11/12 2xl:w-10/12 flex flex-col items-center"}>
		<h1 className={"text-4xl text-yellow-500"}>Les derniers gif</h1>
		<ul className={"grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-4 gap-4"}>
			{gifs?.map((gif, index) =>
				<GifCard key={index} gif={gif} redirect={true} width={"min-w-72 max-w-72"} />
			)}
		</ul>
	</section>;
}