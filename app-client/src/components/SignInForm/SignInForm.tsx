import { Link as RouterLink, useSearchParams } from 'react-router-dom';

import GitHubIcon from '@mui/icons-material/GitHub';
import GoogleIcon from '@mui/icons-material/Google';    
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

const SignInForm = () => {
    const [ searchParams ] = useSearchParams();
    const hasError = searchParams.has('error');

    return (
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
                <LockOutlinedIcon />
            </Avatar>
            <Typography component="h1" variant="h5">
                Sign in
            </Typography>
            <Box component="form" action="/api/sign-in" method="POST" noValidate sx={{ mt: 1 }}>
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
                            error={hasError}
                            helperText={hasError ? 'Incorrect Username or Password' : undefined}
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
                    <GitHubIcon />
                </IconButton>
                <IconButton href="/api/oauth2/authorization/google">
                    <GoogleIcon />
                </IconButton>
            </Stack>
        </Box>
    );
};

export default SignInForm;
