import { Role } from "./login.request";

export interface UserDTO{
    id: number;
    email: string;
    username: string;
    fullName: string;
    role: Role;
    image: string;
}