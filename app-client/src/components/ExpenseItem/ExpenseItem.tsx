import { Box, Fade, IconButton, Tooltip, Typography } from '@mui/material';

import ExpenseItemIcon from '@/components/ExpenseItemIcon';

// TODO: consider non-optional props consistent with model
export type Props = {
    icon?: string;
    name?: string;
    total?: number;
    currency?: string;
    tooltip?: string;
    onClick?: () => void;
};

const ExpenseItem = ({ icon, name, total, currency, tooltip, onClick }: Props) => (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="caption">{name}</Typography>
        <Tooltip title={tooltip ?? ''} placement="top" TransitionComponent={Fade}>
            <IconButton size="large" sx={{ background: '#00cc00' }} onClick={onClick}>
                <ExpenseItemIcon icon={icon} />
            </IconButton>
        </Tooltip>
        <Typography variant="caption">{total} {currency}</Typography>
    </Box>
);

export default ExpenseItem;
