import ExpenseStore from '@/store/expense';
import NotificationStore from '@/store/notification';
import UserStore from '@/store/user';

export type RootStore = {
    user: UserStore;
    expense: ExpenseStore;
    notification: NotificationStore;
};

export default class implements RootStore {
    user: UserStore;
    expense: ExpenseStore;
    notification: NotificationStore;

    constructor(provided: Partial<RootStore> = {}) {
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

