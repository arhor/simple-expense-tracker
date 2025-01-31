import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';
import { Navigate, useLocation, useSearchParams } from 'react-router-dom';

import { SignInForm } from '@/components';
import { useStore } from '@/store';

const SignIn = () => {
    const [ searchParams ] = useSearchParams();
    const { state } = useLocation() as { state: { doNotCallAuth?: boolean } | null };
    const { user } = useStore();

    if (searchParams.has('auth') && searchParams.get('auth') == 'success') {
        return (
            <Navigate to="/" />
        );
    } else if (state?.doNotCallAuth) {
        return (
            <SignInForm />
        );
    } else {
        useEffect(() => {
            autorun(() => {
                user.fetchData();
            });
        }, []);
    
        return user.authenticated ? (
            <Navigate to="/" />
        ) : (
            <SignInForm />
        );
    }
};

export default observer(SignIn);
