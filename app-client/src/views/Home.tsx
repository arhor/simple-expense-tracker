import { useEffect, useState } from 'react';

import { observer } from 'mobx-react';

import ExpenseList from '@/components/ExpenseList';
import Loading from '@/components/Loading';
import { useStore } from '@/store';

const Home = () => {
    const [ loading, setLoading ] = useState(true);
    const { expense } = useStore();

    useEffect(() => {
        expense.fetchData().finally(() => {
            setLoading(false);
        });
    }, []);

    const handleCreate = () => {
        /* no-op */
    };
    const handleUpdate = (expenseId: number) => {
        /* no-op */
    };

    return loading
        ? <Loading />
        : <ExpenseList expenses={expense.items} onCreate={handleCreate} onUpdate={handleUpdate} />;
};

export default observer(Home);
