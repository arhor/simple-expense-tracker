import Box from '@mui/material/Box';
import Fade from '@mui/material/Fade';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';

import ExpenseIcon from '@/components/ExpenseIcon';

// TODO: consider non-optional props consistent with model
export type Props = {
    icon?: string;
    name?: string;
    total?: number;
    currency?: string;
    tooltip?: string;
    onClick?: () => void;
};

const Expense = ({ icon, name, total, currency, tooltip, onClick }: Props) => (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="caption">{name}</Typography>
        <Tooltip title={tooltip ?? ''} placement="top" TransitionComponent={Fade}>
            <IconButton size="large" sx={{ background: '#00cc00' }} onClick={onClick}>
                <ExpenseIcon icon={icon} />
            </IconButton>
        </Tooltip>
        <Typography variant="caption">{total} {currency}</Typography>
    </Box>
);

export default Expense;
