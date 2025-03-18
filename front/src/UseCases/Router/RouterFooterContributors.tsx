export default function FooterContributors() {
    const contributors: Contributors[] = [
        { name: "Denis Turbiez", githubId: "MacaronFR" },
        { name: "Enzo Soares", githubId: "enzoSoa" },
        { name: "Loïc Vanden Bossche", githubId: "Loic-Vanden-Bossche" },
    ]

    return <ul className="flex flex-row gap-2">
        {contributors.map((contributor, index) =>
            <a key={index} className="cursor-pointer hover:underline"
                href={`https://github.com/${contributor.githubId}`}
                title={contributor.name}>
                {contributor.name[0]}
            </a>
        )}
    </ul >;
}