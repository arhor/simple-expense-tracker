import { StrictMode } from 'react';

import ReactDOM from 'react-dom';

import App from '@/App';
import '@/config/i18n';
import '@/config/logging';
import { withOptional } from '@/utils/core-utils';

withOptional(document.getElementById('root'), (root) => {
    ReactDOM.render(
        <StrictMode>
            <App />
        </StrictMode>,
        root,
    );
});
