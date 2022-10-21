import { useState } from 'react';

import { Link as RouterLink } from 'react-router-dom';

import BarChartIcon from '@mui/icons-material/BarChart';
import CurrencyExchangeIcon from '@mui/icons-material/CurrencyExchange';
import SettingsIcon from '@mui/icons-material/Settings';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import Paper from '@mui/material/Paper';

const AppNav = () => {
    const [value, setValue] = useState(0);

    return (
        <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0 }} elevation={3}>
            <BottomNavigation value={value} onChange={(e, newValue) => { setValue(newValue); }}>
                <BottomNavigationAction
                    icon={<CurrencyExchangeIcon />}
                    component={RouterLink}
                    to="/"
                />
                <BottomNavigationAction
                    icon={<BarChartIcon />}
                    component={RouterLink}
                    to="/reports"
                />
                <BottomNavigationAction
                    icon={<SettingsIcon />}
                    component={RouterLink}
                    to="/settings"
                />
            </BottomNavigation>
        </Paper>
    );
};

export default AppNav;
