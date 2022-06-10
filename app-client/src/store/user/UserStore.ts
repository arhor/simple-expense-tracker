import log from 'loglevel';
import { action, makeObservable, observable } from 'mobx';

import client from '@/api/client.js';
import { UserResponse } from '@/generated/UserResponse';
import { Store } from '@/store/Store';
import { Optional } from '@/utils/core-utils';

export default class UserStore {

    root?: Store;

    id: Optional<number> = null;
    username: Optional<string> = null;
    authenticated = false;

    constructor() {
        makeObservable(this, {
            root: false, 
            id: observable,
            username: observable,
            authenticated: observable,
            signUp: action.bound,
            fetchData: action.bound,
            setData: action.bound,
            clear: action.bound,
        });
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

    setData(data: Partial<UserResponse>) {
        this.id = data.id;
        this.username = data.username;
        this.authenticated = Boolean(data.id) && Boolean(data.username);
    }

    clear() {
        this.setData({});
    }
}
