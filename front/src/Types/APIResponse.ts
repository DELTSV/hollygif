export default interface APIResponse<T> {
	code: number,
	message: string,
	data: T
}