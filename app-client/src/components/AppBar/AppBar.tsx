import { observer } from 'mobx-react';
import { useTranslation } from 'react-i18next';

import { AppBar as MUIAppBar, Toolbar, Typography } from '@mui/material';
import { styled } from '@mui/material/styles';

import { useStore } from '@/store';

const Offset = styled('div')(({ theme }) => theme.mixins.toolbar);

const AppBar = () => {
    const { t } = useTranslation();
    const { expense } = useStore();

    return (
        <>
            <MUIAppBar position="fixed">
                <Toolbar>
                    <Typography>
                        {t('Total Expenses')}: {expense.totalAmount}
                    </Typography>
                </Toolbar>
            </MUIAppBar>
            <Offset />
        </>
    );
};

export default observer(AppBar);
