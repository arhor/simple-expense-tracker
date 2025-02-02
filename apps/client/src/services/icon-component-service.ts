import { ComponentType } from 'react';

import AddIcon from '@mui/icons-material/Add';
import CheckroomIcon from '@mui/icons-material/Checkroom';
import CoffeeIcon from '@mui/icons-material/Coffee';
import FastfoodIcon from '@mui/icons-material/Fastfood';
import GitHubIcon from '@mui/icons-material/GitHub';
import GoogleIcon from '@mui/icons-material/Google';  
import HomeIcon from '@mui/icons-material/Home';
import QuestionMarkIcon from '@mui/icons-material/QuestionMark';

import { Optional } from '@/utils/core-utils';

export const availableIcons = Object.freeze<{ [name: string]: ComponentType }>({
    add: AddIcon, // TODO: 'add' icon should not be available to select as expense icon
    cloth: CheckroomIcon,
    coffee: CoffeeIcon,
    fastfood: FastfoodIcon,
    home: HomeIcon,
    question: QuestionMarkIcon,
    github: GitHubIcon,
    google: GoogleIcon,
});

export function getIconByName(iconName: Optional<string>): ComponentType {
    const IconComponent: Optional<ComponentType> =
        (iconName !== null && iconName !== undefined)
            ? availableIcons[iconName]
            : null;

    return IconComponent ?? QuestionMarkIcon;
}
