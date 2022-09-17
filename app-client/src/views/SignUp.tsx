import { useEffect } from 'react';

import { observer } from 'mobx-react';
import { Navigate } from 'react-router';

import SignUpForm from '@/components/SignUpForm';
import { useStore } from '@/store';

const SignUp = () => {
    const { user } = useStore();

    useEffect(() => {
        user.fetchData();
    }, []);

    return user.authenticated ? (
        <Navigate to={{ pathname: '/' }} />
    ) : (
        <SignUpForm />
    );
};

export default observer(SignUp);
