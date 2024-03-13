import FooterContributors from "./Footer.contributors";

export function Footer() {
    return <footer className={"bg-black flex justify-center px-8 py-2 drop-shadow-footer gap-3"}>
        <p>Fait avec <span className={"text-red-600 px-1"}>♥</span> par</p>
        <FooterContributors />
    </footer>;
}