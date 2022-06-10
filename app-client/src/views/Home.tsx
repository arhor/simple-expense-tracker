import { useEffect, useState } from 'react';

import { observer } from 'mobx-react';
import { useNavigate } from 'react-router';

import ExpenseItemList from '@/components/ExpenseItemList';
import Loading from '@/components/Loading';
import { useStore } from '@/store';

const Home = () => {
    const [ loading, setLoading ] = useState(true);
    const { expense } = useStore();
    const navigate = useNavigate();

    useEffect(() => {
        expense.fetchData().finally(() => {
            setLoading(false);
        });
    }, []);

    const handleCreate = () => {
        navigate('/expenses');
    };
    const handleUpdate = (expenseId: number) => {
        navigate(`/expenses/${expenseId}`);
    };

    return loading
        ? <Loading />
        : <ExpenseItemList expenses={expense.items} onCreate={handleCreate} onUpdate={handleUpdate} />;
};

export default observer(Home);
