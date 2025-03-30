import {clsx} from "clsx";

interface SpinnerProps {
	className?: string;
}

export default function Spinner(props: SpinnerProps) {
	return (
		<div className={clsx("h-12 w-12 border-2 border-neutral-400 rounded-full animate-spin !border-b-transparent", props.className)}/>
	)
}