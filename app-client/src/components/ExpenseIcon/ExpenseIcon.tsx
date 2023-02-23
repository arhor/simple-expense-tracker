import { getIconByName } from '~/services/icon-component-service';
import { Optional } from '~/utils/core-utils';

export type Props = {
    icon: Optional<string>;
};

// TODO: candidate for removal
const ExpenseIcon = (props: Props) => {
    const IconComponent = getIconByName(props.icon);
    return <IconComponent />;
};

export default ExpenseIcon;
