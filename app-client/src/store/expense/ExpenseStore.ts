import log from 'loglevel';
import { action, computed, makeObservable, observable } from 'mobx';

import client from '@/api/client.js';
import { ExpenseRequestDTO } from '@/generated/ExpenseRequestDTO';
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
            createExpense: action.bound,
            setData: action.bound,
            clear: action.bound,
        });
    }

    get totalAmount(): number {
        return this.items.map((item) => item.total ?? 0).reduce((prev, next) => prev + next, 0);
    }

    async fetchData(): Promise<void> {
        if (this.loaded) {
            return;
        }
        try {
            const { data } = await client.get('/expenses');
            log.debug('Successfully fetched current user expenses');
            this.setData(data);
        } catch (e) {
            log.error('Unable to fetch current user expenses', e);
            this.clear();
        }
    }

    async createExpense(expense: ExpenseRequestDTO): Promise<void> {
        try {
            const { data } = await client.post('/expenses', expense);
            log.debug('Successfully created user expense');
            this.setData([...this.items, data]);
        } catch (e) {
            log.error('Unable to create user expense', e);
        }
    }

    setData(items: ExpenseResponseDTO[], loaded = true): void {
        this.items = items;
        this.loaded = loaded;
    }

    clear(): void {
        this.setData([], false);
    }
}
