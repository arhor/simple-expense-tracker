import log from 'loglevel';
import { action, computed, makeObservable, observable, runInAction } from 'mobx';

import client from '@/api/client.js';
import { ExpenseItemRequestDTO } from '@/generated/ExpenseItemRequestDTO';
import { ExpenseItemResponseDTO } from '@/generated/ExpenseItemResponseDTO';
import { ExpenseRequestDTO } from '@/generated/ExpenseRequestDTO';
import { ExpenseResponseDTO } from '@/generated/ExpenseResponseDTO';
import { Store } from '@/store/Store';

export default class ExpenseStore {
    root?: Store;

    expenses = [] as ExpenseResponseDTO[];
    expenseItems = new Map<number, ExpenseItemResponseDTO[]>();
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
            log.error('Unable to fetch current user expenses, still using previously loaded data', e);
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
        try {
            const { data } = await client.get(`/expenses/${expenseId}/items`);
            log.debug(`Successfully fetched expense items by expense id: ${expenseId}`);
            this.expenseItems.set(expenseId, data);
        } catch (e) {
            log.error('Unable to fetch current user expenses', e);
        }
    }

    async createExpenseItem(expenseId: number, expenseItem: ExpenseItemRequestDTO): Promise<void> {
        try {
            const { data } = await client.post(`/expenses/${expenseId}/items`, expenseItem);
            runInAction(() => {
                this.expenseItems.set(expenseId, [...(this.expenseItems.get(expenseId) ?? []), data]);
                this.expenses = this.expenses.map((expense) =>
                    (expense.id === expenseId)
                        ? { ...expense, total: expense.total + data.amount }
                        : expense
                );
            });
            log.debug('Successfully created expense item');
        } catch (e) {
            log.error('Unable to create expense item', e);
        }
    }
}
