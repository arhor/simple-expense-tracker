import { InternalAxiosRequestConfig } from 'axios';
import * as uuid from 'uuid';

const SAFE_METHODS = new Set([
    'GET',
    'HEAD',
    'OPTIONS',
    'TRACE',
]);

export const CSRF_TOKEN = uuid.v4();

export default function addCsrfToken(config: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
    const method = config.method?.toUpperCase();

    if (!method || SAFE_METHODS.has(method)) {
        config.headers.set('X-XSRF-TOKEN', CSRF_TOKEN);
    }
    return config;
}
