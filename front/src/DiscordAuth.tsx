import OAuth2Login from "react-simple-oauth2-login";
import React from "react";
import Button from "./Components/Button.tsx";

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
	const {token, setToken, user, setUser} = props
	if(token === null) {
		return (
			<OAuth2Login
				authorizationUrl={"https://discord.com/oauth2/authorize"}
				responseType={"token"}
				clientId={props.clientId}
				redirectUri={props.redirectUri}
				className={"border-2 border-neutral-400 text-neutral-100 hover:text-black hover:bg-neutral-400 px-4 py-2 m-2 rounded-lg hover:scale-110 transition"}
				onSuccess={(r) => {
					const h = new Headers();
					setToken(r["access_token"]);
					h.append("Authorization", "Bearer " + r["access_token"]);
					fetch("https://discord.com/api/v10/users/@me", {headers: h}).then(async (r) => {
						setUser(await r.json());
					});
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
				}}>Se déconnecter</Button>
			</div>
		)
	}
}