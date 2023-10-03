import { observer } from 'mobx-react-lite';
import { useTranslation } from 'react-i18next';

import MUIAppBar from '@mui/material/AppBar';
import { styled } from '@mui/material/styles';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography'

import { useStore } from '~/store';

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
