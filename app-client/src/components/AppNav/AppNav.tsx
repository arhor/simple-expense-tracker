import { useState } from 'react';

import { Link as RouterLink } from 'react-router-dom';

import { BarChart, CurrencyExchange, Settings } from '@mui/icons-material';
import { BottomNavigation, BottomNavigationAction, Paper } from '@mui/material';

const AppNav = () => {
    const [value, setValue] = useState(0);

    return (
        <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0 }} elevation={3}>
            <BottomNavigation value={value} onChange={(e, newValue) => { setValue(newValue); }}>
                <BottomNavigationAction
                    icon={<CurrencyExchange />}
                    component={RouterLink}
                    to="/"
                />
                <BottomNavigationAction
                    icon={<BarChart />}
                    component={RouterLink}
                    to="/reports"
                />
                <BottomNavigationAction
                    icon={<Settings />}
                    component={RouterLink}
                    to="/settings"
                />
            </BottomNavigation>
        </Paper>
    );
};

export default AppNav;
