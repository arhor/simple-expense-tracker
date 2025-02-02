import { vi, describe, test } from 'vitest';
import { render } from 'vitest-browser-react'

import { Loader } from '@/components';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (arg: string) => arg,
        i18n: {
            changeLanguage: () => new Promise(() => { }),
        },
    }),
}));

describe('Loader component', () => {
    test('should render without crashing', () => {
        render(<Loader />);
    });
});
