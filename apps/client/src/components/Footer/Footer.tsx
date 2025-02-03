import { useEffect, useState } from 'react';
import { Link as RouterLink, useLocation } from 'react-router-dom';

import BarChartIcon from '@mui/icons-material/BarChart';
import CurrencyExchangeIcon from '@mui/icons-material/CurrencyExchange';
import SettingsIcon from '@mui/icons-material/Settings';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import Paper from '@mui/material/Paper';

const buttons = [
    {
        icon: CurrencyExchangeIcon,
        path: '/',
    },
    {
        icon: BarChartIcon,
        path: '/reports',
    },
    {
        icon: SettingsIcon,
        path: '/settings',
    },
];

export default function Footer() {
    const { pathname } = useLocation();
    const [value, setValue] = useState<number>();

    useEffect(() => {
        setValue(buttons.findIndex(it => it.path == pathname));
    }, [pathname]);

    return (
        <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0 }} elevation={3}>
            <BottomNavigation value={value} onChange={(_, newValue) => setValue(newValue)}>
                {buttons.map(it => (
                    <BottomNavigationAction
                        to={it.path}
                        key={it.path}
                        icon={<it.icon />}
                        component={RouterLink}
                    />
                ))}
            </BottomNavigation>
        </Paper>
    );
}
