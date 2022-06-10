import { useParams } from 'react-router';

const ExpenseUpdate = () => {
    const { id } = useParams();

    return (
        <div>
            {`update expense with id: ${id}`}
        </div>
    );
}

export default ExpenseUpdate;
