export interface UserDto {
    userId: number;
    email: string;
    firstName: string;
    lastName: string;
    phone: string;
    description: string;
    txUser: string;
    txDate: Date;
    groups: string[];
    roles: string[];
}
