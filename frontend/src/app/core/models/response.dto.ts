export interface ResponseDto<T> {
    successful: boolean;
    message: string;
    data: T | null;
}
