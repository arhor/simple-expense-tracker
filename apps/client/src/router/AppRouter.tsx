import { createBrowserRouter, RouterProvider } from 'react-router';

import { Layout } from '@/components';
import Protected from '@/router/security/Protected';
import { NotFound, SignIn, SignUp } from '@/views';

const router = createBrowserRouter([
    {
        element: <Protected component={Layout} />,
        children: [
            {
                index: true,
                lazy: () => import('@/views/Home').then(it => ({ Component: it.default })),
            }
        ],
    },
    {
        path: '/sign-in',
        element: <SignIn />,
    },
    {
        path: '/sign-up',
        element: <SignUp />,
    },
    {
        path: '*',
        element: <NotFound />,
    },
]);

export default function AppRouter() {
    return <RouterProvider router={router} />;
}

if (import.meta.hot) {
    import.meta.hot.dispose(() => router.dispose());
}
