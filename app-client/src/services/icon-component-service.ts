import { ComponentType } from 'react';

import AddIcon from '@mui/icons-material/Add';
import CheckroomIcon from '@mui/icons-material/Checkroom';
import CoffeeIcon from '@mui/icons-material/Coffee';
import FastfoodIcon from '@mui/icons-material/Fastfood';
import HomeIcon from '@mui/icons-material/Home';
import QuestionMarkIcon from '@mui/icons-material/QuestionMark';

import { Optional } from '@/utils/core-utils';

export const availableIcons: ReadonlyMap<string, ComponentType> = new Map([
    ['add', AddIcon],
    ['cloth', CheckroomIcon],
    ['coffee', CoffeeIcon],
    ['fastfood', FastfoodIcon],
    ['home', HomeIcon],
]);

export function getIconByName(iconName: Optional<string>): ComponentType {
    const IconComponent: Optional<ComponentType> =
        (iconName !== null && iconName !== undefined)
            ? availableIcons.get(iconName)
            : null;

    return IconComponent ?? QuestionMarkIcon;
}


