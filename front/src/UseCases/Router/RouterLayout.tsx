import { useState } from "react";
import API from "../../Api/Api";
import { Link, Outlet } from "react-router-dom";
import DiscordAuth from "../../DiscordAuth";
import {Footer} from "./RouterFooter.tsx";

interface RouterLayoutProps {
    handleScroll: (e: React.UIEvent<HTMLDivElement>) => void,
    setBottom: (isBottom: boolean) => void,
    api: API
}

export function RouterLayout(props: RouterLayoutProps) {
    const [user, setUser] = useState<User | null>(null);
    const [userToken, setUserToken] = useState<string | null>(null);

    return (
        <>
            <div className={"w-screen h-screen max-h-screen flex flex-col text-yellow-500 shadow"}>
                <div className={"w-full grid grid-cols-3 justify-between bg-black items-center px-8 py-2 drop-shadow-header"}>
                    <div className={"flex items-center gap-8 grow"}>
                        <Link className={"text-3xl"} to={"/"}>
                            Kaamelott - gif
                        </Link>
                        <a className={"h-12"} href='https://play.google.com/store/apps/details?id=fr.imacaron.mobile.gif&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
                            <img className={"h-full"} alt='Disponible sur Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/fr_badge_web_generic.png'/>
                        </a>
                    </div>
                    <div className={"flex justify-center gap-4"}>
                        {user !== null &&
                            <Link to={"/gif/me"}>Mes gifs</Link>
                        }
                        <Link to={"/series"}>Les séries</Link>
                    </div>
                    <div className={"grow flex justify-end"}>
                        <DiscordAuth
                            user={user}
                            setUser={setUser}
                            token={userToken}
                            setToken={setUserToken}
                            redirectUri={import.meta.env.VITE_REDIRECT}
                            clientId={import.meta.env.VITE_CLIENT_ID}
                            scope={"identify"}
                            api={props.api}
                        />
                    </div>
                </div>
                <div className={"grow py-4 relative overflow-auto"} onScroll={props.handleScroll} onLoad={e => {
                    if (e.currentTarget.scrollHeight === e.currentTarget.clientHeight) {
                        props.setBottom(true);
                    }
                }}>
                    <div className={"relative z-10 w-full flex flex-col items-center"}>
                        <Outlet />
                    </div>
                </div>
                <Footer/>
            </div>
        </>
    )
}