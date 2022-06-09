import { StrictMode } from 'react';

import { createRoot } from 'react-dom/client';

import App from '@/App';
import '@/config/i18n';
import '@/config/logging';

createRoot(appContainer('root')).render(
    <StrictMode>
        <App />
    </StrictMode>
);

function appContainer(id: string): HTMLElement {
    let container = document.getElementById(id);
    if (container == null) {
        container = document.createElement('div');
        container.id = id;
        document.body.appendChild(container);
    }
    return container;
}
