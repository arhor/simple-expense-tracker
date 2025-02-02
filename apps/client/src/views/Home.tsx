import { useState } from 'react';
import { useNavigate } from 'react-router';

import { observer } from 'mobx-react-lite';

import ExpenseCreateDialog from '@/components/ExpenseCreateDialog';
import ExpenseList from '@/components/ExpenseList';
import ExpenseUpdateDialog from '@/components/ExpenseUpdateDialog';
import { useStore } from '@/store';

const Home = observer(function Home() {
    const [ createDialogOpen, setCreateDialogOpen ] = useState(false);
    const [ updateDialogOpen, setUpdateDialogOpen ] = useState(false);
    const [ currentExpenseId, setCurrentExpenseId ] = useState<number | null>(null);
    
    const { expense } = useStore();

    const navigate = useNavigate();

    const handleCreate = () => {
        setCreateDialogOpen(true);
    };
    const handleCreateDialogSubmit = () => {
        setCreateDialogOpen(false);
        expense.fetchExpenses();
    };
    const handleCreateDialogCancel = () => {
        setCreateDialogOpen(false);
    };

    const handleUpdate = (expenseId: number) => {
        // setCurrentExpenseId(expenseId);
        // setUpdateDialogOpen(true);

        navigate(`/expenses/${expenseId}`);
    };
    const handleUpdateDialogSubmit = () => {
        setCurrentExpenseId(null);
        setUpdateDialogOpen(false);
        expense.fetchExpenses();
    };
    const handleUpdateDialogCancel = () => {
        setCurrentExpenseId(null);
        setUpdateDialogOpen(false);
    };

    return (
        <>
            <ExpenseList
                onCreate={handleCreate}
                onUpdate={handleUpdate}
            />
            <ExpenseCreateDialog
                open={createDialogOpen}
                onSubmit={handleCreateDialogSubmit}
                onCancel={handleCreateDialogCancel}
            />
            <ExpenseUpdateDialog
                id={currentExpenseId}
                open={updateDialogOpen}
                onSubmit={handleUpdateDialogSubmit}
                onCancel={handleUpdateDialogCancel}
            />
        </>
    );
})

export default Home;
