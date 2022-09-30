import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';
import { Navigate } from 'react-router';
import { useSearchParams } from 'react-router-dom';

import SignInForm from '@/components/SignInForm';
import { useStore } from '@/store';

const SignIn = () => {
    const [ searchParams ] = useSearchParams();
    const { user } = useStore();

    if (searchParams.has('success')) {
        return (
            <Navigate to={{ pathname: '/' }} />
        );
    } else {
        useEffect(() => {
            autorun(() => {
                user.fetchData();
            });
        }, []);
    
        return user.authenticated ? (
            <Navigate to={{ pathname: '/' }} />
        ) : (
            <SignInForm />
        );
    }    
};

export default observer(SignIn);
