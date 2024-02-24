import OAuth2Login from "react-simple-oauth2-login";
import React from "react";

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
			<div>
				{JSON.stringify(user)}
			</div>
		)
	}
}