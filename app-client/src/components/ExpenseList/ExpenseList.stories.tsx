import { ComponentStoryObj, ComponentMeta } from '@storybook/react';

import log from 'loglevel';

import ExpenseList from '@/components/ExpenseList';

log.setLevel('INFO');

export default {
    title: 'Library/ExpenseList',
    component: ExpenseList,
} as ComponentMeta<typeof ExpenseList>;

export const Default: ComponentStoryObj<typeof ExpenseList> = {
    args: {
        expenses: [
            {
                id: 1,
                name: 'Miscellaneous',
                total: 10.15,
            },
            {
                id: 2,
                icon: 'coffee',
                name: 'Coffee',
                total: 25.75
            },
            {
                id: 3,
                icon: 'cloth',
                name: 'Clothing',
                total: 130.53
            },
            {
                id: 4,
                icon: 'fastfood',
                name: 'Junk food',
                total: 31.99
            },
            {
                id: 5,
                icon: 'home',
                name: 'Household',
                total: 379
            },
        ],
        onCreate: () => {
            log.info(`Action 'CREATE' triggered`);
        },
        onUpdate: (expenseId) => {
            log.info(`Action 'UPDATE' triggered with id: ${expenseId}`);
        },
    },
};
