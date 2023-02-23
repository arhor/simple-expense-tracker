import { forwardRef, ReactElement, Ref, useState } from 'react';

import { observer } from 'mobx-react-lite';
import { useTranslation } from 'react-i18next';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid';
import Slide from '@mui/material/Slide';
import TextField from '@mui/material/TextField';
import { TransitionProps } from '@mui/material/transitions';

import { ExpenseRequestDTO } from '~/generated/ExpenseRequestDTO';
import { useStore } from '~/store';
import { defineValidator, formIsValid } from '~/utils/validation-utils';

const Transition = forwardRef(function Transition(
    props: TransitionProps & {
        children: ReactElement;
    },
    ref: Ref<unknown>,
) {
    return <Slide direction="up" ref={ref} {...props} />;
});

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
    open: boolean;
    onSubmit: () => void;
    onCancel: () => void;
};

const ExpenseCreateDialog = ({ open, onSubmit, onCancel }: Props) => {
    const [ entity, setEntity ] = useState({ name: '', icon: '', color: '' });
    const [ errors, setErrors ] = useState({} as Partial<ExpenseRequestDTO>);

    const { t } = useTranslation();
    const { expense } = useStore();

    const fields: readonly { field: keyof ExpenseRequestDTO, label: string }[] = [
        {
            field: 'name',
            label: t('name'),
        },
        {
            field: 'icon',
            label: t('icon'),
        },
        {
            field: 'color',
            label: t('color'),
        },
    ];

    const clearState = () => {
        setEntity({ name: '', icon: '', color: '' });
        setErrors({});
    };
    const handleSubmit = async () => {
        const currentErrors = validator(entity);

        if (formIsValid(currentErrors)) {
            await expense.createExpense(entity as ExpenseRequestDTO);
            clearState();
            onSubmit();
        } else {
            setErrors(currentErrors);
        }
    };
    const handleCancel = () => {
        clearState();
        onCancel();
    };

    return (
        <Dialog open={open} onClose={handleCancel} TransitionComponent={Transition}>
            <DialogTitle>{t('Create Expense')}</DialogTitle>
            <DialogContent>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        {fields.map(({field, label}) => (
                            <TextField
                                id={field}
                                key={field}
                                name={field}
                                label={label}
                                margin="normal"
                                error={Boolean(errors[field])}
                                helperText={errors[field]}
                                required
                                fullWidth
                                sx={{ mb: 5 }}
                                value={entity[field]}
                                onChange={(e) => setEntity(state => ({ ...state, [field]: e.target.value })) }
                            />
                        ))}
                    </Grid>
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleCancel}>{t('cancel')}</Button>
                <Button onClick={handleSubmit}>{t('submit')}</Button>
            </DialogActions>
        </Dialog>
    );
};

export default observer(ExpenseCreateDialog);
