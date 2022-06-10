import { Optional, PartialOptional } from '@/utils/core-utils';

export type ValidationRule<T> = (v: Optional<T>) => true | string;

export type ValidationRules<T> = Partial<{ [P in keyof T]: ValidationRule<T[P]>[] }>;

export type ValidationErrors<T> = Partial<{ [P in keyof T]: string }>;

export type ValidationFunction<T> = (values: PartialOptional<T>) => ValidationErrors<T>;

export function defineValidator<T>(rules: ValidationRules<T>): ValidationFunction<T> {
    return (values: PartialOptional<T>) => {
        const errors: ValidationErrors<T> = {};

        if (values) {
            for (const field in values) {
                const fieldRules = rules[field] as Optional<ValidationRule<T[keyof T]>[]>;

                if (fieldRules) {
                    for (const rule of fieldRules) {
                        const value = values[field];
                        const result = rule(value);

                        if (result !== true) {
                            errors[field] = result;
                            break;
                        }
                    }
                }
            }
        }
        return errors;
    };
}
