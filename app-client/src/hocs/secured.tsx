import { ComponentType, Suspense, useEffect, useState } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';
import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import { useStore } from '@/store';
import { Optional } from '@/utils/core-utils';

const REACT_LAZY_TYPE = Symbol.for('react.lazy');

export default function secured<T extends JSX.IntrinsicAttributes>(
    WrappedComponent: ComponentType<T> & { $$typeof: Optional<symbol | number> },
): ComponentType<T> {
    const SecuredComponent = (props: T) => {
        const [ loading, setLoading ] = useState(true);
        const { user } = useStore();

        useEffect(() => {
            autorun(() => {
                user.fetchData()
                    .finally(() => {
                        setLoading(false);
                    });
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
    SecuredComponent.displayName = `secured(${
        WrappedComponent.displayName || WrappedComponent.name || 'Component'
    })`;
    return observer(SecuredComponent);
}
