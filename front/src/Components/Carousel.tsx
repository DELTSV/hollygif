import {useState} from "react";

interface CarouselProps {
	images: string[];
}

 export default function Carousel(props: CarouselProps) {
	const [currentIndex, setCurrentIndex] = useState(0);

	const nextSlide = () => {
		setCurrentIndex((prevIndex) =>
			prevIndex === props.images.length - 1 ? 0 : prevIndex + 1
		);
	};

	const prevSlide = () => {
		setCurrentIndex((prevIndex) =>
			prevIndex === 0 ? props.images.length - 1 : prevIndex - 1
		);
	};

	return (
		<div className="relative w-full max-w-3xl mx-auto">
			<div className="overflow-hidden rounded-lg">
				<img
					src={props.images[currentIndex]}
					alt={`Gif ${currentIndex + 1}`}
					className="w-full h-64 object-cover transition-all duration-500"
				/>
			</div>
			<button
				className="absolute top-1/2 left-3 transform -translate-y-1/2 p-2 rounded-full bg-[#432004] bg-opacity-50 hover:bg-opacity-75"
				onClick={prevSlide}
			>
				&lt;
			</button>
			<button
				className="absolute top-1/2 right-3 transform -translate-y-1/2 p-2 rounded-full bg-[#432004] bg-opacity-50 hover:bg-opacity-75"
				onClick={nextSlide}
			>
				&gt;
			</button>
			<div className="flex justify-center mt-4 space-x-2">
				{props.images.map((_, index) => (
					<button
						key={index}
						onClick={() => setCurrentIndex(index)}
						className={`w-3 h-3 rounded-full ${
							index === currentIndex
								? "bg-yellow-500"
								: "bg-[#432004] hover:bg-yellow-700"
						}`}
					/>
				))}
			</div>
		</div>
	);
}