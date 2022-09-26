import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react';
import { useSnackbar } from 'notistack';
import { Route, Routes } from 'react-router-dom';

import Container from '@mui/material/Container';

import AppBar from '@/components/AppBar';
import AppNav from '@/components/AppNav';
import { useStore } from '@/store';
import Expenses from '@/views/Expenses';
import Reports from  '@/views/Reports';
import Settings from  '@/views/Settings';

const AppLayout = () => {
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
                <Routes>
                    <Route index element={<Expenses />} />
                    <Route path="/reports" element={<Reports />} />
                    <Route path="/settings" element={<Settings />} />
                </Routes>
            </Container>
            <AppNav />
        </>
    );
};

export default observer(AppLayout);
