import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';
import { Navigate, useLocation } from 'react-router-dom';

import SignUpForm from '@/components/SignUpForm';
import { useStore } from '@/store';

const SignUp = () => {
    const { state } = useLocation() as { state: { doNotCallAuth?: boolean } | null };
    const { user } = useStore();

    if (state?.doNotCallAuth) {
        return (
            <SignUpForm />
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
            <SignUpForm />
        );
    }
};

export default observer(SignUp);
