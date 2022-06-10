import Grid from '@mui/material/Grid';

import ExpenseItem from '@/components/ExpenseItem';
import { ExpenseResponseDTO } from '@/generated/ExpenseResponseDTO';

export type Props = {
    expenses: ExpenseResponseDTO[];
    onCreate: () => void;
    onUpdate: (expenseId: number) => void;
};

const ExpenseItemList = ({ expenses, onCreate, onUpdate }: Props) => (
    <Grid container spacing={{ xs: 3 }} columns={{ xs: 3, sm: 4 }}>
        {expenses.map((expense, index) => (
            <Grid item xs={1} key={index}>
                <ExpenseItem {...expense} tooltip={'edit'} onClick={() => onUpdate(expense.id)} />
            </Grid>
        ))}
        <Grid item xs={1}>
            <ExpenseItem icon="add" name="new" onClick={onCreate} />
        </Grid>
    </Grid>
);

export default ExpenseItemList;
