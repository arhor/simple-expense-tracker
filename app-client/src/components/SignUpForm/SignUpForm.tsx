import { FormEvent, useState } from 'react';

import { observer } from 'mobx-react';
import { Link as RouterLink, Navigate, useNavigate } from 'react-router-dom';

import { LockOutlined } from '@mui/icons-material';
import { Avatar, Box, Button, Grid, Link, TextField, Typography } from '@mui/material';

import { UserRequestDTO } from '@/generated/UserRequestDTO';
import { useStore } from '@/store';
import { Optional } from '@/utils/core-utils';
import { defineValidator, formIsValid } from '@/utils/validation-utils';

const USERNAME_REG_EXP = /^.+$/;
const PASSWORD_REG_EXP = /^.+$/;

const validator = defineValidator<UserRequestDTO>({
    username: [
        (v) => !!v
            || 'Username is required',
        (v) => (v && v.length >= 4)
            || 'Username must have more than 3 characters',
        (v) => (v && v.length <= 20)
            || 'Username must have less than 21 character',
        (v) => (v && USERNAME_REG_EXP.test(v))
            || 'Username must be valid'
    ],
    password: [
        (v) => !!v
            || 'Password is required',
        (v) => (v && v.length >= 6)
            || 'Password must have more than 5 characters',
        (v) => (v && v.length <= 20)
            || 'Password must have less than 21 character',
        (v) => (v && PASSWORD_REG_EXP.test(v))
            || 'Password must be valid'
    ]
});

const SignUpForm = () => {
    const { user } = useStore();
    const navigate = useNavigate();
    const [ errors, setErrors ] = useState<Partial<UserRequestDTO>>({});

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const username = formData.get('username') as Optional<string>;
        const password = formData.get('password') as Optional<string>;

        const currentErrors = validator({ username, password });

        setErrors(currentErrors);

        if (formIsValid(errors)) {
            await user.signUp(
                username as string,
                password as string,
            );
            navigate('/');
        }
    };

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
                            error={Boolean(errors.username)}
                            helperText={errors.username}
                            required
                            fullWidth
                            sx={{ mb: 5 }}
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <TextField
                            id="password"
                            name="password"
                            type="password"
                            label="Password"
                            margin="normal"
                            error={Boolean(errors.password)}
                            helperText={errors.password}
                            required
                            fullWidth
                            sx={{ mb: 5 }}
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

export default observer(SignUpForm);
