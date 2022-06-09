import { ComponentStoryObj, ComponentMeta } from '@storybook/react';

import ExpenseItem from '@/components/ExpenseItem';

export default {
    title: 'Library/ExpenseItem',
    component: ExpenseItem,
} as ComponentMeta<typeof ExpenseItem>;

export const Default: ComponentStoryObj<typeof ExpenseItem> = {
    args: {
        name: 'Miscellaneous',
        total: 10.15
    },
};

export const CoffeeExpense: ComponentStoryObj<typeof ExpenseItem> = {
    args: {
        icon: 'coffee',
        name: 'Coffee',
        total: 25.75,
        tooltip: 'tooltip',
    },
};
