import { AxiosRequestConfig } from 'axios';
import log from 'loglevel';

const DEFAULT_TIMEOUT = 10_000;

const timeouts: ReadonlyMap<string, number> = new Map([
    ['/api/users/current', 5_000],
]);

export default function addRequestTimeout(config: AxiosRequestConfig): AxiosRequestConfig {
    const { timeout, ...restConfig } = config;
    const requestUrl = (config.baseURL + '/' + config.url).replace(/([^:]\/)\/+/g, "$1");
    const timeoutToUse = timeout || timeouts.get(requestUrl) || DEFAULT_TIMEOUT;

    log.debug('Using request timeout %s for the url: %s', timeoutToUse, requestUrl);

    return {
        timeout: timeoutToUse,
        ...restConfig,
    };
}
