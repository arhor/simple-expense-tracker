import { lazy } from 'react';

import { BrowserRouter, Route, Routes } from 'react-router-dom';

import secured from '@/hocs/secured';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

const AppLayout = secured(lazy(() => import('@/AppLayout')));

const AppRouter = () => (
    <BrowserRouter>
        <Routes>
            <Route path="/*" element={<AppLayout />} />
            <Route path="/sign-in" element={<SignIn />} />
            <Route path="/sign-up" element={<SignUp />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
    </BrowserRouter>
);

export default AppRouter;
