import { ComponentStoryObj, ComponentMeta } from '@storybook/react';
import log from 'loglevel';

import IconPicker from '@/components/IconPicker';

log.setLevel('INFO');

export default {
    title: 'Library/IconPicker',
    component: IconPicker,
} as ComponentMeta<typeof IconPicker>;

export const Default: ComponentStoryObj<typeof IconPicker> = {
    args: {
        onClick: (icon) => {
            log.info(`Icon '${icon}' selected`);
        },
    },
};
