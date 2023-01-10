import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';
import { useParams } from 'react-router-dom';

import { useStore } from '@/store';

const ExpenseItems = () => {
    const { expenseId } = useParams();
    const { expense } = useStore();    

    useEffect(() => {
        autorun(() => {
            expense.fetchExpenseItems(Number(expenseId));
        });
    }, []);

    const handleCreateExpenseItem = () => {
        expense.createExpenseItem(Number(expenseId), {
            date: '2023-01-01',
            amount: 15,
            currency: 'USD',
        });
    };

    return (
        <>
            <button onClick={() => handleCreateExpenseItem()}>TEST</button>
            <table>
                <thead>
                    <tr>
                        <th>id</th>
                        <th>date</th>
                        <th>amount</th>
                        <th>currency</th>
                    </tr>
                </thead>
                <tbody>
                    {expense.expenseItems.get(Number(expenseId))?.map((item) => (
                        <tr key={item.id}>
                        <td>{item.id}</td>
                        <td>{item.date}</td>
                        <td>{item.amount}</td>
                        <td>{item.currency}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </>
    );
};

export default observer(ExpenseItems);
