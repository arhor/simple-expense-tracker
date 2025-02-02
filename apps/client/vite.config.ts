import react from '@vitejs/plugin-react';
import dns from 'dns';
import { fileURLToPath, URL } from 'url';
import { defineConfig, loadEnv } from 'vite';
import { VitePWA } from 'vite-plugin-pwa';

dns.setDefaultResultOrder('verbatim');

export default defineConfig(({ mode }) => {
    const rootProjectDir = fileURLToPath(new URL('..', import.meta.url));
    const variablePrefixes = [''];

    process.env = { ...loadEnv(mode, rootProjectDir, variablePrefixes) };

    return {
        plugins: [
            react(),
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
        server: {
            proxy: {
                '^/(api)': {
                    target: 'http://localhost:5000',
                    changeOrigin: true,
                },
            },
        },
        build: {
            rollupOptions: {
                output: {
                    manualChunks: {
                        react: [
                            'react',
                            'react-dom',
                            'react-router',
                            'react-router-dom',
                        ],
                        material: [
                            '@emotion/cache',
                            '@emotion/react',
                            '@emotion/styled',
                            '@mui/icons-material',
                            '@mui/material',
                        ],
                    },
                },
            },
        },
        test: {
            browser: {
                enabled: true,
                provider: 'playwright',
                instances: [
                    { browser: 'chromium' },
                ],
            },
        },
    }
});
