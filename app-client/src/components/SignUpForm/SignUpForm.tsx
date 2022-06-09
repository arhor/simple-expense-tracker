import { FormEvent } from 'react';

import log from 'loglevel';
import { Link as RouterLink, Navigate } from 'react-router-dom';

import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';

import { signUp } from '@/api/userClient';
import { UserDto } from '@/generated/UserDto';
import { useStore } from '@/store';
import { REG_EXP_EMAIL } from '@/utils/patterns';
import { defineValidator } from '@/utils/validation-utils';

const validator = defineValidator<UserDto>({
    email: [
        (v) => !!v
            || 'E-mail is required',
        (v) => (v && REG_EXP_EMAIL.test(v))
            || 'E-mail must be valid'
    ],
    password: [
        (v) => !!v
            || 'Password is required',
        (v) => (v && v.length <= 10)
            || 'Password must be less than 10 characters',
    ]
});

log.info(validator);

const SignUpForm = () => {
    const { user } = useStore();
    const orientationPortrait = useMediaQuery('(orientation: portrait)');

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const username = formData.get('username');
        const password = formData.get('password');

        if ((typeof username === 'string') && (typeof password === 'string')) {
            const response = await signUp(username, password);
            log.info(response);
        }
    };

    return user.authenticated ? (
        <Navigate to={{ pathname: '/' }} />
    ) : (
        <Box
            sx={{
                marginTop: orientationPortrait ? 20 : 0,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
            }}
        >
            <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                <LockOutlinedIcon />
            </Avatar>
            <Typography component="h1" variant="h5">
                Sign up
            </Typography>
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        <TextField
                            id="username"
                            name="username"
                            label="Username"
                            margin="normal"
                            required
                            fullWidth
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <TextField
                            id="password"
                            name="password"
                            type="password"
                            label="Password"
                            margin="normal"
                            required
                            fullWidth
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            {"Sign Up"}
                        </Button>
                    </Grid>
                    <Grid item>
                        <Link to="/sign-in" component={RouterLink} variant="body2">
                            {"Already have an account? Sign in"}
                        </Link>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
};

export default SignUpForm;
