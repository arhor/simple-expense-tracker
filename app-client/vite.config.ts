import { fileURLToPath, URL } from 'url';
import dns from 'dns';

import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';
import eslint from 'vite-plugin-eslint';
import { VitePWA } from 'vite-plugin-pwa';

dns.setDefaultResultOrder('verbatim');

export default defineConfig(({ mode }) => {
    const rootProjectDir = fileURLToPath(new URL('..', import.meta.url));
    const variablePrefixes = [''];

    process.env = { ...loadEnv(mode, rootProjectDir, variablePrefixes) };

    return {
        plugins: [
            react(),
            eslint(),
            VitePWA({
                registerType: 'autoUpdate',
                workbox: {
                    globPatterns: [
                        '**/*',
                    ],
                },
                includeAssets: [
                    '**/*',
                ],
                manifest: {
                    theme_color: '#ffffff',
                    orientation: 'any',
                    icons: [
                        {
                            src: 'android-chrome-192x192.png',
                            type: 'image/png',
                            sizes: '192x192',
                        },
                        {
                            src: 'android-chrome-512x512.png',
                            type: 'image/png',
                            sizes: '512x512',
                        },
                        {
                            src: 'android-chrome-512x512.png',
                            type: 'image/png',
                            sizes: '512x512',
                            purpose: 'any',
                        },
                        {
                            src: 'android-chrome-512x512.png',
                            type: 'image/png',
                            sizes: '512x512',
                            purpose: 'maskable',
                        },
                    ],
                },
            }),
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('src', import.meta.url)),
            },
        },
        build: {
            rollupOptions: {
                output: {
                    manualChunks: {
                        'bundle-react': [
                            'react',
                            'react-dom',
                            'react-i18next',
                            'react-router',
                            'react-router-dom',
                        ],
                        'bundle-material-ui': [
                            '@emotion/react',
                            '@emotion/styled',
                            '@mui/material',
                            '@mui/icons-material',
                        ],
                        'bundle-mobx': [
                            'mobx',
                            'mobx-react-lite',
                        ],
                        'bundle-misc': [
                            'axios',
                            'i18next',
                            'i18next-http-backend',
                            'uuid',
                        ],
                    },
                },
            },
        },
        server: {
            proxy: {
                '^/api': {
                    target: process.env.API_BASE_URL || 'http://localhost:5000',
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
