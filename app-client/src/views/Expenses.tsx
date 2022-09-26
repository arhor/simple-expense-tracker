import { useState } from 'react';

import { observer } from 'mobx-react';

import ExpenseCreateDialog from '@/components/ExpenseCreateDialog';
import ExpenseItemList from '@/components/ExpenseItemList';
import ExpenseUpdateDialog from '@/components/ExpenseUpdateDialog';
import { useStore } from '@/store';

const Home = () => {
    const [ createDialogOpen, setCreateDialogOpen ] = useState(false);
    const [ updateDialogOpen, setUpdateDialogOpen ] = useState(false);
    const [ currentExpenseId, setCurrentExpenseId ] = useState<number | null>(null);
    
    const { expense } = useStore();

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
        setCurrentExpenseId(expenseId);
        setUpdateDialogOpen(true);
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
            <ExpenseItemList
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
};

export default observer(Home);
