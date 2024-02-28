import {ReactNode} from "react";
import {clsx} from "clsx";

interface CardProps {
	image?: string
	imageWidth?: string
	className?: string
	children: ReactNode
}

export default function Card(props: CardProps) {
	return (
		<div className={clsx(props.className, "rounded overflow-hidden bg-black bg-opacity-75 text-yellow-500 hover:drop-shadow-xl transition")}>
			{props.image &&
			<>
				<img src={props.image} alt={"gif"}
					 className={props.imageWidth ?? "w-auto"}/>
				<div className={"h-0.5 w-full bg-neutral-400"}/>
			</>
			}
			{props.children}
		</div>
	)
}