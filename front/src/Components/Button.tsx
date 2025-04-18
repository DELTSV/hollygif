import React, {ReactNode} from "react";
import {clsx} from "clsx";

export interface ButtonProps {
	onClick?: React.MouseEventHandler<HTMLButtonElement>
	children: ReactNode,
	className?: string
}

export default function Button(props: ButtonProps) {
	return (
		<button
			onClick={props.onClick}
			className={clsx(props.className, "border-2 border-yellow-500 text-yellow-500 rounded px-4 py-2 hover:bg-yellow-500 transition-all hover:scale-110 hover:text-black")}
		>
			{props.children}
		</button>
	);
}

export function DangerButton(props: ButtonProps) {
	return (
		<Button className={clsx(props.className, "!border-red-500 !text-red-500 hover:!bg-red-500 hover:!text-black")} onClick={props.onClick}>
			{props.children}
		</Button>
	)
}