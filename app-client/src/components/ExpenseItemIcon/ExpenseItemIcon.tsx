import { ComponentType } from 'react';

import AddIcon from '@mui/icons-material/Add';
import CheckroomIcon from '@mui/icons-material/Checkroom';
import CoffeeIcon from '@mui/icons-material/Coffee';
import FastfoodIcon from '@mui/icons-material/Fastfood';
import HomeIcon from '@mui/icons-material/Home';
import QuestionMarkIcon from '@mui/icons-material/QuestionMark';

import { Optional } from '@/utils/core-utils';

export const availableIcons: Map<Optional<string>, ComponentType> = new Map([
    ['add', AddIcon],
    ['cloth', CheckroomIcon],
    ['coffee', CoffeeIcon],
    ['fastfood', FastfoodIcon],
    ['home', HomeIcon],
]);

export type Props = {
    icon: Optional<string>;
};

const ExpenseItemIcon = (props: Props) => {
    const IconComponent = availableIcons.get(props.icon) ?? QuestionMarkIcon;
    return <IconComponent />;
};

export default ExpenseItemIcon;
