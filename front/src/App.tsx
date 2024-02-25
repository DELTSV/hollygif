import {useMemo, useState} from 'react';
import DiscordAuth from "./DiscordAuth.tsx";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Gif from "./Pages/Gif.tsx";
import Home from "./Pages/Home.tsx";
import "./App.css"
import API from "./api/api.ts";

function App() {
    const [user, setUser] = useState<User|null>(null);
    const [userToken, setUserToken] = useState<string|null>(null);

    const api = useMemo(() => new API(import.meta.env.VITE_API), []);

    const router = createBrowserRouter([
        {
            path: "/",
            element: <Home api={api}/>,
        },
        {
            path: "gif/:id",
            element: <Gif api={api}/>
        }
    ])

    return (
        <>
            <div className={"w-screen h-screen max-h-screen flex flex-col"}>
                <div className={"w-full flex justify-between bg-black items-center px-8 py-2 drop-shadow-header"}>
                    <a className={"text-neutral-100 text-3xl"} href={"/"}>
                        Kaamelott - gif
                    </a>
                    <DiscordAuth
                        user={user}
                        setUser={setUser}
                        token={userToken}
                        setToken={setUserToken}
                        redirectUri={import.meta.env.VITE_REDIRECT}
                        clientId={import.meta.env.VITE_CLIENT_ID}
                        scope={"identify"}
                    />
                </div>
                <div className={"grow bg-neutral-400 py-4 relative overflow-auto"}>
                    <div className={"absolute top-0 left-0 w-full h-full bg-logo z-0"}/>
                    <div className={"relative z-10 w-full flex flex-col items-center"}>
                        <RouterProvider router={router}/>
                    </div>
                </div>
                <div className={"bg-black text-neutral-100 flex justify-end px-8 py-2 drop-shadow-footer"}>
                    Fait avec <span className={"text-red-600 px-1"}>♥</span> par imacaron
                </div>
            </div>
        </>
    )
}

export default App
