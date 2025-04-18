import {clsx} from "clsx";
import React, {useCallback, useEffect, useRef, useState} from "react";
import {
	ChevronLeft,
	ChevronRight,
	Info,
	Pause,
	Play,
	Save,
	Type,
	Volume,
	Volume1,
	Volume2,
	VolumeX
} from "react-feather";
import API from "../Api/Api.ts";
import {useNavigate} from "react-router-dom";
import Spinner from "./Spinner.tsx";

interface PlayerProps {
	className?: string,
	scenes: Scene[],
	onSceneClick?: (scene: Scene) => void,
	currentScene: number,
	setCurrentScene: React.Dispatch<React.SetStateAction<number>>,
	name: string,
	season: number,
	episode: number,
	api: API
}

export default function Player(props: PlayerProps) {
	const {name, season, episode, currentScene} = props;
	const video = useRef<HTMLVideoElement>(null);
	const [paused, setPaused] = useState(false);
	const [loading, setLoading] = useState(false);
	const [sceneTime, setSceneTime] = useState(0);
	const [volume, setVolume] = useState(1);
	const [muted, setMuted] = useState(false);
	const volumeContainer = useRef<HTMLDivElement>(null);
	useEffect(() => {
		if (paused) {
			video.current?.play().catch(() => setPaused(false));
		} else {
			video.current?.pause();
		}
	}, [paused]);
	useEffect(() => {
		if (video.current) {
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
	useEffect(() => {
		if (video.current) {
			video.current.volume = volume;
		}
	}, [volume]);
	useEffect(() => {
		if (video.current) {
			video.current.muted = muted;
		}
	}, [muted]);
	const mute = useCallback(() => setMuted(prev => !prev), []);
	const [volumeSet, setVolumeSet] = useState(false);
	const [height, setHeight] = useState(0);
	const [width, setWidth] = useState(0);

	useEffect(() => {
		if(video.current) {
			const obs = new ResizeObserver((e) => {
				setHeight(e[0].contentRect.height);
				setWidth(e[0].contentRect.width);
			})
			obs.observe(video.current);
			return () => {
				obs.disconnect();
			}
		}
	}, []);

	return (
		<div className={clsx(props.className, "relative")}>
			<video
				className={"aspect-video cursor-pointer w-full focus:border-2 border-red-500"}
				src={import.meta.env.VITE_API + `/api/series/${name}/seasons/${season}/episodes/${episode}/scenes/${currentScene}/file`}
				controls={false}
				ref={video}
			/>
			{props.scenes.length > 0 &&
                <div className={"absolute top-0 left-0 h-4 overflow-hidden"}
                     style={{
						 width: width + "px",
						 height: height + "px"
					 }}
                >
                    <div
                        className={clsx("w-full px-2 flex absolute bottom-2 left-0 h-4", props.scenes.length < 300 && "gap-x-0.5")}>
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
									{s.index === currentScene &&
                                        <div className={"transition-all absolute top-0 left-0 h-4 bg-neutral-100"}
                                             style={{width: (sceneTime * 100 / (s.end - s.start)) + "%"}}/>}
								</div>
							)
						})}
                    </div>
                    <button className={"absolute top-1/2 left-4 -translate-y-1/2 p-4 bg-neutral-800/70 rounded-xl"}
                            onClick={() => {
								props.setCurrentScene(prev => (prev ?? 0) - 1)
							}}>
                        <ChevronLeft/>
                    </button>
                    <button
                        className={"absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"}
                        onClick={() => {
							if (loading) return;
							setPaused(prev => !prev);
						}}
                    >
						{(loading && currentScene !== null) && <div
                                className={"border-4 w-8 h-8 border-yellow-500 border-b-transparent rounded-full animate-spin"}/> || paused &&
                            <Pause/> || <Play/>}
                    </button>
                    <button className={"absolute top-1/2 right-4 -translate-y-1/2 p-4 bg-neutral-800/70 rounded-xl"}
                            onClick={() => {
								props.setCurrentScene(prev => (prev ?? 0) + 1)
							}}>
                        <ChevronRight/>
                    </button>
                    <button
                        className={"absolute top-4 right-4 group flex flex-col items-center gap-2 pb-12"}
                        onMouseLeave={() => {
							setVolumeSet(false);
						}}
                        onMouseUp={() => {
							setVolumeSet(false);
						}}
                        onMouseMove={(e) => {
							if (volumeSet) {
								setVolume(Math.min((e.clientY - (volumeContainer.current?.getBoundingClientRect()?.top ?? 0)) / (volumeContainer.current?.getBoundingClientRect().height ?? 1), 1));
							}
						}}
                    >
						{(volume === 0 || muted) && <VolumeX onClick={mute}/>}
						{(volume < 0.33 && !muted) && <Volume onClick={mute}/>}
						{(volume < 0.66 && volume >= 0.33 && !muted) && <Volume1 onClick={mute}/>}
						{(volume >= 0.66 && !muted) && <Volume2 onClick={mute}/>}
                        <div
                            ref={volumeContainer}
                            className={"transition-all h-0 group-hover:h-20 w-1 bg-neutral-100/50 rounded"}
                            onMouseDown={(e) => {
								setVolume((e.clientY - (volumeContainer.current?.getBoundingClientRect()?.top ?? 0)) / (volumeContainer.current?.getBoundingClientRect().height ?? 1));
								setVolumeSet(true);
							}}
                        >
                            <div className={"bg-yellow-500 w-full rounded"} style={{height: (volume * 100) + "%"}}/>
                        </div>
                    </button>
                    <GifMaker currentScene={props.scenes[currentScene]} api={props.api} height={height}/>
                </div>
			}
		</div>
	)
}

interface TextBoxProps {
	currentScene: Scene,
	api: API,
	height: number,
}

function GifMaker(props: TextBoxProps) {
	const [textVisible, setTextVisible] = useState(false);
	const container = useRef<HTMLDivElement>(null);
	const [loading, setLoading] = useState(false);
	const [status, setStatus] = useState("");
	const [textSize, setTextSize] = useState("156");
	const [textHeight, setTextHeight] = useState(199);
	useEffect(() => {
		if(!isNaN(parseInt(textSize))) {
			props.api.getTextSize(props.currentScene, container.current?.innerText ?? "", parseInt(textSize)).then((res) => {
				setTextHeight(res * props.height / props.currentScene.episode.height)
			})
		}
	}, [props.api, props.currentScene, props.height, textSize]);
	// const textAreaRef = useRef<HTMLTextAreaElement>(null);
	// const [x, setX] = useState(50);
	// const [y, setY] = useState(50);
	// const [dragging, setDragging] = useState(false);
	// const mouseUp = useCallback(() => {
	// 	setDragging(false);
	// }, []);
	// const mouseMove = useCallback((e: MouseEvent) => {
	// 	if (dragging) {
	// 		setX(prev => prev + e.movementX);
	// 		setY(prev => prev + e.movementY);
	// 	}
	// }, [dragging]);
	// useEffect(() => {
	// 	document.addEventListener("mouseup", mouseUp);
	// 	document.addEventListener("mousemove", mouseMove);
	// 	return () => {
	// 		document.removeEventListener("mouseup", mouseUp);
	// 		document.removeEventListener("mousemove", mouseMove);
	// 	}
	// }, [mouseMove, mouseUp]);
	const navigate = useNavigate();
	return (
		<>
			<div className={"absolute top-4 left-4 flex gap-4 items-center"}>
				<div title={"Ceci est une prévisualisation, le gif final peut être légèrement différent"}>
					<Info/>
				</div>
				<div className={"flex gap-2"}>
					<button title={"Ajouter du texte au gif"} onClick={() => {
						setTextVisible(prev => !prev);
					}}>
						<Type/>
					</button>
					<input className={clsx("bg-transparent border border-yellow-500 rounded-md p-1 w-12", textVisible || "hidden")} value={textSize} onChange={(e) => { setTextSize(e.target.value)}}/>
				</div>
				<button className={"flex gap-2"} title={"Créer le gif"} onClick={() => {
					setLoading(true);
					props.api.createGif(props.currentScene, textVisible ? container.current?.innerText ?? "" : "", parseInt(textSize), (data) => {
						if(data.error !== undefined) {
							setLoading(false);
							setStatus(data.error);
						} else if(data.gifId !== undefined) {
							setLoading(false);
							navigate("/gif/" + data.gifId);
						} else if(data.gif === true) {
							setStatus("Le gif est prêt");
						} else if(data.text === true) {
							setStatus("Le texte est enluminé");
						} else if(data.textLength === true) {
							setStatus("Les mesures sont prises");
						} else if(data.scene === true) {
							setStatus("La scène est monté");
						}
					}).catch(() => {
						setLoading(false);
					})
				}}>
					{loading && <Spinner className={"!h-6 !w-6 !border-yellow-500"}/> || <Save/>}
					{status}
				</button>
			</div>
			<div
				className={clsx("absolute border-dotted p-2 bottom-8 w-full kaamelott text-white text-center focus:outline-0", textVisible || "hidden")}
				style={{fontSize: textHeight + "px"}}
				contentEditable
				ref={container}
				// style={{top: y, left: x}}
				// onMouseDown={() => {setDragging(true);}}
				// onMouseUp={() => {setDragging(false);}}
				suppressContentEditableWarning={true}
			>
				{/*<textarea*/}
				{/*	className={"bg-transparent text-5xl field-sizing-content p-1 text-white kaamelott resize-none text-center"}*/}
				{/*	value={text} onChange={(e) => setText(e.target.value)}/>*/}
				Tapez votre texte
			</div>
		</>
	)
}