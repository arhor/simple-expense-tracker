import { Box } from '@mui/material';
import Fade from '@mui/material/Fade';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';

import CustomIcon from '@/components/CustomIcon';
import { doNothing } from '@/utils/function-utils';

export type Props = {
    icon?: string;
    name?: string;
    total?: number;
    tooltip?: string;
    onClick?: () => void;
};

const ExpenseItem = ({ icon, name, total, tooltip, onClick = doNothing }: Props) => (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="caption">{name}</Typography>
        <Tooltip title={tooltip ?? ''} placement="top" TransitionComponent={Fade}>
            <IconButton size="large" sx={{ background: '#00cc00' }} onClick={onClick}>
                <CustomIcon icon={icon} />
            </IconButton>
        </Tooltip>
        <Typography variant="caption">{total}</Typography>
    </Box>
);

export default ExpenseItem;
