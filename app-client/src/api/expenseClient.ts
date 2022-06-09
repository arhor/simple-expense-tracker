import client from '@/api/client.js';
import { ExpenseDTO } from '@/generated/ExpenseDTO';

export async function getCurrentUserExpenses(): Promise<ExpenseDTO[]> {
    const { data } = await client.get('/expenses');
    return data;
}
