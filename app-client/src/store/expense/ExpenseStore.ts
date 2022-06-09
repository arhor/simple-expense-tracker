import log from 'loglevel';
import { action, computed, observable, makeObservable, runInAction } from 'mobx';

import { getCurrentUserExpenses } from '@/api/expenseClient';
import { ExpenseDTO } from '@/generated/ExpenseDTO';
import { RootStore } from '@/store/RootStore';

export default class ExpenseStore {

    root?: RootStore;

    items: ExpenseDTO[] = [];
    loaded = false;

    constructor() {
        makeObservable(this, {
            root: false,
            items: observable,
            loaded: observable,
            totalAmount: computed,
            fetchData: action.bound
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
            const expenses = await getCurrentUserExpenses();

            log.debug('Successfully fetched current user expenses');

            runInAction(() => {
                this.items = expenses;
                this.loaded = true;
            });
        } catch (e) {
            log.error('Unable to fetch current user expenses', e);

            runInAction(() => {
                this.items = [];
                this.loaded = false;
            });
        }
    }
}
