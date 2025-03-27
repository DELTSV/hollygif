import {useCallback, useEffect, useMemo, useRef, useState} from "react";
import API from "../../Api/Api";
import {Link, Outlet} from "react-router-dom";
import DiscordAuth from "../../DiscordAuth";
import {Footer} from "./RouterFooter.tsx";
import {Search} from "react-feather";
import {clsx} from "clsx";
import SearchResult from "../../Types/SearchResult.ts";
import ResultContainer from "../../Components/ResultContainer.tsx";

interface RouterLayoutProps {
	handleScroll: (e: React.UIEvent<HTMLDivElement>) => void,
	setBottom: (isBottom: boolean) => void,
	api: API
}

export function RouterLayout(props: RouterLayoutProps) {
	const [user, setUser] = useState<User | null>(null);
	const [userToken, setUserToken] = useState<string | null>(null);
	const inputBox = useRef<HTMLInputElement>(null);
	const [timeout, setTimeout1] = useState<number | null>(null);
	const [searchText, setSearchText] = useState("");
	const [searchFocus, setSearchFocus] = useState(false);
	const [searchData, setSearchData] = useState<SearchResult[] | null>(null);

	useEffect(() => {
		if(inputBox.current) {
			inputBox.current.onfocus = () => {
				setSearchFocus(true);
			}
			inputBox.current.onblur = () => {
				setSearchFocus(false);
			}
		}
	}, []);

	const search = useCallback((search: string) => {
		if (timeout != null) {
			clearTimeout(timeout);
			setTimeout1(null);
		}
		setTimeout1(setTimeout(() => {
			if(search !== "") {
				props.api.search(search).then((data) => {
					setSearchData(data);
				});
			}
		}, 500));
	}, [props.api, timeout]);

	return (
		<>
			<div className={"w-screen h-screen max-h-screen flex flex-col text-yellow-500 shadow"}>
				<div
					className={"w-full grid grid-cols-3 justify-between bg-black items-center px-8 py-2 drop-shadow-header z-20"}>
					<div className={"flex items-center gap-8 grow"}>
						<Link className={"text-3xl"} to={"/"}>
							Kaamelott - gif
						</Link>
						<a className={"h-12"}
						   href='https://play.google.com/store/apps/details?id=fr.imacaron.mobile.gif&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
							<img className={"h-full"} alt='Disponible sur Google Play'
								 src='https://play.google.com/intl/en_us/badges/static/images/badges/fr_badge_web_generic.png'/>
						</a>
					</div>
					<div className={"flex justify-center items-center gap-4 text-xl"}>
						{user !== null &&
                            <Link to={"/gif/me"}>Mes gifs</Link>
						}
						<Link to={"/series"}>Les séries</Link>
						<div className={"flex items-center gap-2 relative"}>
							<input
								className={clsx("transition-all bg-transparent focus:outline-0 focus:border-2 border-white rounded p-1", (searchText !== "" || searchFocus) && "w-48" || "w-0")}
								type={"search"}
								ref={inputBox}
								value={searchText}
								onChange={(e) => {
									setSearchText(e.target.value);
									search(e.target.value);
								}}
							/>
							<Search onClick={() => {
								if(!searchFocus) {
									inputBox.current?.focus()
								}
							}} className={"cursor-pointer"}/>
							<div className={clsx("flex-col absolute top-full bg-neutral-900 rounded", (searchText !== "" || searchFocus) && "flex" || "hidden")}>
								<div className={"flex gap-2 p-2"}>
									<span>Épisode</span>
									<span>Gif</span>
									<span>Texte</span>
								</div>
								<div className={clsx("flex-col", searchData && "flex" || "hidden")}>
									{searchData?.map((res, key) => (
										<ResultContainer result={res} key={key}/>
									))}
								</div>
							</div>
						</div>
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
						<Outlet/>
					</div>
				</div>
				<Footer/>
			</div>
		</>
	)
}