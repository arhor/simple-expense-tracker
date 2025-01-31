import { SnackbarProvider } from 'notistack';

import { ErrorBoundary } from '@/components';
import { AppRouter } from '@/router';
import { AppThemeProvider } from '@/theme';

export default function App() {
    return (
        <AppThemeProvider>
            <ErrorBoundary>
                <SnackbarProvider preventDuplicate>
                    <AppRouter />
                </SnackbarProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
