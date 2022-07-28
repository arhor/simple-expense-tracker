export function notEmptyArray<T>(items: T[]): boolean {
    return !isEmptyArray(items);
}

export function isEmptyArray<T>(items: T): boolean {
    return Array.isArray(items) && items.length === 0;
}

export function commaSeparate<T>(items?: T[] | null): string {
    return items?.join(', ') ?? '';
}
