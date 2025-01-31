import { AxiosRequestConfig } from 'axios';
import * as uuid from 'uuid';

export const CSRF_TOKEN = uuid.v4();

const SAFE_METHODS: Readonly<Record<string, boolean>> = {
    GET: true,
    HEAD: true,
    OPTIONS: true,
    TRACE: true,
};

export default function addCsrfToken(config: AxiosRequestConfig): AxiosRequestConfig {
    const requestMethod = config.method?.toUpperCase();

    if (requestMethod && SAFE_METHODS[requestMethod]) {
        return config;
    }

    const { headers = {}, ...restConfig } = config;

    return {
        headers: {
            ...headers,
            'X-XSRF-TOKEN': CSRF_TOKEN,
        },
        ...restConfig,
    };
}
