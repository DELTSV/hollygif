import OAuth2Login from "react-simple-oauth2-login";
import React, {useCallback, useEffect, useState} from "react";
import Button from "./Components/Button.tsx";
import Spinner from "./Components/Spinner.tsx";

interface DiscordAuthProps {
	token: string|null,
	setToken: React.Dispatch<React.SetStateAction<string|null>>
	user: User|null
	setUser: React.Dispatch<React.SetStateAction<User|null>>
	redirectUri: string,
	clientId: string,
	scope: string
}

export default function DiscordAuth(props: DiscordAuthProps) {
	const {token, setToken, user, setUser} = props;

	const getUser = useCallback((token: string) => {
		const h = new Headers();
		h.append("Authorization", "Bearer " + token);
		fetch("https://discord.com/api/v10/users/@me", {headers: h}).then(async (r) => {
			setUser(await r.json());
		});
	}, [setUser]);

	const [loading, setLoading] = useState(true);
	useEffect(() => {
		const t = localStorage.getItem("token");
		if(t === null) {
			setLoading(false);
		} else {
			setLoading(false);
			setToken(t);
			getUser(t);
		}
	}, [setToken, getUser]);
	if(loading) {
		return (
			<Spinner/>
		)
	}
	if(token === null) {
		return (
			<OAuth2Login
				authorizationUrl={"https://discord.com/oauth2/authorize"}
				responseType={"token"}
				clientId={props.clientId}
				redirectUri={props.redirectUri}
				className={"border-2 border-neutral-400 text-neutral-100 hover:text-black hover:bg-neutral-400 px-4 py-2 m-2 rounded-lg hover:scale-110 transition"}
				onSuccess={(r) => {
					setToken(r["access_token"]);
					localStorage.setItem("token", r["access_token"]);
					getUser(r["access_token"]);
				}}
				onFailure={console.error}
				scope={props.scope}
			/>
		);
	} else {
		return (
			<div className={"flex items-center gap-4"}>
				<p className={"text-neutral-100 text-xl"}>{user?.global_name}</p>
				<img src={`https://cdn.discordapp.com/avatars/${user?.id}/${user?.avatar}.png`} alt={"Profile picture"} className={"rounded-full h-12"}/>
				<Button className={"!text-neutral-100 hover:!text-black"} onClick={() => {
					setUser(null);
					setToken(null);
					localStorage.removeItem("token")
				}}>Se déconnecter</Button>
			</div>
		)
	}
}