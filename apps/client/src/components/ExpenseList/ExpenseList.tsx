import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';

import Grid from '@mui/material/Grid';

import Expense from '~/components/Expense';
import { useStore } from '~/store';

export type Props = {
    onCreate: () => void;
    onUpdate: (expenseId: number) => void;
};

const ExpenseList = ({ onCreate, onUpdate }: Props) => {
    const { expense } = useStore();

    useEffect(() => {
        autorun(() => {
            expense.fetchExpenses();
        });
    }, []);

    return (
        <Grid container spacing={{ xs: 3 }} columns={{ xs: 3, sm: 4 }}>
            {expense.expenses.map((expense) => (
                <Grid item xs={1} key={expense.id}>
                    <Expense {...expense} tooltip={'edit'} onClick={() => onUpdate(expense.id)} />
                </Grid>
            ))}
            <Grid item xs={1}>
                <Expense icon="add" name="new" onClick={onCreate} />
            </Grid>
        </Grid>
    );
};

export default observer(ExpenseList);
