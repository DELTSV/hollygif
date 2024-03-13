import { useCallback, useMemo, useState } from "react";
import API from "../../Api/Api";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import { RouterLayout } from "./RouterLayout";
import { GifList, GifDetails } from "..";
import UserGifs from "../../Pages/UserGifs";
import Series from "../../Pages/Series";
import Seasons from "../../Pages/Seasons";

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
                    element: <GifList api={api} bottom={bottom} />,
                },
                {
                    path: "gif/:id",
                    element: <GifDetails api={api} />
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
                    element: "Pas fait"
                }
            ]
        }
    ]);

    return <RouterProvider router={router} />;
}
