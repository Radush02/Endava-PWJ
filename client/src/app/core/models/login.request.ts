export interface LoginDTO{
    username: string;
    password: string;
}

export interface LoginResultDTO{
    id: number;
    username: string;
    email: string;
    fullName: string;
    role: Role;
    accessToken: string;
    refreshToken: string;
}

export enum Role{
    USER = 'User',
    ADMIN = 'Admin'
}