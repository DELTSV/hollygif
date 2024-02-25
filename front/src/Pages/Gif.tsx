import {useParams} from "react-router-dom";

export default function Gif() {
	const {id} = useParams();

	return (
		<div>
			{id}
		</div>
	)
}