import { ComponentStoryObj, ComponentMeta } from '@storybook/react';

import ExpenseCreateForm from '@/components/ExpenseCreateForm';
import { withMemoryRouter } from '@/utils/dev/router-utils';

export default {
    title: 'Library/ExpenseCreateForm',
    component: withMemoryRouter(ExpenseCreateForm),
} as ComponentMeta<typeof ExpenseCreateForm>;

export const Default: ComponentStoryObj<typeof ExpenseCreateForm> = {
    args: {},
};
