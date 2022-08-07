import { ComponentStoryObj, ComponentMeta } from '@storybook/react';

import AppThemeProvider from '@/AppThemeProvider';
import ExpenseCreateForm from '@/components/ExpenseCreateForm';
import { withMemoryRouter } from '@/utils/dev/router-utils';

const ExpenseCreateFormWithAppThemeProvider = () => {
    return (
        <AppThemeProvider>
            <ExpenseCreateForm />
        </AppThemeProvider>
    );
};

export default {
    title: 'Library/ExpenseCreateForm',
    component: withMemoryRouter(ExpenseCreateFormWithAppThemeProvider),
} as ComponentMeta<typeof ExpenseCreateForm>;

export const Default: ComponentStoryObj<typeof ExpenseCreateForm> = {
    args: {},
};
