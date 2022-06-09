import client from '@/api/client.js';
import { UserResponse } from '@/generated/UserResponse';

export async function getCurrentUser(): Promise<UserResponse> {
    const { data } = await client.get('/users/current');
    return data;
}

export async function signUp(username: string, password: string): Promise<UserResponse> {
    const { data } = await client.post('/users', {
        username,
        password,
    });
    return data;
}
