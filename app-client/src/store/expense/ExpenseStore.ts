import log from 'loglevel';
import { action, computed, makeObservable, observable } from 'mobx';

import client from '@/api/client.js';
import { ExpenseResponseDTO } from '@/generated/ExpenseResponseDTO';
import { Store } from '@/store/Store';

export default class ExpenseStore {

    root?: Store;

    items: ExpenseResponseDTO[] = [];
    loaded = false;

    constructor() {
        makeObservable(this, {
            root: false,
            items: observable,
            loaded: observable,
            totalAmount: computed,
            fetchData: action.bound,
            setData: action.bound,
            clear: action.bound,
        });
    }

    get totalAmount() {
        return this.items.map((item) => item.total).reduce((prev, next) => prev + next, 0);
    }

    async fetchData(): Promise<void> {
        if (this.loaded) {
            return;
        }
        try {
            const { data } = await client.get('/expenses');
            log.debug('Successfully fetched current user expenses');
            this.setData(data, true);
        } catch (e) {
            log.error('Unable to fetch current user expenses', e);
            this.clear();
        }
    }

    setData(items: ExpenseResponseDTO[], loaded: boolean): void {
        this.items = items;
        this.loaded = loaded;
    }

    clear() {
        this.setData([], false);
    }
}
