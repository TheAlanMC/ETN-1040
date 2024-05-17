export interface JwtPayload {
    sub: string;
    roles: string[];
    givenName: string;
    familyName: string;
    iss: string;
    name: string;
    groups: string[];
    refresh: boolean;
    exp: number;
    userId: number;
    iat: number;
    email: string;
}
