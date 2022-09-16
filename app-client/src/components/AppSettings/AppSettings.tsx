import { observer } from 'mobx-react';
import { useTranslation } from 'react-i18next';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import FormControlLabel from '@mui/material/FormControlLabel';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import { useTheme } from '@mui/material/styles';
import Switch from '@mui/material/Switch';
import Typography from '@mui/material/Typography';

import { useAppThemeControl } from '@/AppThemeProvider';

const AppSettings = () => {
    const theme = useTheme();
    const { t } = useTranslation();
    const { toggleColorMode } = useAppThemeControl();

    return (
        <Grid container justifyContent="center">
            <Grid item xs={8}>
                <Paper
                    sx={{
                        marginTop: 8,
                        padding: 5,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Typography component="h1" variant="h5">
                        Settings
                    </Typography>
                    <Box sx={{ mt: 1 }}>
                        <Grid container>
                            <Grid item xs={12}>
                                <FormControlLabel
                                    label="Dark mode"
                                    control={
                                        <Switch
                                            checked={theme.palette.mode === 'dark'}
                                            onChange={toggleColorMode}
                                        />
                                    }
                                />
                            </Grid>
                        </Grid>
                        <Button color="inherit" href="/api/sign-out">
                            {t('Sign-Out')}
                        </Button>
                    </Box>
                </Paper>
            </Grid>
        </Grid>
    );
};

export default observer(AppSettings);
