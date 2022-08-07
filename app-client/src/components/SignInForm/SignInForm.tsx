import { Link as RouterLink, Navigate } from 'react-router-dom';

import { GitHub, Google, LockOutlined } from '@mui/icons-material';
import { Avatar, Box, Button, Grid, IconButton, Link, Stack, TextField, Typography } from '@mui/material';

import { useStore } from '@/store';

const SignInForm = () => {
    const { user } = useStore();

    return user.authenticated ? (
        <Navigate to={{ pathname: '/' }} />
    ) : (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                height: '100vh',
            }}
        >
            <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                <LockOutlined />
            </Avatar>
            <Typography component="h1" variant="h5">
                Sign in
            </Typography>
            <Box component="form" action="/api/login" method="POST" noValidate sx={{ mt: 1 }}>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="username"
                            label="Username"
                            name="username"
                            autoComplete="username"
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            type="password"
                            id="password"
                            autoComplete="current-password"
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            Sign In
                        </Button>
                    </Grid>
                    <Grid item>
                        <Link to="/sign-up" component={RouterLink} variant="body2">
                            {"Don't have an account? Sign Up"}
                        </Link>
                    </Grid>
                </Grid>
            </Box>
            <Stack direction="row" alignItems="center" spacing={2} sx={{ padding: 2 }}>
                <IconButton href="/api/oauth2/authorization/github">
                    <GitHub />
                </IconButton>
                <IconButton href="/api/oauth2/authorization/google">
                    <Google />
                </IconButton>
            </Stack>
        </Box>
    );
};

export default SignInForm;
