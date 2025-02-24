import { useTranslation } from 'react-i18next';

import { observer } from 'mobx-react-lite';

import AppBar from '@mui/material/AppBar';
import FormControlLabel from '@mui/material/FormControlLabel';
import { styled, useTheme } from '@mui/material/styles';
import Switch from '@mui/material/Switch';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';

import { useAppThemeControl } from '@/theme';

const Offset = styled('div')(({ theme }) => theme.mixins.toolbar);

const Header = observer(() => {
    const theme = useTheme();
    const { t } = useTranslation();
    const { switchColorMode } = useAppThemeControl();

    return (
        <>
            <AppBar position="fixed">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        {t('Netflix DGS + Apollo Client (React)')}
                    </Typography>
                    <FormControlLabel
                        label="Dark mode"
                        control={
                            <Switch
                                checked={theme.palette.mode === 'dark'}
                                onChange={switchColorMode}
                            />
                        }
                    />
                </Toolbar>
            </AppBar>
            <Offset />
        </>
    );
});

export default Header;
