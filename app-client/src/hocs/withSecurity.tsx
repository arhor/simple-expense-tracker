import { ComponentType, useEffect, useState } from 'react';

import { observer } from 'mobx-react';
import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import { useStore } from '@/store';

export default function withSecurity<T>(WrappedComponent: ComponentType<T>): ComponentType<T> {
    const SecuredComponent = (props: T) => {
        const [ loading, setLoading ] = useState(true);
        const { user } = useStore();

        useEffect(() => {
            user.fetchData().finally(() => {
                setLoading(false);
            });
        }, []);

        if (loading) {
            return <Loading />;
        }
        return user.authenticated ? (
            <WrappedComponent {...props} />
        ) : (
            <Navigate to={{ pathname: '/sign-in' }} />
        );
    };
    SecuredComponent.displayName = `withSecurity(${
        WrappedComponent.displayName || WrappedComponent.name || 'Component'
    })`;
    return observer(SecuredComponent);
}
