import {clsx} from "clsx";
import React, {useEffect, useRef, useState} from "react";
import {ChevronLeft, ChevronRight, Pause, Play} from "react-feather";

interface PlayerProps {
	className?: string,
	scenes: Scene[],
	onSceneClick?: (scene: Scene) => void,
	currentScene: number,
	setCurrentScene: React.Dispatch<React.SetStateAction<number>>,
	name: string,
	season: number,
	episode: number,
}

export default function Player(props: PlayerProps) {
	const {name, season, episode, currentScene} = props;
	const video = useRef<HTMLVideoElement>(null);
	const [paused, setPaused] = useState(false);
	const [loading, setLoading] = useState(false);
	const [sceneTime, setSceneTime] = useState(0);
	useEffect(() => {
		if(paused) {
			video.current?.play();
		} else {
			video.current?.pause();
		}
	}, [paused]);
	useEffect(() => {
		if(video.current) {
			video.current.onpause = () => {
				setPaused(false);
			}
			video.current.onplay = () => {
				setPaused(true);
			}
			video.current.onloadstart = () => {
				setLoading(true);
			}
			video.current.onloadeddata = () => {
				setLoading(false);
				setPaused(true);
			}
			video.current.ontimeupdate = () => {
				setSceneTime(video.current?.currentTime ?? 0);
			}
		}
	}, []);
	return (
		<div className={clsx(props.className, "relative")}>
			<video
				className={"aspect-video h-96 cursor-pointer"}
				src={import.meta.env.VITE_API + `/api/series/${name}/seasons/${season}/episodes/${episode}/scenes/${currentScene}/file`}
				controls={false}
				ref={video}
			/>
			{props.scenes.length > 0 &&
                <div className={"absolute top-0 left-0 h-4"}
                     style={{
						 width: (video.current?.getBoundingClientRect().width ?? 0) + "px",
						 height: (video.current?.getBoundingClientRect().height ?? 0) + "px"
					 }}
                >
                    <div className={"w-full px-2 flex gap-x-0.5 absolute bottom-2 left-0 h-4"}>
						{props.scenes.map(s => {
							return (
								<div
									onClick={() => {
										props.onSceneClick && props.onSceneClick(s)
									}}
									title={"Scène " + (s.index + 1)}
									className={clsx(
										"h-4 relative",
										(currentScene ?? 0) <= s.index && "bg-neutral-100/50" || "bg-neutral-100",
										(s.index === 0 && "rounded-l-sm"),
										(s.index === props.scenes.length - 1 && "rounded-r-sm"),
									)}
									style={{width: ((s.end - s.start) / s.episode.duration * 100) + "%"}}
									key={s.index}
								>
									{s.index === currentScene && <div className={"transition-all absolute top-0 left-0 h-4 bg-neutral-100"} style={{width: (sceneTime * 100 / (s.end - s.start)) + "%"}}/>}
								</div>
							)
						})}
                    </div>
					<button className={"absolute top-1/2 left-4 -translate-y-1/2"} onClick={() => { props.setCurrentScene(prev => (prev ?? 0) - 1)}}>
						<ChevronLeft/>
					</button>
                    <button
                        className={"absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"}
                        onClick={() => {
							if(loading) return;
							setPaused(prev => !prev);
						}}
                    >
						{(loading && currentScene !== null) && <div className={"border-4 w-8 h-8 border-yellow-500 border-b-transparent rounded-full animate-spin"}/> || paused && <Pause/> || <Play/>}
                    </button>
					<button className={"absolute top-1/2 right-4 -translate-y-1/2"} onClick={() => { props.setCurrentScene(prev => (prev ?? 0) + 1)}}>
						<ChevronRight/>
					</button>
                </div>
			}
		</div>
	)
}