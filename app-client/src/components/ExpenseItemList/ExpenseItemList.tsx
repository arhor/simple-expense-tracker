import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react';

import Grid from '@mui/material/Grid';

import ExpenseItem from '@/components/ExpenseItem';
import { useStore } from '@/store';

export type Props = {
    onCreate: () => void;
    onUpdate: (expenseId: number) => void;
};

const ExpenseItemList = ({ onCreate, onUpdate }: Props) => {
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
                    <ExpenseItem {...expense} tooltip={'edit'} onClick={() => onUpdate(expense.id)} />
                </Grid>
            ))}
            <Grid item xs={1}>
                <ExpenseItem icon="add" name="new" onClick={onCreate} />
            </Grid>
        </Grid>
    );
};

export default observer(ExpenseItemList);
