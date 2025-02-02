import { ReactNode, useMemo, useState } from 'react';

import CssBaseline from '@mui/material/CssBaseline';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

import { AppThemeControlContext } from '@/theme/AppThemeControlContext';

export default function AppThemeProvider(props: { children: ReactNode }) {
    const [colorMode, setColorMode] = useState<'light' | 'dark'>();
    const darkThemePreferred = useMediaQuery('(prefers-color-scheme: dark)');

    const theme = useMemo(() => createTheme({
        palette: {
            mode: colorMode ?? determineColorMode(darkThemePreferred),
        },
    }), [colorMode, darkThemePreferred]);

    const switchColorMode = () => {
        setColorMode((prev) => determineColorMode(prev === 'light'));
    };

    return (
        <AppThemeControlContext.Provider value={{ switchColorMode }}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                {props.children}
            </ThemeProvider>
        </AppThemeControlContext.Provider>
    );
}

function determineColorMode(shouldUseDarkTheme: boolean) {
    return shouldUseDarkTheme ? 'dark' : 'light';
}
