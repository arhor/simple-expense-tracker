import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';

import { availableIcons } from '@/services/icon-component-service';

export type Props = {
    onClick: (icon: string) => void;
};

const IconPicker = ({ onClick }: Props) => (
    <Grid container spacing={3} columns={4}>
        {Object.entries(availableIcons).map(([name, Icon], index) => (
            <Grid item xs={1} key={index}>
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    <IconButton size="large" sx={{ border: '1px solid' }} onClick={() => onClick(name)}>
                        <Icon />
                    </IconButton>
                </Box>
            </Grid>
        ))}
    </Grid>
);

export default IconPicker;
