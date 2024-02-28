import {Link} from "react-router-dom";
import {useMemo} from "react";
import Card from "./Card.tsx";

interface GifCardProps {
	gif: Gif | null,
	redirect: boolean,
	width?: string
}

export default function GifCard(props: GifCardProps) {
	const { gif, redirect } = props;
	const content = useMemo(() => {
		return <Card image={import.meta.env.VITE_API + "/api/gif/file/" + gif?.file}>
			<div className={"flex items-center px-2 py-1 gap-2"}>
				<img className={"h-6 rounded-full"} src={gif?.creator.avatar ?? ""} alt={"Profile picture"}/>
				<p>{gif?.creator.global_name}</p>
			</div>
		</Card>
	}, [gif]);
	if (redirect) {
		return (
			<Link to={"/gif/" + gif?.id}>
				{content}
			</Link>
		);
	}
	return content;
}