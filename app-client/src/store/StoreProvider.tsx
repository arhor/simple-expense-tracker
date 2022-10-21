import { createContext, ReactNode, useContext } from 'react';

import { RootStore, Store } from '@/store/Store';

export const store = new RootStore();

export const StoreContext = createContext<Readonly<Store>>(store);

export const StoreProvider = (props: { children: ReactNode }) => (
    <StoreContext.Provider value={store}>
        {props.children}
    </StoreContext.Provider>
);

export function useStore() {
    return useContext(StoreContext);
}
