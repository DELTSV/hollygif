import { useState } from 'react';
import DiscordAuth from "./DiscordAuth.tsx";

function App() {
    const [user, setUser] = useState<User|null>(null);
    const [token, setToken] = useState<string|null>(null);

    return (
        <>
            <div>
                <DiscordAuth
                    user={user}
                    setUser={setUser}
                    token={token}
                    setToken={setToken}
                    redirectUri={import.meta.env.VITE_REDIRECT}
                    clientId={import.meta.env.VITE_CLIENT_ID}
                    scope={"identify"}
                />
            </div>
        </>
    )
}

export default App
