import Button from "../Components/Button.tsx";

export default function ReturnToApp() {
	return (
		<a href={window.location.href}>
			<Button>
				Retourner à l'application
			</Button>
		</a>
	);
}