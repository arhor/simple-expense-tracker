import AppStore from '@/store/app';
import ExpenseStore from '@/store/expense';
import NotificationStore from '@/store/notification';
import UserStore from '@/store/user';

export type Store = {
    app: AppStore;
    user: UserStore;
    expense: ExpenseStore;
    notification: NotificationStore;
};

export class RootStore implements Store {
    app: AppStore;
    user: UserStore;
    expense: ExpenseStore;
    notification: NotificationStore;

    constructor(provided: Partial<Store> = {}) {
        this.app = Object.assign(provided.app ?? new AppStore(), {
            root: this,
        });
        this.user = Object.assign(provided.user ?? new UserStore(), {
            root: this,
        });
        this.expense = Object.assign(provided.expense ?? new ExpenseStore(), {
            root: this,
        });
        this.notification = Object.assign(provided.notification ?? new NotificationStore(), {
            root: this,
        });
    }
}

