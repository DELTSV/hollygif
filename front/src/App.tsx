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
                    redirectUri={"http://localhost:5173/"}
                    clientId={"1203342110917402666"}
                    scope={"identify"}
                />
            </div>
        </>
    )
}

export default App
