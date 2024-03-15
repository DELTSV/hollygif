import { useEffect, useState } from "react";
import API from "../../Api/Api.ts";
import { Card } from "../../Components/index.ts";
import { Link } from "react-router-dom";

interface SeriesProps {
	api: API
}

export function SeriesList(props: SeriesProps) {
	const [series, setSeries] = useState<Series[]>([]);
	useEffect(() => {
		props.api.series().then((res) => {
			setSeries(res);
		});
	}, [props.api]);
	return (
		<div>
			<h1 className={"text-4xl"}>Les séries</h1>
			{series.map(s => {
				return (
					<Link to={"/series/" + s.name}>
						<Card image={s.logo} imageClassName={"bg-white h-24"} horizontal>
							<div className={"p-2 flex items-center"}>
								<p>{s.name}</p>
							</div>
						</Card>
					</Link>
				);
			})}
		</div>
	)
}