import log from 'loglevel';
import { makeAutoObservable } from 'mobx';

import client from '@/api/client.js';
import { UserResponseDTO } from '@/generated/UserResponseDTO';
import { Store } from '@/store/Store';
import { Optional } from '@/utils/core-utils';

export default class UserStore {

    root?: Store;

    id: Optional<number> = null;
    username: Optional<string> = null;
    authorities: Optional<string[]> = null;
    authenticated: boolean = false;

    constructor() {
        makeAutoObservable(this, { root: false }, { autoBind: true })
    }

    async signUp(username: string, password: string): Promise<void> {
        const { data } = await client.post('/users', {
            username,
            password,
        });
        this.setData(data);
    }

    async fetchData(): Promise<void> {
        if (!this.authenticated) {
            try {
                const { data } = await client.get('/users/current');
                log.debug('Successfully fetched current user');
                this.setData(data);
            } catch (e) {
                log.error('Unable to fetch current user info', e);
                this.clear();
            }
        }
    }

    setData(data: Partial<UserResponseDTO>) {
        this.id = data.id;
        this.username = data.username;
        this.authorities = data.authorities;
        this.authenticated = ((data.id !== null) && (data.id !== undefined));
    }

    clear() {
        this.setData({});
    }
}
