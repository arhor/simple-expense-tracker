import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react';
import { Navigate } from 'react-router-dom';

import SignUpForm from '@/components/SignUpForm';
import { useStore } from '@/store';

const SignUp = () => {
    const { user } = useStore();

    useEffect(() => {
        autorun(() => {
            user.fetchData();
        });
    }, []);

    return user.authenticated ? (
        <Navigate to={{ pathname: '/' }} />
    ) : (
        <SignUpForm />
    );
};

export default observer(SignUp);
