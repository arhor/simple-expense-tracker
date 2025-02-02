import { Component, ErrorInfo, ReactNode } from 'react';
import { Translation } from 'react-i18next';

import { StatelessWidget } from '@/components';

const DEFAULT_TITLE = 'Ups, something went wrong...';
const DEFAULT_DESCRIPTION = 'Please, contact system administrator if you have nothing else to do';

export type Props = {
    children: ReactNode;
}

export type State = {
    error: Error | null;
    errorInfo: ErrorInfo | null;
};

// eslint-disable-next-line no-unused-vars
class ErrorBoundaryWithTranslation extends Component<Props & { t: (arg0: string) => string }, State> {
    state = {
        error: null,
        errorInfo: null,
    } as State;

    static getDerivedStateFromError(error: Error) {
        return { error };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        this.setState({ error, errorInfo });
    }

    render() {
        const { error, errorInfo } = this.state;
        const { t } = this.props;

        if (errorInfo) {
            // eslint-disable-next-line no-undef
            const [title, description] = process?.env?.NODE_ENV === 'development'
                ? [error?.toString() ?? DEFAULT_TITLE, errorInfo.componentStack ?? DEFAULT_DESCRIPTION]
                : [t(DEFAULT_TITLE), t(DEFAULT_DESCRIPTION)];

            return <StatelessWidget title={title} description={description} />;
        }
        return this.props.children;
    }
}

export default function ErrorBoundary(props: Props) {
    return (
        <Translation>
            {
                (t) => (
                    <ErrorBoundaryWithTranslation t={t}>
                        {props.children}
                    </ErrorBoundaryWithTranslation>
                )
            }
        </Translation>
    );
}
