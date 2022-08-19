import { ComponentType, Suspense, useEffect, useState } from 'react';

import { observer } from 'mobx-react';
import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import { useStore } from '@/store';
import { Optional } from '@/utils/core-utils';

const REACT_LAZY_TYPE = Symbol.for('react.lazy');

export default function withSecurity<T>(
    WrappedComponent: ComponentType<T> & { $$typeof: Optional<symbol | number> },
): ComponentType<T> {
    const SecuredComponent = (props: T) => {
        const [loading, setLoading] = useState(true);
        const { user } = useStore();

        useEffect(() => {
            user.fetchData().finally(() => {
                setLoading(false);
            });
        }, []);

        if (loading) {
            return <Loading />;
        }

        if (user.authenticated) {
            if (WrappedComponent.$$typeof === REACT_LAZY_TYPE) {
                return (
                    <Suspense fallback={<Loading />}>
                        <WrappedComponent {...props} />
                    </Suspense>
                );
            } else {
                return <WrappedComponent {...props} />;
            }
        } else {
            return <Navigate to={{ pathname: '/sign-in' }} />;
        }
    };
    SecuredComponent.displayName = `withSecurity(${
        WrappedComponent.displayName || WrappedComponent.name || 'Component'
    })`;
    return observer(SecuredComponent);
}
