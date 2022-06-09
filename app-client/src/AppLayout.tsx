import { ReactNode, useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react';
import { useSnackbar } from 'notistack';

import Container from '@mui/material/Container';

import AppBar from '@/components/AppBar';
import AppNav from '@/components/AppNav';
import { useStore } from '@/store';

export type Props = {
    children: ReactNode
};

const AppLayout = ({ children }: Props) => {
    const { notification } = useStore();
    const { enqueueSnackbar } = useSnackbar();

    useEffect(() => {
        autorun(() => {
            const enqueued = notification.items.map((item) => enqueueSnackbar(item.message, {
                key: item.id,
                variant: item.level,
                autoHideDuration: item.timeout,
            }));
            notification.remove(enqueued);
        });
    }, []);

    return (
        <>
            <AppBar />
            <Container component="main" maxWidth="sm" sx={{ p: 5 }}>
                {children}
            </Container>
            <AppNav />
        </>
    );
};

export default observer(AppLayout);
