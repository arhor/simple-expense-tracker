import { useState } from 'react';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';

import { ExpenseRequestDTO } from '~/generated/ExpenseRequestDTO';
import { defineValidator, formIsValid } from '~/utils/validation-utils';

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

export type Props = {
    id: number | null;
    open: boolean;
    onSubmit: () => void;
    onCancel: () => void;
};

const ExpenseUpdateDialog = ({ id, open, onSubmit, onCancel }: Props) => {
    const [ entity, setEntity ] = useState<Partial<ExpenseRequestDTO>>({});
    const [ errors, setErrors ] = useState<Partial<ExpenseRequestDTO>>({});    

    const clearState = () => {
        setEntity({});
        setErrors({});
    };
    const handleCreate = async () => {
        const currentErrors = validator(entity);

        if (formIsValid(currentErrors)) {
            clearState();
            onSubmit();
        } else {
            setErrors(currentErrors);
        }
    };

    return (
        <Dialog open={open} onClose={() => {clearState(); onCancel();}}>
            <DialogTitle>{`Update Expense ${id}`}</DialogTitle>
            <DialogContent>
                <Grid container justifyContent="center">
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
                            value={entity.name}
                            onChange={(e) => {
                                setEntity(prevState => {
                                    return {
                                        ...prevState,
                                        name: e.target.value
                                    };
                                });
                            }}
                        />
                        <TextField
                            id="icon"
                            name="icon"
                            label="icon"
                            margin="normal"
                            error={Boolean(errors.icon)}
                            helperText={errors.icon}
                            required
                            fullWidth
                            sx={{ mb: 5 }}
                            value={entity.icon}
                            onChange={(e) => {
                                setEntity(prevState => {
                                    return {
                                        ...prevState,
                                        icon: e.target.value
                                    };
                                });
                            }}
                        />
                        <TextField
                            id="color"
                            name="color"
                            label="color"
                            margin="normal"
                            error={Boolean(errors.color)}
                            helperText={errors.color}
                            required
                            fullWidth
                            sx={{ mb: 5 }}
                            value={entity.color}
                            onChange={(e) => {
                                setEntity(prevState => {
                                    return {
                                        ...prevState,
                                        color: e.target.value
                                    };
                                });
                            }}
                        />
                    </Grid>
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => {clearState(); onCancel();}}>Cancel</Button>
                <Button onClick={handleCreate}>Create</Button>
            </DialogActions>
        </Dialog>
    );
};

export default ExpenseUpdateDialog;
