import { ReactNode } from 'react';

import { store, StoreContext } from '@/store/Store';

export const StoreProvider = (props: { children: ReactNode }) => (
    <StoreContext.Provider value={store}>
        {props.children}
    </StoreContext.Provider>
);

