import { ComponentStoryObj, ComponentMeta } from '@storybook/react';

import ExpenseCreateForm from '@/components/ExpenseCreateForm';

export default {
    title: 'Library/ExpenseCreateForm',
    component: ExpenseCreateForm,
} as ComponentMeta<typeof ExpenseCreateForm>;

export const Default: ComponentStoryObj<typeof ExpenseCreateForm> = {
    args: {},
};
