import { ReactNode } from "react";
import { clsx } from "clsx";

interface CardProps {
	image?: string,
	imageClassName?: string,
	className?: string,
	horizontal?: boolean,
	children: ReactNode,
}

export function Card(props: CardProps) {
	const css = clsx(props.className, "rounded overflow-hidden bg-black bg-opacity-75 hover:drop-shadow-xl transition", props.horizontal === true && "flex items-stretch");
	return (
		<div className={css}>
			{props.image &&
				<>
					<img src={props.image} alt={"gif"} className={props.imageClassName ?? "w-auto"} />
					<div className={clsx(props.horizontal === true ? "w-0.5 grow" : "h-0.5 w-full", "bg-neutral-400")} />
				</>
			}
			{props.children}
		</div>
	)
}