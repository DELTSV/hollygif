import {useEffect, useMemo, useState} from "react";
import {clsx} from "clsx";
import {ExternalLink} from "react-feather";
import {Link} from "react-router-dom";

interface CarouselProps {
	gifs: Gif[];
	fetchMore: () => void;
}

export default function Carousel(props: CarouselProps) {
	const [currentIndex, setCurrentIndex] = useState(0);
	const {gifs, fetchMore} = props;
	const images = useMemo(() => gifs?.map(g => import.meta.env.VITE_API + "/api/gif/file/" + g.file), [gifs]);
	const canPrev = useMemo(() => currentIndex > 0, [currentIndex]);
	const canNext = useMemo(() => currentIndex < images.length - 1, [currentIndex, images.length]);

	useEffect(() => {
		if (currentIndex == images.length - 2) {
			fetchMore();
		}
	}, [currentIndex, fetchMore, images.length])

	const nextSlide = () => {
		setCurrentIndex((prevIndex) =>
			prevIndex === images.length - 1 ? 0 : prevIndex + 1
		);
	};

	const prevSlide = () => {
		setCurrentIndex((prevIndex) =>
			prevIndex === 0 ? images.length - 1 : prevIndex - 1
		);
	};

	return (
		<div className="relative w-3/4 mx-auto">
			<div className="overflow-hidden rounded-lg gap-4 flex relative justify-center items-center h-96">
				{!canPrev && <div className="w-1/4"/>}
				{images.map((img, index) => {
					if (index === currentIndex - 1) {
						return (
							<div key={index}
								 className={clsx("absolute skew-y-12 w-1/2 overflow-x-hidden origin-right scale-90 opacity-75 blur-sm -translate-x-full transition")}>
								<img src={img} className={"h-full object-cover object-right"} onClick={prevSlide}/>
							</div>
						);
					} else if (index === currentIndex) {
						return (
							<div key={index} className={clsx("absolute w-1/2 translate-x-0 transition")}>
								<Link to={"/gif/" + gifs[currentIndex].id} className={"w-full h-full"}>
									<ExternalLink className={"h-8 w-8 absolute top-2 right-2"}/>
								</Link>
								<img src={img} className="w-full object-cover"/>
							</div>
						);
					} else if(index === currentIndex + 1) {
						return (
							<div key={index} className={clsx("absolute -skew-y-12 w-1/2 overflow-x-hidden origin-left scale-90 opacity-75 blur-sm translate-x-full transition")}>
								<img src={images[currentIndex + 1]} className={"h-full object-cover object-left"} onClick={nextSlide}/>
							</div>
						);
					}
				})}
				{!canNext && <div className="w-1/4"/>}
			</div>
		</div>
	);
}