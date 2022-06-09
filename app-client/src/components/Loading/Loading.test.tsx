import { render } from '@testing-library/react';

import Loading from '@/components/Loading';

describe('Loading component', () => {
    test('should render without crashing', () => {
        render(<Loading />);
    });
});

