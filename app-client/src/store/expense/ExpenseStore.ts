import log from 'loglevel';
import { action, computed, makeObservable, observable } from 'mobx';

import client from '@/api/client.js';
import { ExpenseItemDTO } from '@/generated/ExpenseItemDTO';
import { ExpenseRequestDTO } from '@/generated/ExpenseRequestDTO';
import { ExpenseResponseDTO } from '@/generated/ExpenseResponseDTO';
import { Store } from '@/store/Store';

export default class ExpenseStore {
    root?: Store;

    expenses: ExpenseResponseDTO[] = [];
    expenseItems: Map<number, ExpenseItemDTO[]> = new Map();
    loaded = false;

    constructor() {
        makeObservable(this, {
            root: false,
            expenses: observable,
            expenseItems: observable,
            loaded: observable,
            totalAmount: computed,
            fetchExpenses: action.bound,
            createExpense: action.bound,
            fetchExpenseItems: action.bound,
            createExpenseItem: action.bound,
            setData: action.bound,
            clear: action.bound,
        });
    }

    get totalAmount(): number {
        return this.expenses.map((expense) => expense.total ?? 0).reduce((prev, next) => prev + next, 0);
    }

    async fetchExpenses(): Promise<void> {
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
            this.setData([...this.expenses, data]);
        } catch (e) {
            log.error('Unable to create user expense', e);
        }
    }

    async fetchExpenseItems(expenseId: number): Promise<void> {
        if (!this.expenseItems.has(expenseId)) {
            try {
                const { data } = await client.get(`/expenses/${expenseId}/items`);
                log.debug(`Successfully fetched expense items by expense id: ${expenseId}`);
                this.expenseItems.set(expenseId, data);
            } catch (e) {
                log.error('Unable to fetch current user expenses', e);
            }
        }
    }

    async createExpenseItem(expenseId: number, expenseItem: ExpenseItemDTO): Promise<void> {
        try {
            const { data } = await client.post(`/expenses/${expenseId}/items`, expenseItem);
            log.debug('Successfully created expense item');
            this.expenseItems.set(expenseId, [...(this.expenseItems.get(expenseId) ?? []), data]);
        } catch (e) {
            log.error('Unable to create expense item', e);
        }
    }

    setData(expenses: ExpenseResponseDTO[], loaded = true): void {
        this.expenses = expenses;
        this.loaded = loaded;
    }

    clear(): void {
        this.setData([], false);
        this.expenseItems.clear();
    }
}
