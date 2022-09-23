import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react';
import { useNavigate } from 'react-router';

import ExpenseItemList from '@/components/ExpenseItemList';
import { useStore } from '@/store';

const Home = () => {
    const navigate = useNavigate();
    const { expense } = useStore();

    useEffect(() => {
        autorun(() => {
            expense.fetchExpenses();
        });
    }, []);

    const handleCreate = () => {
        navigate('/expenses');
    };
    const handleUpdate = (expenseId: number) => {
        navigate(`/expenses/${expenseId}`);
    };

    return (
        <ExpenseItemList
            expenses={expense.expenses}
            onCreate={handleCreate}
            onUpdate={handleUpdate}
        />
    );
};

export default observer(Home);
