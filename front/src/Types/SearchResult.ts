export default interface SearchResult {
	total: number,
	showed: number,
	page: number,
	type: string
	data: any[],
}