import axios from 'axios';

import addCsrfToken from '@/api/interceptors/addCsrfToken';
import addRequestTimeout from '@/api/interceptors/addRequestTimeout';
import { pipe } from '@/utils/function-utils';

const client = axios.create({
    baseURL: '/api',
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Cache': 'no-cache',
    },
});

client.interceptors.request.use(
    pipe(
        addCsrfToken,
        addRequestTimeout,
    )
);

export default client;
