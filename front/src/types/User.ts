interface User {
	id: string,
	username: string,
	avatar: string|null,
	bot?: boolean,
	system?: boolean,
	discriminator: string,
	public_flag?: number,
	premium_type?: number,
	flags?: number,
	banner?: string|null,
	accent_color?: number|null,
	global_name: string|null,
	avatar_decoration_data?: string|null,
	banner_color: string,
	mfa_enabled?: boolean,
	locale?: string
	verified?: boolean,
	email?: string|null,

}