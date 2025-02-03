import { ComponentType, JSX, Suspense, useEffect, useState } from 'react';
import { Navigate } from 'react-router';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';

import Loader from '@/components/Loader';
import { useStore } from '@/store';
import { Optional } from '@/utils/core-utils';

const Loading = Loader;
const REACT_LAZY_TYPE = Symbol.for('react.lazy');

function determineNameOf<T extends JSX.IntrinsicAttributes>(component: ComponentType<T>) {
    return component.displayName
        || component.name
        || 'AnonymousComponent';
}
function authorized(authenticated: boolean | null | undefined, authorities: string[] | null | undefined, requiredAuthorities: string[]) {
    return authenticated
        && authorities
        && requiredAuthorities.every(auth => authorities.includes(auth));
}

export function withProtection<T extends JSX.IntrinsicAttributes>(Component: ComponentType<T>, authorities: string[]) {
    const ProtectedComponent = (props: T) => {
        const [loading, setLoading] = useState(true);
        const { user } = useStore();

        useEffect(() => autorun(() => {
            user.fetchData()
                .finally(() => {
                    setLoading(false);
                });
        }), []);

        if (loading) {
            return <Loader />;
        }
        return authorized(user.authenticated, user.authorities, authorities)
            ? <Component {...props} />
            : <Navigate to="/sign-in" />;
    };
    ProtectedComponent.displayName = `withProtection(${determineNameOf(Component)})`;
    return observer(ProtectedComponent);
}

export function secured<T extends JSX.IntrinsicAttributes>(
    WrappedComponent: ComponentType<T> & { $$typeof: Optional<symbol | number> },
): ComponentType<T> {
    const SecuredComponent = (props: T) => {
        const [loading, setLoading] = useState(true);
        const { user } = useStore();

        useEffect(() => autorun(() => {
            user.fetchData()
                .finally(() => {
                    setLoading(false);
                });
        }), []);

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
            return <Navigate to="/sign-in" state={{ doNotCallAuth: true }} />;
        }
    };
    SecuredComponent.displayName = `secured(${determineNameOf(WrappedComponent)})`;
    return observer(SecuredComponent);
}
