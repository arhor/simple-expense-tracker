import log from 'loglevel';
import { action, computed, makeObservable, observable } from 'mobx';

import client from '@/api/client.js';
import { ExpenseItemRequestDTO } from '@/generated/ExpenseItemRequestDTO';
import { ExpenseItemResponseDTO } from '@/generated/ExpenseItemResponseDTO';
import { ExpenseRequestDTO } from '@/generated/ExpenseRequestDTO';
import { ExpenseResponseDTO } from '@/generated/ExpenseResponseDTO';
import { Store } from '@/store/Store';

export default class ExpenseStore {
    root?: Store;

    expenses: ExpenseResponseDTO[] = [];
    expenseItems: Map<number, ExpenseItemResponseDTO[]> = new Map();
    loading = false;

    constructor() {
        makeObservable(this, {
            root: false,
            expenses: observable,
            expenseItems: observable,
            loading: observable,
            totalAmount: computed,
            fetchExpenses: action.bound,
            createExpense: action.bound,
            fetchExpenseItems: action.bound,
            createExpenseItem: action.bound,
            clear: action.bound,
        });
    }

    get totalAmount(): number {
        return this.expenses.map((expense) => expense.total ?? 0).reduce((prev, next) => prev + next, 0);
    }

    async fetchExpenses(): Promise<void> {
        if (this.loading) {
            return;
        }
        try {
            this.loading = true;
            const { data } = await client.get('/expenses');
            log.debug('Successfully fetched current user expenses');
            this.expenses = data;
        } catch (e) {
            log.error('Unable to fetch current user expenses', e);
            this.clear();
        } finally {
            this.loading = false;
        }
    }

    async createExpense(expense: ExpenseRequestDTO): Promise<void> {
        try {
            await client.post('/expenses', expense);
            log.debug('Successfully created user expense');
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

    async createExpenseItem(expenseId: number, expenseItem: ExpenseItemRequestDTO): Promise<void> {
        try {
            const { data } = await client.post(`/expenses/${expenseId}/items`, expenseItem);
            log.debug('Successfully created expense item');
            this.expenseItems.set(expenseId, [...(this.expenseItems.get(expenseId) ?? []), data]);
        } catch (e) {
            log.error('Unable to create expense item', e);
        }
    }

    clear(): void {
        this.expenses = [];
        this.expenseItems.clear();
    }
}
