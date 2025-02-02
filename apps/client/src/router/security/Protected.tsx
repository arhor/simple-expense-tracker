import { ComponentType } from 'react';

import { withProtection } from '@/router/security/withProtection';

type ProtectedProps = {
    component: ComponentType<any>;
    authorities?: string[];
} & Record<string, any>;

export default function Protected({ component, authorities, ...rest }: ProtectedProps) {
    const ProtectedComponent = withProtection(component, authorities ?? []);
    return <ProtectedComponent {...rest} />;
}
