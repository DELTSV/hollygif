import OAuth2Login from "react-simple-oauth2-login";
import React, { useCallback, useEffect, useState } from "react";
import Spinner from "./Components/Spinner.tsx";
import API from "./Api/Api.ts";
import Button from "./Components/Button.tsx";

interface DiscordAuthProps {
	token: string | null,
	setToken: React.Dispatch<React.SetStateAction<string | null>>
	user: User | null
	setUser: React.Dispatch<React.SetStateAction<User | null>>
	redirectUri: string,
	clientId: string,
	scope: string,
	api: API
}

export default function DiscordAuth(props: DiscordAuthProps) {
	const { token, setToken, user, setUser } = props;

	const getUser = useCallback((token: string) => {
		const h = new Headers();
		h.append("Authorization", "Bearer " + token);
		fetch("https://discord.com/api/v10/users/@me", { headers: h }).then(async (r) => {
			setUser(await r.json());
		}).catch(() => {
			setToken(null);
			localStorage.removeItem("token")
			props.api.refreshToken();
		});
	}, [props.api, setToken, setUser]);

	const [loading, setLoading] = useState(true);
	useEffect(() => {
		const t = localStorage.getItem("token");
		if (t === null) {
			setLoading(false);
			props.api.refreshToken();
		} else {
			setLoading(false);
			setToken(t);
			getUser(t);
			props.api.refreshToken();
		}
	}, [props.api, setToken, getUser]);
	if (loading) {
		return (
			<Spinner />
		)
	}
	if (token === null) {
		return (
			<OAuth2Login
				authorizationUrl={"https://discord.com/oauth2/authorize"}
				responseType={"token"}
				clientId={props.clientId}
				redirectUri={props.redirectUri}
				className={"border-2 border-yellow-500 hover:text-black hover:bg-yellow-500 px-4 py-2 mx-2 rounded-lg hover:scale-110 transition"}
				onSuccess={(r) => {
					setToken(r["access_token"]);
					localStorage.setItem("token", r["access_token"]);
					getUser(r["access_token"]);
					props.api.refreshToken();
				}}
				onFailure={console.error}
				scope={props.scope}
			/>
		);
	} else {
		return (
			<div className={"flex items-center gap-4"}>
				<p className={"text-xl"}>{user?.global_name}</p>
				<img src={`https://cdn.discordapp.com/avatars/${user?.id}/${user?.avatar}.png`} alt={"Profile picture"} className={"rounded-full h-12"} />
				<Button onClick={() => {
					setUser(null);
					setToken(null);
					localStorage.removeItem("token");
					props.api.refreshToken();
				}}>Se déconnecter</Button>
			</div>
		)
	}
}