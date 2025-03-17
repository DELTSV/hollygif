interface Episode {
	id: number,
	number: number,
	width: number,
	height: number,
	fps: number,
	title: string,
	duration: number,
	season: Season,
	numberOfGif?: number
}