import { FormEvent, useState } from 'react';

import { observer } from 'mobx-react';
import { useNavigate } from 'react-router-dom';

import { Box, Button, Grid, IconButton, TextField } from '@mui/material';

import { ExpenseRequestDTO } from '@/generated/ExpenseRequestDTO';
import { getIconByName } from '@/services/icon-component-service';
import { useStore } from '@/store';
import { Optional } from '@/utils/core-utils';
import { defineValidator, formIsValid } from '@/utils/validation-utils';

const validator = defineValidator<ExpenseRequestDTO>({
    name: [
        (v) => !!v
            || 'Name is required',
        (v) => (v && v.length >= 3)
            || 'Name length should not be less than 3 characters',
        (v) => (v && v.length <= 20)
            || 'Name length should not be more than 20 characters',
    ],
});

type IconButtonColor = 'inherit' | 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';

const ExpenseCreateForm = () => {
    const { expense } = useStore();
    const navigate = useNavigate();
    const [ errors, setErrors ] = useState<Partial<ExpenseRequestDTO>>({});
    const [ icon, setIcon ] = useState<string>();
    const [ color, setColor ] = useState<IconButtonColor>();

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const name = formData.get('name') as Optional<string>;

        const currentErrors = validator({ name, icon, color });

        setErrors(currentErrors);

        if (formIsValid(errors)) {
            await expense.createExpense({ 
                name: name as string,
                icon: icon,
                color: color as string | undefined,
            });
            navigate('/');
        }
    };

    const IconComponent = getIconByName(icon);

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
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        <IconButton size="large" color={color} style={{
                            border: '1px solid',
                        }}>
                            <IconComponent />
                        </IconButton>
                    </Grid>
                    <Grid item xs={10}>
                        <TextField
                            id="name"
                            name="name"
                            label="name"
                            margin="normal"
                            error={Boolean(errors.name)}
                            helperText={errors.name}
                            required
                            fullWidth
                            sx={{ mb: 5 }}
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
                            {'Save'}
                        </Button>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
};

export default observer(ExpenseCreateForm);
