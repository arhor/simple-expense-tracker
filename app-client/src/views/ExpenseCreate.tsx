import { observer } from 'mobx-react';

import Box from '@mui/material/Box';

import { useStore } from '@/store';

const ExpenseCreate = () => {
    const { expense } = useStore();

    return (
        <Box>
            {'create new expense'}
        </Box>
    );
}

export default observer(ExpenseCreate);
