import { useCallback, useMemo, useState } from "react";
import API from "../../Api/Api";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import { RouterLayout } from "./RouterLayout";
import Home from "../Home";
import Gif from "../../Pages/Gif";
import UserGifs from "../../Pages/UserGifs";
import Series from "../../Pages/Series";
import Seasons from "../../Pages/Seasons";
import Episodes from "../../Pages/Episodes.tsx";
import EpisodePage from "../../Pages/EpisodePage.tsx";

export function Router() {
    const [bottom, setBottom] = useState(false);
    const api = useMemo(() => new API(import.meta.env.VITE_API), []);
    const handleScroll = useCallback((e: React.UIEvent<HTMLDivElement>) => {
        const target = e.currentTarget;
        if (target.scrollHeight - target.scrollTop === target.clientHeight) {
            setBottom(true)
        } else {
            setBottom(false);
        }
    }, []);

    const router = createBrowserRouter([
        {
            path: "/",
            element: <RouterLayout handleScroll={handleScroll} setBottom={setBottom} api={api} />,
            children: [
                {
                    path: "/",
                    element: <Home api={api} bottom={bottom} />,
                },
                {
                    path: "gif/:id",
                    element: <Gif api={api} />
                },
                {
                    path: "gif/me",
                    element: <UserGifs api={api} bottom={bottom} />
                },
                {
                    path: "series",
                    element: <Series api={api} />
                },
                {
                    path: "series/:name",
                    element: <Seasons api={api} />
                },
                {
                    path: "series/:name/:season",
                    element: <Episodes api={api} bottom={bottom}/>
                },
                {
                    path: "series/:name/:season/:episode",
                    element: <EpisodePage api={api}/>
                }
            ]
        }
    ]);

    return <RouterProvider router={router} />;
}
