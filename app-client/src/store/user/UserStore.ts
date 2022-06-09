import log from 'loglevel';
import { action, observable, makeObservable, runInAction } from 'mobx';

import { getCurrentUser } from '@/api/userClient';
import { RootStore } from '@/store/RootStore';
import { Optional } from '@/utils/core-utils';

export default class UserStore {

    root?: RootStore;

    id: Optional<number> = null;
    username: Optional<string> = null;
    authenticated = false;

    constructor() {
        makeObservable(this, {
            root: false, 
            id: observable,
            username: observable,
            authenticated: observable,
            fetchData: action.bound,
        });
    }

    async fetchData(): Promise<void> {
        if (!this.authenticated) {
            try {
                const user = await getCurrentUser();

                log.debug('Sucessfully fetched current user');

                runInAction(() => {
                    this.id = user.id;
                    this.username = user.username;
                    this.authenticated = true;
                });
            } catch (e) {
                log.error('Unable to fetch current user info', e);

                runInAction(() => {
                    this.id = null;
                    this.username = null;
                    this.authenticated = false;
                });
            }
        }
    }
}
