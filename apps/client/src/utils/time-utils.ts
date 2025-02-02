function int(num: number) {
    return num | 0;
}

export function secondsToMillis(num: number) {
    return int(int(num) * int(1000));
}

export const MILLIS_IN_1_SECOND = secondsToMillis(1);
export const MILLIS_IN_5_SECONDS = secondsToMillis(5);
export const MILLIS_IN_10_SECONDS = secondsToMillis(10);
