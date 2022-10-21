import i18n from 'i18next';
import httpBackend from 'i18next-http-backend';
import log from 'loglevel';
import { initReactI18next } from 'react-i18next';

i18n.use(httpBackend)
    .use(initReactI18next)
    .init({
        lng: 'en',
        fallbackLng: 'en',
        load: 'languageOnly',
        preload: ['en'],
        interpolation: {
            escapeValue: false,
        },
        react: {
            useSuspense: true,
        },
    })
    .then(() => {
        log.info('Localization module loaded successfully');
    })
    .catch((e) => {
        log.error('Localization module loading failure', e);
    });

export default i18n;
