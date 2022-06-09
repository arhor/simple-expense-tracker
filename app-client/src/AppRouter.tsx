import { lazy, Suspense } from 'react';

import { BrowserRouter as Router, Outlet, Route, Routes } from 'react-router-dom';

import Loading from '@/components/Loading';
import withSecurity from '@/hocs/withSecurity';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

const AppLayout = withSecurity(lazy(() => import('@/AppLayout')));

const Home = lazy(() => import('@/views/Home'));
const Reports = lazy(() => import( '@/views/Reports'));
const Settings = lazy(() => import( '@/views/Settings'));

const AppRouter = () => (
    <Router>
        <Routes>
            <Route
                path="/"
                element={
                    <AppLayout>
                        <Suspense fallback={<Loading />}>
                            <Outlet />
                        </Suspense>
                    </AppLayout>
                }
            >
                <Route index element={<Home />} />
                <Route path="/reports" element={<Reports />} />
                <Route path="/settings" element={<Settings />} />
            </Route>
            <Route path="/sign-in" element={<SignIn />} />
            <Route path="/sign-up" element={<SignUp />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
    </Router>
);

export default AppRouter;
