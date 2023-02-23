import log from 'loglevel';
import { action, makeObservable, observable, runInAction } from 'mobx';

import client from '~/api/client.js';
import { AuthProviderDTO } from '~/generated/AuthProviderDTO';
import { Store } from '~/store/Store';

export default class AppStore {

    root?: Store;

    loaded = false;
    authProviders: AuthProviderDTO[] = [];

    constructor() {
        makeObservable(this, {
            root: false,
            loaded: observable,
            authProviders: observable,
            fetchAuthProviders: action.bound,
        });
    }

    async fetchAuthProviders(): Promise<void> {
        if (this.loaded) {
            return;
        }
        try {
            const { data } = await client.get('/auth-providers');
            log.debug('Successfully fetched available authentication providers');
            runInAction(() => {
                this.authProviders = data;
                this.loaded = true;
            });
        } catch (e) {
            log.error('Unable to fetch available authentication providers', e);
        }
    }
}
