import { fileURLToPath, URL } from 'url';
import dns from 'dns';

import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';
import eslint from 'vite-plugin-eslint';

dns.setDefaultResultOrder('verbatim');

export default defineConfig(({ mode }) => {
    const rootProjectDir = fileURLToPath(new URL('..', import.meta.url));
    const variablePrefixes = [''];

    process.env = { ...loadEnv(mode, rootProjectDir, variablePrefixes) };

    return {
        plugins: [react(), eslint()],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('src', import.meta.url)),
            },
        },
        server: {
            proxy: {
                '^/api|oauth2': {
                    target: process.env.API_BASE_URL,
                    changeOrigin: true,
                },
            },
        },
        test: {
            globals: true,
            environment: 'jsdom',
            watch: false,
            setupFiles: ['src/tests.setup.ts'],
        },
    }
});
