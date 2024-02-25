interface GifCardProps {
	gif: Gif
}

export default function GifCard(props: GifCardProps) {
	const { gif } = props;
	return (
		<a className={"border-2 border-black rounded overflow-hidden bg-neutral-300"} href={"/gif/" + gif.id}>
			<img src={import.meta.env.VITE_API + "/api/gif/" + gif.file} alt={"gif"}/>
			<div className={"h-0.5 w-full bg-black"}/>
			<div className={"flex items-center px-2 py-1 gap-2"}>
				<img className={"h-6 rounded-full"} src={gif.creator.avatar ?? ""} alt={"Profile picture"}/>
				<p>{gif.creator.global_name}</p>
			</div>
		</a>
	)
}