export interface JwtPayload {
    sub: string;
    permissions: string[];
    givenName: string;
    familyName: string;
    iss: string;
    name: string;
    roles: string[];
    refresh: boolean;
    exp: number;
    userId: number;
    iat: number;
    email: string;
}
