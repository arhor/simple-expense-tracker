import { Suspense,useEffect } from 'react';
import { Outlet } from 'react-router-dom';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';
import { useSnackbar } from 'notistack';

import Container from '@mui/material/Container';

import { Footer, Header, Loader } from '@/components';
import AppBar from '@/components/AppBar';
import AppNav from '@/components/AppNav';
import { useStore } from '@/store';

function Layout() {
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
            <Header />
            <Container component="main" sx={{ p: 5 }}>
                <Suspense fallback={<Loader />}>
                    <Outlet />
                </Suspense>
            </Container>
            <Footer />
            <AppNav />
        </>
    );
}

export default observer(Layout);
