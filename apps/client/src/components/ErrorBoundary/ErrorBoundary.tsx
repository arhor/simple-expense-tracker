import { Component, ErrorInfo, ReactNode } from 'react';
import { Translation } from 'react-i18next';

import log from 'loglevel';

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
            log.error(error?.toString(), errorInfo.componentStack);
            return (
                <StatelessWidget
                    title={t(DEFAULT_TITLE)}
                    description={t(DEFAULT_DESCRIPTION)}
                />
            );
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
