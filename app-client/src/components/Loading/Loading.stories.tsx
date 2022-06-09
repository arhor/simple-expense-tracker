import { ComponentStoryObj } from '@storybook/react';

import Loading from '@/components/Loading';

export default {
    title: 'Library/Loading',
    component: Loading,
};

export const Default: ComponentStoryObj<typeof Loading> = {
    args: {},
};
