import useMediaQuery from '@mui/material/useMediaQuery';

export default function useOrientationTopMargin() {
    return useMediaQuery('(orientation: portrait)')
        ? 20
        : 10;
}
