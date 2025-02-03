import { useEffect } from 'react';

import { autorun } from 'mobx';
import { observer } from 'mobx-react-lite';

import IconButton from '@mui/material/IconButton';
import Stack from '@mui/material/Stack';

import { getIconByName } from '@/services/icon-component-service';
import { useStore } from '@/store';

const AuthProviderList = observer(() => {
    const { app } = useStore();

    useEffect(() => {
        autorun(() => {
            app.fetchAuthProviders();
        });
    }, [app]);

    return (
        <Stack direction="row" alignItems="center" spacing={2} sx={{ padding: 2 }}>
            {app.authProviders.map((authProvider) => {
                const IconComponent = getIconByName(authProvider.name)

                return (
                    <IconButton href={authProvider.href} key={authProvider.name}>
                        <IconComponent />
                    </IconButton>
                );
            })}
        </Stack>
    );
});

export default AuthProviderList;
